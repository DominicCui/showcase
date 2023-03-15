#include <map>
#include <shared_mutex>
#include <string>
#include <chrono>
#include <mutex>
#include <thread>
#include <errno.h>
#include <iostream>
#include <cstdio>
#include <fstream>
#include <getopt.h>
#include <sys/stat.h>
#include <grpcpp/grpcpp.h>
#include <dirent.h>

#include "src/dfslibx-service-runner.h"
#include "dfslib-shared-p2.h"
#include "proto-src/dfs-service.grpc.pb.h"
#include "src/dfslibx-call-data.h"
#include "dfslib-servernode-p2.h"

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
using dfs_service::FileList;
using dfs_service::FileStatus;

//
// STUDENT INSTRUCTION:
//
// Change these "using" aliases to the specific
// message types you are using in your `dfs-service.proto` file
// to indicate a file request and a listing of files from the server
//
using FileRequestType = Files;
using FileListResponseType = FileList;

extern dfs_log_level_e DFS_LOG_LEVEL;

std:: map<std::string, std::string> fileAccessMap;

//
// STUDENT INSTRUCTION:
//
// As with Part 1, the DFSServiceImpl is the implementation service for the rpc methods
// and message types you defined in your `dfs-service.proto` file.
//
// You may start with your Part 1 implementations of each service method.
//
// Elements to consider for Part 2:
//
// - How will you implement the write lock at the server level?
// - How will you keep track of which client has a write lock for a file?
//      - Note that we've provided a preset client_id in DFSClientNode that generates
//        a client id for you. You can pass that to the server to identify the current client.
// - How will you release the write lock?
// - How will you handle a store request for a client that doesn't have a write lock?
// - When matching files to determine similarity, you should use the `file_checksum` method we've provided.
//      - Both the client and server have a pre-made `crc_table` variable to speed things up.
//      - Use the `file_checksum` method to compare two files, similar to the following:
//
//          std::uint32_t server_crc = dfs_file_checksum(filepath, &this->crc_table);
//
//      - Hint: as the crc checksum is a simple integer, you can pass it around inside your message types.
//
class DFSServiceImpl final :
    public DFSService::WithAsyncMethod_CallbackList<DFSService::Service>,
        public DFSCallDataManager<FileRequestType , FileListResponseType> {

private:

    /** The runner service used to start the service and manage asynchronicity **/
    DFSServiceRunner<FileRequestType, FileListResponseType> runner;

    /** Mutex for managing the queue requests **/
    std::mutex queue_mutex;

    /** The mount path for the server **/
    std::string mount_path;

    /** The vector of queued tags used to manage asynchronous requests **/
    std::vector<QueueRequest<FileRequestType, FileListResponseType>> queued_tags;


    /**
     * Prepend the mount path to the filename.
     *
     * @param filepath
     * @return
     */
    const std::string WrapPath(const std::string &filepath) {
        return this->mount_path + filepath;
    }

    /** CRC Table kept in memory for faster calculations **/
    CRC::Table<std::uint32_t, 32> crc_table;

public:

    DFSServiceImpl(const std::string& mount_path, const std::string& server_address, int num_async_threads):
        mount_path(mount_path), crc_table(CRC::CRC_32()) {

        this->runner.SetService(this);
        this->runner.SetAddress(server_address);
        this->runner.SetNumThreads(num_async_threads);
        this->runner.SetQueuedRequestsCallback([&]{ this->ProcessQueuedRequests(); });

    }

    ~DFSServiceImpl() {
        this->runner.Shutdown();
    }

    void Run() {
        this->runner.Run();
    }

    /**
     * Request callback for asynchronous requests
     *
     * This method is called by the DFSCallData class during
     * an asynchronous request call from the client.
     *
     * Students should not need to adjust this.
     *
     * @param context
     * @param request
     * @param response
     * @param cq
     * @param tag
     */
    void RequestCallback(grpc::ServerContext* context,
                         FileRequestType* request,
                         grpc::ServerAsyncResponseWriter<FileListResponseType>* response,
                         grpc::ServerCompletionQueue* cq,
                         void* tag) {

        std::lock_guard<std::mutex> lock(queue_mutex);
        this->queued_tags.emplace_back(context, request, response, cq, tag);

    }

    /**
     * Process a callback request
     *
     * This method is called by the DFSCallData class when
     * a requested callback can be processed. You should use this method
     * to manage the CallbackList RPC call and respond as needed.
     *
     * See the STUDENT INSTRUCTION for more details.
     *
     * @param context
     * @param request
     * @param response
     */
    void ProcessCallback(ServerContext* context, FileRequestType* request, FileListResponseType* response) {

        //
        // STUDENT INSTRUCTION:
        //
        // You should add your code here to respond to any CallbackList requests from a client.
        // This function is called each time an asynchronous request is made from the client.
        //
        // The client should receive a list of files or modifications that represent the changes this service
        // is aware of. The client will then need to make the appropriate calls based on those changes.
        //

        // https://www.w3schools.blog/check-if-directory-exists-cpp
    
        // struct stat check;
        // if (stat(mount_path.c_str(), &check) == -1) { return Status(StatusCode::NOT_FOUND, "Not Found"); }
        
        // https://www.ibm.com/docs/en/zos/2.4.0?topic=functions-opendir-open-directory
        DIR *directory = opendir(mount_path.c_str());
        // Result results;

        struct dirent *entry;
        struct stat info;
        std::string file;
        std::string filepath;
        while ((entry = readdir(directory)) != NULL){
            // if (context->IsCancelled()) {
            //     return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
            // }
            file = entry->d_name;
            filepath = WrapPath(file);

            if((stat(filepath.c_str(), &info) == 0)){
                if(S_ISREG(info.st_mode)){
                    FileStatus *fstatus = response->add_fstatus();

                    fstatus->set_name(file);
                    fstatus->set_size(info.st_size);
                    int ctime = info.st_ctime;
                    fstatus->set_ctime(ctime); //error: cannot convert ‘timespec’ to ‘google::protobuf::int64’ {aka ‘long int’}
                    int mtime = info.st_mtime;
                    fstatus->set_mtime(mtime);
                    fstatus->set_checksum(dfs_file_checksum(filepath, &this->crc_table));
                    // std::cout << results.name() << "-" << results.mtime() << std::endl;
                    // writer->Write(results);
                }
            }
        }
        closedir(directory);
        
        // return Status::OK;
    }

    /**
     * Processes the queued requests in the queue thread
     */
    void ProcessQueuedRequests() {
        while(true) {

            //
            // STUDENT INSTRUCTION:
            //
            // You should add any synchronization mechanisms you may need here in
            // addition to the queue management. For example, modified files checks.
            //
            // Note: you will need to leave the basic queue structure as-is, but you
            // may add any additional code you feel is necessary.
            //


            // Guarded section for queue
            {
                dfs_log(LL_DEBUG2) << "Waiting for queue guard";
                std::lock_guard<std::mutex> lock(queue_mutex);


                for(QueueRequest<FileRequestType, FileListResponseType>& queue_request : this->queued_tags) {
                    this->RequestCallbackList(queue_request.context, queue_request.request,
                        queue_request.response, queue_request.cq, queue_request.cq, queue_request.tag);
                    queue_request.finished = true;
                }

                // any finished tags first
                this->queued_tags.erase(std::remove_if(
                    this->queued_tags.begin(),
                    this->queued_tags.end(),
                    [](QueueRequest<FileRequestType, FileListResponseType>& queue_request) { return queue_request.finished; }
                ), this->queued_tags.end());

            }
        }
    }

    //
    // STUDENT INSTRUCTION:
    //
    // Add your additional code here, including
    // the implementations of your rpc protocol methods.
    //

    Status WriteLock(ServerContext* context, const Files* file, Result* Result) override {
        std::string clientID = file->clientid();
        std::string filename = file->name();
        
        if(fileAccessMap.find(filename) == fileAccessMap.end()){
            fileAccessMap.insert(std::pair<std::string, std::string>(filename, clientID)); // Error: need pair<> to identify
        }
        
        if(fileAccessMap[filename] != clientID){
            return Status(StatusCode::RESOURCE_EXHAUSTED, "Requested file being accessed by another client");
        }          
        std::cout<< "lock" << std::endl;
        return Status::OK;
    }

    Status StoreFile(ServerContext* context, ServerReader<FileContent>* reader, Result* result) override{
        // std::cout<< "store start" << std::endl;
        FileContent fileContent;
        reader->Read(&fileContent);
        std::string filename = fileContent.name();
        std::string clientID = fileContent.clientid();
        int checksum = fileContent.checksum();

        if(filename.empty()){ fileAccessMap.erase(filename); return Status(StatusCode::CANCELLED, "CANCELLED"); } 

        if(fileAccessMap.find(filename) == fileAccessMap.end() || fileAccessMap[filename] != clientID){
            fileAccessMap.erase(filename);
            // std::cout<< "no lock" << std::endl;
            return Status(StatusCode::RESOURCE_EXHAUSTED, "No authority");
        }

        std::string filepath = WrapPath(filename);
        int server_checksum = dfs_file_checksum(filepath, &this->crc_table);
        std::cout<< server_checksum << std::endl;

        if(access(filepath.c_str(), F_OK) != -1 && checksum == server_checksum){ 
            fileAccessMap.erase(filename);
            // std::cout<< filepath << " same file" << std::endl;
            return Status(StatusCode::ALREADY_EXISTS, "Server have same copy of file");
        }
        
        std::ofstream fileStream;
        fileStream.open(filepath);
        // std::cout<< "transfer" << std::endl;

        while (reader->Read(&fileContent)){
            if (context->IsCancelled()) {
                return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
            }
            fileStream << fileContent.content();
        }
        fileStream.close();
        fileAccessMap.erase(filename);

        return Status::OK;
    }

    Status FetchFile(ServerContext* context, const Files* file, ServerWriter<FileContent>* writer) override{
        std::string filepath = WrapPath(file->name());
        // std::cout << mount_path << " " << filepath << std::endl;
        if(access(filepath.c_str(), F_OK ) == -1 ){ return Status(StatusCode::NOT_FOUND, "Not Found"); }
        
        FileContent content;
        content.set_name(file->name());
        content.set_checksum(dfs_file_checksum(filepath, &this->crc_table));
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
        if(access(filepath.c_str(), F_OK ) == -1 ){ 
            fileAccessMap.erase(file->name());
            return Status(StatusCode::NOT_FOUND, "Not Found"); 
        }

        if (context->IsCancelled()) {
            return Status(StatusCode::DEADLINE_EXCEEDED, "Deadline exceeded or Client cancelled, abandoning.");
        }
        remove(filepath.c_str());
        fileAccessMap.erase(file->name());

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
        // std::cout << file->name() << " " << file_stat.st_size << " " << file_stat.st_mtime << std::endl;
        return Status::OK;
    }

};

//
// STUDENT INSTRUCTION:
//
// The following three methods are part of the basic DFSServerNode
// structure. You may add additional methods or change these slightly
// to add additional startup/shutdown routines inside, but be aware that
// the basic structure should stay the same as the testing environment
// will be expected this structure.
//
/**
 * The main server node constructor
 *
 * @param mount_path
 */
DFSServerNode::DFSServerNode(const std::string &server_address,
        const std::string &mount_path,
        int num_async_threads,
        std::function<void()> callback) :
        server_address(server_address),
        mount_path(mount_path),
        num_async_threads(num_async_threads),
        grader_callback(callback) {}
/**
 * Server shutdown
 */
DFSServerNode::~DFSServerNode() noexcept {
    dfs_log(LL_SYSINFO) << "DFSServerNode shutting down";
}

/**
 * Start the DFSServerNode server
 */
void DFSServerNode::Start() {
    DFSServiceImpl service(this->mount_path, this->server_address, this->num_async_threads);


    dfs_log(LL_SYSINFO) << "DFSServerNode server listening on " << this->server_address;
    service.Run();
}

//
// STUDENT INSTRUCTION:
//
// Add your additional definitions here
//
