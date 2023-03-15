#include <regex>
#include <mutex>
#include <vector>
#include <getopt.h>
#include <string>
#include <thread>
#include <cstdio>
#include <chrono>
#include <iostream>
#include <errno.h>
#include <csignal>
#include <sstream>
#include <iomanip>
#include <unistd.h>
#include <fstream>
#include <limits.h>
#include <sys/inotify.h>
#include <grpcpp/grpcpp.h>
#include <utime.h>

#include "src/dfs-utils.h" //Canvas @1903
#include "src/dfslibx-clientnode-p2.h"
#include "dfslib-shared-p2.h"
#include "dfslib-clientnode-p2.h"
#include "proto-src/dfs-service.grpc.pb.h"

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
using dfs_service::FileList;
using dfs_service::DFSService;

extern dfs_log_level_e DFS_LOG_LEVEL;

//
// STUDENT INSTRUCTION:
//
// Change these "using" aliases to the specific
// message types you are using to indicate
// a file request and a listing of files from the server.
//
using FileRequestType = Files;
using FileListResponseType = FileList;

std::mutex mutex; // https://www.educba.com/c-plus-plus-mutex/


DFSClientNodeP2::DFSClientNodeP2() : DFSClientNode() {}
DFSClientNodeP2::~DFSClientNodeP2() {}


grpc::StatusCode DFSClientNodeP2::Store(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to store a file here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // You can start with your Part 1 implementation. However, you will
    // need to adjust this method to recognize when a file trying to be
    // stored is the same on the server (i.e. the ALREADY_EXISTS gRPC response).
    //
    // You will also need to add a request for a write lock before attempting to store.
    //
    // If the write lock request fails, you should return a status of RESOURCE_EXHAUSTED
    // and cancel the current operation.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::ALREADY_EXISTS - if the local cached file has not changed from the server version
    // StatusCode::RESOURCE_EXHAUSTED - if a write lock cannot be obtained
    // StatusCode::CANCELLED otherwise
    //
    //
    std::string filepath = WrapPath(filename);
    if(access(filepath.c_str(), F_OK) == -1){ return StatusCode::NOT_FOUND; } 
    std::cout << mount_path << " " << filepath << std::endl;

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    StatusCode accessRequest = RequestWriteAccess(filename);
    if (accessRequest != StatusCode::OK){ return accessRequest; }

    Result result;
    std::unique_ptr<ClientWriter<FileContent>> writer(service_stub->StoreFile(&context, &result));
    // std::cout<< "transfer" << std::endl;

    FileContent content;
    content.set_name(filename);
    content.set_clientid(client_id);
    content.set_checksum(dfs_file_checksum(filepath, &this->crc_table));
    writer->Write(content);
    // std::cout<< "header" << std::endl;
    std::cout<< dfs_file_checksum(filepath, &this->crc_table) << std::endl;

    struct stat file_stat;
    stat (filepath.c_str(), &file_stat);
    int length = file_stat.st_size;
    std::ifstream fileStream(filepath); 

    int read_bytes = 0;
    int sent_bytes = 0;
    int BUFFER_SIZE = 2048;
    char buffer[BUFFER_SIZE];
    // std::cout<< "content" << std::endl;

    while(sent_bytes < length) {
        read_bytes = (length - sent_bytes) < BUFFER_SIZE ? (length - sent_bytes) : BUFFER_SIZE;
        fileStream.read(buffer, read_bytes);
        content.set_content(buffer, read_bytes);
        writer->Write(content);
        sent_bytes += read_bytes;
        // std::cout<< sent_bytes << std::endl;
    }
    fileStream.close();
    writer->WritesDone();
    Status status = writer->Finish();

    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; } 
    if (status.error_code() == 6) { return StatusCode::ALREADY_EXISTS; }
    if (status.error_code() == 8) { return StatusCode::RESOURCE_EXHAUSTED; }
    
    return StatusCode::OK;

}


