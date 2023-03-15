#include <regex>
#include <vector>
#include <string>
#include <cstdio>
#include <chrono>
#include <errno.h>
#include <iostream>
#include <thread>
#include <sstream>
#include <fstream>
#include <iomanip>
#include <getopt.h>
#include <unistd.h>
#include <limits.h>
#include <csignal>
#include <sys/inotify.h>
#include <grpcpp/grpcpp.h>

#include "dfslib-shared-p1.h"
#include "proto-src/dfs-service.grpc.pb.h"
#include "dfslib-clientnode-p1.h"

using grpc::Channel;
using grpc::Status;
using grpc::StatusCode;
using grpc::ClientWriter;
using grpc::ClientReader;
using grpc::ClientContext;

using dfs_service::FileContent;
using dfs_service::Result;
using dfs_service::Files;
using dfs_service::NoArgs;
using dfs_service::FileStatus;
using dfs_service::DFSService;

//
// STUDENT INSTRUCTION:
//
// You may want to add aliases to your namespaced service methods here.
// All of the methods will be under the `dfs_service` namespace.
//
// For example, if you have a method named MyMethod, add
// the following:
//
//      using dfs_service::MyMethod
//


DFSClientNodeP1::DFSClientNodeP1() : DFSClientNode() {}

DFSClientNodeP1::~DFSClientNodeP1() noexcept {}

StatusCode DFSClientNodeP1::Store(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to store a file here. This method should
    // connect to your gRPC service implementation method
    // that can accept and store a file.
    //
    // When working with files in gRPC you'll need to stream
    // the file contents, so consider the use of gRPC's ClientWriter.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::NOT_FOUND - if the file cannot be found on the client
    // StatusCode::CANCELLED otherwise
    //
    //
    std::string filepath = WrapPath(filename);
    // https://zhuanlan.zhihu.com/p/180501394
    if(access(filepath.c_str(), F_OK) == -1){ return StatusCode::NOT_FOUND; } 
    // std::cout << "Not Found" << std::endl;
    std::cout << mount_path << " " << filepath << std::endl;

    ClientContext context;
    // std::chrono::time_point deadline = std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout); 
    //missing template arguments before ‘deadline’
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    // https://grpc.io/docs/languages/cpp/basics/
    Result result;
    std::unique_ptr<ClientWriter<FileContent>> writer(service_stub->StoreFile(&context, &result));

    FileContent content;
    content.set_name(filename);
    writer->Write(content);

    struct stat file_stat;
    stat (filepath.c_str(), &file_stat);
    int length = file_stat.st_size;
    std::ifstream fileStream(filepath); 
    // https://www.w3schools.com/cpp/cpp_files.asp#:~:text=C%2B%2B%20Files%201%20C%2B%2B%20Files%20The%20fstream%20library,class%2C%20and%20the%20name%20of%20the%20file.%20
    int read_bytes = 0;
    int sent_bytes = 0;
    int BUFFER_SIZE = 2048;
    char buffer[BUFFER_SIZE];

    while(sent_bytes < length) {
        read_bytes = (length - sent_bytes) < BUFFER_SIZE ? (length - sent_bytes) : BUFFER_SIZE;
        fileStream.read(buffer, read_bytes);
        content.set_content(buffer, read_bytes);
        writer->Write(content);
        sent_bytes += read_bytes;
    }
    fileStream.close();
    writer->WritesDone();
    Status status = writer->Finish();

    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; }

    return StatusCode::OK;
}


StatusCode DFSClientNodeP1::Fetch(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to fetch a file here. This method should
    // connect to your gRPC service implementation method
    // that can accept a file request and return the contents
    // of a file from the service.
    //
    // As with the store function, you'll need to stream the
    // contents, so consider the use of gRPC's ClientReader.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::NOT_FOUND - if the file cannot be found on the server
    // StatusCode::CANCELLED otherwise
    //
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    Files file;
    file.set_name(filename);
    std::unique_ptr<ClientReader<FileContent>> reader(service_stub->FetchFile(&context, file));
    
    FileContent fileContent;
    reader->Read(&fileContent);
    if(fileContent.name().empty()){ return StatusCode::NOT_FOUND; } // std::cout << "Not Found" << std::endl;
    
    std::ofstream fileStream;
    std::string filepath = WrapPath(filename);
    fileStream.open(filepath);
        
    while (reader->Read(&fileContent)){
        fileStream << fileContent.content();
    }
    fileStream.close();
    // reader->ReadsDone(); // no memmber ReadsDone
    Status status = reader->Finish();

    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; }


    return StatusCode::OK;

}

StatusCode DFSClientNodeP1::Delete(const std::string& filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to delete a file here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::NOT_FOUND - if the file cannot be found on the server
    // StatusCode::CANCELLED otherwise
    //
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    Files file;
    file.set_name(filename);
    Result result;
    Status status = service_stub->DeleteFile(&context, file, &result);
    
    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; }
    if (status.error_code() == 5) { return StatusCode::NOT_FOUND; }

    return StatusCode::OK;

}


StatusCode DFSClientNodeP1::Stat(const std::string &filename, void* file_status) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to get the status of a file here. This method should
    // retrieve the status of a file on the server. Note that you won't be
    // tested on this method, but you will most likely find that you need
    // a way to get the status of a file in order to synchronize later.
    //
    // The status might include data such as name, size, mtime, crc, etc.
    //
    // The file_status is left as a void* so that you can use it to pass
    // a message type that you defined. For instance, may want to use that message
    // type after calling Stat from another method.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::NOT_FOUND - if the file cannot be found on the server
    // StatusCode::CANCELLED otherwise
    //
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    Files file;
    file.set_name(filename);
    std::unique_ptr<ClientReader<FileStatus>> reader(service_stub->FileStats(&context, file));

    FileStatus fstatus;
    reader->Read(&fstatus);
    if(fstatus.name().empty()){ return StatusCode::NOT_FOUND; } // std::cout << "Not Found" << std::endl;

    std::cout << fstatus.name() <<  " " << fstatus.size() << " " << fstatus.ctime() << " " << fstatus.mtime() << std::endl;
    return StatusCode::OK;
}

StatusCode DFSClientNodeP1::List(std::map<std::string,int>* file_map, bool display) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to list all files here. This method
    // should connect to your service's list method and return
    // a list of files using the message type you created.
    //
    // The file_map parameter is a simple map of files. You should fill
    // the file_map with the list of files you receive with keys as the
    // file name and values as the modified time (mtime) of the file
    // received from the server.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::CANCELLED otherwise
    //
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    NoArgs args;
    std::unique_ptr<ClientReader<Result>> reader(service_stub->ListAll(&context, args));

    Result results;
    while (reader->Read(&results)){
        std::cout << results.name() << " " << results.mtime() << std::endl;
        file_map->insert(std::pair<std::string, int>(results.name(), results.mtime()));
    }
    return StatusCode::OK;

}
//
// STUDENT INSTRUCTION:
//
// Add your additional code here, including
// implementations of your client methods
//

