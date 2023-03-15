#include <chrono>
#include <string>
#include <cstdio>
#include <thread>
#include <map>
#include <iostream>
#include <fstream>
#include <errno.h>
#include <dirent.h>
#include <sys/stat.h>
#include <getopt.h>
#include <grpcpp/grpcpp.h>
#include <unistd.h>

#include "src/dfs-utils.h"
#include "dfslib-servernode-p1.h"
#include "proto-src/dfs-service.grpc.pb.h"
#include "dfslib-shared-p1.h"

using grpc::Status;
using grpc::Server;
using grpc::StatusCode;
using grpc::ServerReader;
using grpc::ServerWriter;
using grpc::ServerContext;
using grpc::ServerBuilder;

using dfs_service::DFSService;
using dfs_service::FileContent;
using dfs_service::Result;
using dfs_service::Files;
using dfs_service::NoArgs;
using dfs_service::FileStatus;

//
// STUDENT INSTRUCTION:
//
// DFSServiceImpl is the implementation service for the rpc methods
// and message types you defined in the `dfs-service.proto` file.
//
// You should add your definition overrides here for the specific
// methods that you created for your GRPC service protocol. The
// gRPC tutorial described in the readme is a good place to get started
// when trying to understand how to implement this class.
//
// The method signatures generated can be found in `proto-src/dfs-service.grpc.pb.h` file.
//
// Look for the following section:
//
//      class Service : public ::grpc::Service {
//
// The methods returning grpc::Status are the methods you'll want to override.
//
// In C++, you'll want to use the `override` directive as well. For example,
// if you have a service method named MyMethod that takes a MyMessageType
// and a ServerWriter, you'll want to override it similar to the following:
//
//      Status MyMethod(ServerContext* context,
//                      const MyMessageType* request,
//                      ServerWriter<MySegmentType> *writer) override {
//
//          /** code implementation here **/
//      }
//
class DFSServiceImpl final : public DFSService::Service {

private:

    /** The mount path for the server **/
    std::string mount_path;

    /**
     * Prepend the mount path to the filename.
     *
     * @param filepath
     * @return
     */
    const std::string WrapPath(const std::string &filepath) {
        return this->mount_path + filepath;
    }


public:

    DFSServiceImpl(const std::string &mount_path): mount_path(mount_path) {
    }

    ~DFSServiceImpl() {}

    //
    // STUDENT INSTRUCTION:
    //
    // Add your additional code here, including
    // implementations of your protocol service methods
    //
    Status StoreFile(ServerContext* context, ServerReader<FileContent>* reader, Result* result) override{
        FileContent fileContent;
        reader->Read(&fileContent);
        if(fileContent.name().empty()){ return Status(StatusCode::CANCELLED, "CANCELLED"); } 
        std::string filepath = WrapPath(fileContent.name());

        std::ofstream fileStream;
        fileStream.open(filepath);

         while (reader->Read(&fileContent)){
            if (context->IsCancelled()) {
                return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
            }
            fileStream << fileContent.content();
        }
        fileStream.close();

        return Status::OK;
    }


    Status FetchFile(ServerContext* context, const Files* file, ServerWriter<FileContent>* writer) override{
        std::string filepath = WrapPath(file->name());
        // std::cout << mount_path << " " << filepath << std::endl;
        if(access(filepath.c_str(), F_OK ) == -1 ){ return Status(StatusCode::NOT_FOUND, "Not Found"); }
        
        FileContent content;
        content.set_name(file->name());
        writer->Write(content);

        struct stat file_stat;
        stat (filepath.c_str(), &file_stat);
        int file_size = file_stat.st_size;
        std::ifstream fileStream(filepath);

        int read_bytes = 0;
        int sent_bytes = 0;
        int BUFFER_SIZE = 2048;
        char buffer[BUFFER_SIZE];

        while(sent_bytes < file_size) {
            if (context->IsCancelled()) {
                return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
            }
            read_bytes = (file_size - sent_bytes) < BUFFER_SIZE ? (file_size - sent_bytes) : BUFFER_SIZE;
            fileStream.read(buffer, read_bytes);
            content.set_content(buffer, read_bytes);
            writer->Write(content);
            sent_bytes += read_bytes;
        }
        fileStream.close();
        std::cout << "finish" << std::endl;
        return Status::OK;
    }