grpc::StatusCode DFSClientNodeP2::RequestWriteAccess(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to obtain a write lock here when trying to store a file.
    // This method should request a write lock for the given file at the server,
    // so that the current client becomes the sole creator/writer. If the server
    // responds with a RESOURCE_EXHAUSTED response, the client should cancel
    // the current file storage
    //
    // The StatusCode response should be:
    //
    // OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::RESOURCE_EXHAUSTED - if a write lock cannot be obtained
    // StatusCode::CANCELLED otherwise
    //
    //
    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    Files file;
    file.set_name(filename);
    file.set_clientid(client_id);
    Result result;
    Status status = service_stub->WriteLock(&context, file, &result);

    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; }
    if (status.error_code() == 8) { return StatusCode::RESOURCE_EXHAUSTED; }

    return StatusCode::OK;

}

grpc::StatusCode DFSClientNodeP2::Fetch(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to fetch a file here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // You can start with your Part 1 implementation. However, you will
    // need to adjust this method to recognize when a file trying to be
    // fetched is the same on the client (i.e. the files do not differ
    // between the client and server and a fetch would be unnecessary.
    //
    // The StatusCode response should be:
    //
    // OK - if all went well
    // DEADLINE_EXCEEDED - if the deadline timeout occurs
    // NOT_FOUND - if the file cannot be found on the server
    // ALREADY_EXISTS - if the local cached file has not changed from the server version
    // CANCELLED otherwise
    //
    // Hint: You may want to match the mtime on local files to the server's mtime
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));

    Files file;
    file.set_name(filename);
    std::unique_ptr<ClientReader<FileContent>> reader(service_stub->FetchFile(&context, file));
    
    FileContent fileContent;
    reader->Read(&fileContent);
    if(fileContent.name().empty()){ return StatusCode::NOT_FOUND; } // std::cout << "Not Found" << std::endl;
    
    std::string filepath = WrapPath(filename);
    int client_checksum = dfs_file_checksum(filepath, &this->crc_table);

    if(access(filepath.c_str(), F_OK) != -1 && fileContent.checksum() == client_checksum){ 
        std::cout<< filepath << " same file" << std::endl;
        return StatusCode::ALREADY_EXISTS;
    }

    std::ofstream fileStream;
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

grpc::StatusCode DFSClientNodeP2::Delete(const std::string &filename) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to delete a file here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // You will also need to add a request for a write lock before attempting to delete.
    //
    // If the write lock request fails, you should return a status of RESOURCE_EXHAUSTED
    // and cancel the current operation.
    //
    // The StatusCode response should be:
    //
    // StatusCode::OK - if all went well
    // StatusCode::DEADLINE_EXCEEDED - if the deadline timeout occurs
    // StatusCode::RESOURCE_EXHAUSTED - if a write lock cannot be obtained
    // StatusCode::CANCELLED otherwise
    //
    //

    ClientContext context;
    context.set_deadline(std::chrono::system_clock::now() + std::chrono::milliseconds(deadline_timeout));
    
    StatusCode accessRequest = RequestWriteAccess(filename);
    if (accessRequest != StatusCode::OK){ return accessRequest; }
    
    Files file;
    file.set_name(filename);
    file.set_clientid(client_id);
    Result result;
    Status status = service_stub->DeleteFile(&context, file, &result);
    
    if (status.error_code() == 1) { return StatusCode::CANCELLED; }
    if (status.error_code() == 4) { return StatusCode::DEADLINE_EXCEEDED; }
    if (status.error_code() == 5) { return StatusCode::NOT_FOUND; }

    return StatusCode::OK;

}

grpc::StatusCode DFSClientNodeP2::List(std::map<std::string,int>* file_map, bool display) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to list files here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // You can start with your Part 1 implementation and add any additional
    // listing details that would be useful to your solution to the list response.
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