    Status DeleteFile(ServerContext* context, const Files* file, Result* result) override{
        std::string filepath = WrapPath(file->name());
        if(access(filepath.c_str(), F_OK ) == -1 ){ return Status(StatusCode::NOT_FOUND, "Not Found"); }
        if (context->IsCancelled()) {
            return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
        }
        remove(filepath.c_str());
        return Status::OK;
    }       


    Status FileStats(ServerContext* context, const Files* file, ServerWriter<FileStatus>* writer) override{
        std::string filepath = WrapPath(file->name());
        if(access(filepath.c_str(), F_OK ) == -1 ){ 
            std::cout << "Not Found" << std::endl;
            return Status(StatusCode::NOT_FOUND, "Not Found"); 
        }

        FileStatus fstatus;
        struct stat file_stat;
        stat(filepath.c_str(), &file_stat);
        fstatus.set_name(file->name());
        fstatus.set_size(file_stat.st_size);
        int ctime = file_stat.st_ctime;
        fstatus.set_ctime(ctime); //error: cannot convert ‘timespec’ to ‘google::protobuf::int64’ {aka ‘long int’}
        int mtime = file_stat.st_mtime;
        fstatus.set_mtime(mtime);
        writer->Write(fstatus);

        if (context->IsCancelled()) {
            return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
        }
        std::cout << file->name() << " " << file_stat.st_size << " " << file_stat.st_mtime << std::endl;
        return Status::OK;
    }


    Status ListAll(ServerContext *context, const NoArgs *args, ServerWriter<Result>* writer) override {
        // https://www.w3schools.blog/check-if-directory-exists-cpp
        struct stat check;
        if (stat(mount_path.c_str(), &check) == -1) { return Status(StatusCode::NOT_FOUND, "Not Found"); }
        
        // https://www.ibm.com/docs/en/zos/2.4.0?topic=functions-opendir-open-directory
        DIR *directory = opendir(mount_path.c_str());
        Result results;

        struct dirent *entry;
        struct stat info;
        std::string file;
        std::string filepath;
        while ((entry = readdir(directory)) != NULL){
            if (context->IsCancelled()) {
                return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
            }
            file = entry->d_name;
            filepath = WrapPath(file);

            if((stat(filepath.c_str(), &info) == 0)){
                if(S_ISREG(info.st_mode)){
                    results.set_name(file);
                    int mtime = info.st_mtime;
                    results.set_mtime(mtime);
                    // std::cout << results.name() << "-" << results.mtime() << std::endl;
                    writer->Write(results);
                }
            }
        }
        closedir(directory);
        
        return Status::OK;
    }
};

//
// STUDENT INSTRUCTION:
//
// The following three methods are part of the basic DFSServerNode
// structure. You may add additional methods or change these slightly,
// but be aware that the testing environment is expecting these three
// methods as-is.
//
/**
 * The main server node constructor
 *
 * @param server_address
 * @param mount_path
 */
DFSServerNode::DFSServerNode(const std::string &server_address,
        const std::string &mount_path,
        std::function<void()> callback) :
    server_address(server_address), mount_path(mount_path), grader_callback(callback) {}

/**
 * Server shutdown
 */
DFSServerNode::~DFSServerNode() noexcept {
    dfs_log(LL_SYSINFO) << "DFSServerNode shutting down";
    this->server->Shutdown();
}

/** Server start **/
void DFSServerNode::Start() {
    DFSServiceImpl service(this->mount_path);
    ServerBuilder builder;
    builder.AddListeningPort(this->server_address, grpc::InsecureServerCredentials());
    builder.RegisterService(&service);
    this->server = builder.BuildAndStart();
    dfs_log(LL_SYSINFO) << "DFSServerNode server listening on " << this->server_address;
    this->server->Wait();
}


//
// STUDENT INSTRUCTION:
//
// Add your additional DFSServerNode definitions here
//