grpc::StatusCode DFSClientNodeP2::Stat(const std::string &filename, void* file_status) {

    //
    // STUDENT INSTRUCTION:
    //
    // Add your request to get the status of a file here. Refer to the Part 1
    // student instruction for details on the basics.
    //
    // You can start with your Part 1 implementation and add any additional
    // status details that would be useful to your solution.
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

void DFSClientNodeP2::InotifyWatcherCallback(std::function<void()> callback) {

    //
    // STUDENT INSTRUCTION:
    //
    // This method gets called each time inotify signals a change
    // to a file on the file system. That is every time a file is
    // modified or created.
    //
    // You may want to consider how this section will affect
    // concurrent actions between the inotify watcher and the
    // asynchronous callbacks associated with the server.
    //
    // The callback method shown must be called here, but you may surround it with
    // whatever structures you feel are necessary to ensure proper coordination
    // between the async and watcher threads.
    //
    // Hint: how can you prevent race conditions between this thread and
    // the async thread when a file event has been signaled?
    //

    mutex.lock();
    callback();
    mutex.unlock();

}

//
// STUDENT INSTRUCTION:
//
// This method handles the gRPC asynchronous callbacks from the server.
// We've provided the base structure for you, but you should review
// the hints provided in the STUDENT INSTRUCTION sections below
// in order to complete this method.
//
void DFSClientNodeP2::HandleCallbackList() {

    void* tag;

    bool ok = false;

    //
    // STUDENT INSTRUCTION:
    //
    // Add your file list synchronization code here.
    //
    // When the server responds to an asynchronous request for the CallbackList,
    // this method is called. You should then synchronize the
    // files between the server and the client based on the goals
    // described in the readme.
    //
    // In addition to synchronizing the files, you'll also need to ensure
    // that the async thread and the file watcher thread are cooperating. These
    // two threads could easily get into a race condition where both are trying
    // to write or fetch over top of each other. So, you'll need to determine
    // what type of locking/guarding is necessary to ensure the threads are
    // properly coordinated.
    //

    // Block until the next result is available in the completion queue.
    while (completion_queue.Next(&tag, &ok)) {
        {
            //
            // STUDENT INSTRUCTION:
            //
            // Consider adding a critical section or RAII style lock here
            //

            // The tag is the memory location of the call_data object
            AsyncClientData<FileListResponseType> *call_data = static_cast<AsyncClientData<FileListResponseType> *>(tag);

            dfs_log(LL_DEBUG2) << "Received completion queue callback";

            // Verify that the request was completed successfully. Note that "ok"
            // corresponds solely to the request for updates introduced by Finish().
            // GPR_ASSERT(ok);
            if (!ok) {
                dfs_log(LL_ERROR) << "Completion queue callback not ok.";
            }

            if (ok && call_data->status.ok()) {

                dfs_log(LL_DEBUG3) << "Handling async callback ";

                //
                // STUDENT INSTRUCTION:
                //
                // Add your handling of the asynchronous event calls here.
                // For example, based on the file listing returned from the server,
                // how should the client respond to this updated information?
                // Should it retrieve an updated version of the file?
                // Send an update to the server?
                // Do nothing?
                //
                mutex.lock();

                FileList call_back_list = call_data->reply;
                for(const FileStatus &fromServer : call_back_list.fstatus()) {
                    std::string filepath = WrapPath(fromServer.name());
                    if(access(filepath.c_str(), F_OK) == -1){ Fetch(fromServer.name()); continue; } 
                    
                    int checksum = dfs_file_checksum(filepath, &this->crc_table);
                    if(checksum != fromServer.checksum()){
                        struct stat file_stat;
                        stat (filepath.c_str(), &file_stat);
                        int mtime = file_stat.st_mtime;

                        if(mtime < fromServer.mtime()){ Fetch(fromServer.name()); }
                        else{ Store(fromServer.name());}
                    }
                }

                mutex.unlock();

            } else {
                dfs_log(LL_ERROR) << "Status was not ok. Will try again in " << DFS_RESET_TIMEOUT << " milliseconds.";
                dfs_log(LL_ERROR) << call_data->status.error_message();
                std::this_thread::sleep_for(std::chrono::milliseconds(DFS_RESET_TIMEOUT));
            }

            // Once we're complete, deallocate the call_data object.
            delete call_data;

            //
            // STUDENT INSTRUCTION:
            //
            // Add any additional syncing/locking mechanisms you may need here

        }


        // Start the process over and wait for the next callback response
        dfs_log(LL_DEBUG3) << "Calling InitCallbackList";
        InitCallbackList();

    }
}

/**
 * This method will start the callback request to the server, requesting
 * an update whenever the server sees that files have been modified.
 *
 * We're making use of a template function here, so that we can keep some
 * of the more intricate workings of the async process out of the way, and
 * give you a chance to focus more on the project's requirements.
 */
void DFSClientNodeP2::InitCallbackList() {
    CallbackList<FileRequestType, FileListResponseType>();
}

//
// STUDENT INSTRUCTION:
//
// Add any additional code you need to here
//


