package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import unsw.response.models.FileInfoResponse;

/**
 * FileSystem class 
 * Deals with everything involving files
 * All entities have a FileSystem, and the properties of the FileSystem
 * is determined by what the entity can and can't do
 */
public class FileSystem {

    private List<FileTransfer> storedFiles = new ArrayList<>();
    private Map<FileTransfer, Integer> downloads = new HashMap<>();
    private List<FileTransfer> uploads = new ArrayList<>();

    private int MAX_FILES_NUM;
    private int MAX_FILES_SIZE;
    private int MAX_SPEED_DOWNLOAD;
    private int MAX_SPEED_UPLOAD;

    public FileSystem(int maxFilesNum, int maxFilesSize, int maxSpeedDownload, int maxSpeedUpload) {
        this.MAX_FILES_NUM = maxFilesNum;
        this.MAX_FILES_SIZE = maxFilesSize;
        this.MAX_SPEED_DOWNLOAD = maxSpeedDownload;
        this.MAX_SPEED_UPLOAD = maxSpeedUpload;
    }

    public Map<String, FileInfoResponse> getInfo() {
        Map<String, FileInfoResponse> files = new HashMap<>();
        for (FileTransfer file : storedFiles) {
            files.put(file.getFilename(), file.getInfo());
        }
        return files;
    }

    public FileTransfer getFile(String filename) {
        for (FileTransfer file : storedFiles) {
            if (file.getFilename().equals(filename)) {
                return file;
            }
        }
        return null;
    }

    private int getNumFiles() {
        return storedFiles.stream().mapToInt(x -> x.isComplete() ? 1 : 0).reduce(0, (x, y) -> x + y);
    }

    private int getUsage() {
        return storedFiles.stream().mapToInt(FileTransfer::getFileSize).reduce(0, (x, y) -> x + y);
    }

    public void addFile(FileTransfer file) throws FileTransferException {
        try {
            checkUsage(file.getFileSize());
        } catch (FileTransferException e) {
            if (e.getMessage().equals("Max Storage Reached")) {
                throw e;
            }
        }
        storedFiles.add(file);
        downloads.put(file, 0);
    }

    private void checkUsage(int fileSize) throws FileTransferException {
        if (getNumFiles() + 1 > MAX_FILES_NUM) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        }
        if (getUsage() + fileSize > MAX_FILES_SIZE) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    public void deleteFile(FileTransfer file) {
        storedFiles.removeIf(remove -> remove.getFilename().equals(file));
    }

    public void registerRemove(Entity remove) {
        // stop uploads
        uploads.removeIf(file -> file.getReceiver() == remove);
        // stop any incomplete transfers
        storedFiles.removeIf(file -> !file.isComplete());
    }

    public void resetDownloads() {
        downloads.clear();
    }

    public void updateUploads(List<Entity> entities) {
        // no uploads, exit
        int size = uploads.size();
        if (size == 0) {
            return;
        }

        int allocatedBytes = MAX_SPEED_UPLOAD / size;
        List<FileTransfer> temp = new ArrayList<>();
        for (FileTransfer file : uploads) {
            Entity sender = file.getSender();
            Entity receiver = file.getReceiver();

            if (file.isComplete()) {
                temp.add(file);
            }
            // target needs to be within the range, but source doesn't have to be
            boolean reach = sender.isReachable(entities, receiver);
            if (reach == false) {
                // not in reach, delete the file
                receiver.deleteFile(file.getFilename());
            } else {
                // in reach, start the transfer
                receiver.requestDownload(file, allocatedBytes);
            }
        }
        uploads.removeAll(temp);
    }

    public void updateDownloads(List<Entity> entities) {
        // no downloads, exit
        int size = downloads.size();
        if (size == 0) {
            return;
        }
        int allocatedBytes = MAX_SPEED_DOWNLOAD / size;
        List<FileTransfer> temp = new ArrayList<>();
        for (Map.Entry<FileTransfer, Integer> entry : downloads.entrySet()) {
            FileTransfer request = entry.getKey();
            int requestedBytes = entry.getValue().intValue();
            // download speed limited by min(sending speed, receiving speed)
            request.download(Math.min(requestedBytes, allocatedBytes));
            if (request.isComplete()) {
                temp.add(request);
            }
        }
        downloads.keySet().removeAll(temp);
    }

    public void startUpload(FileTransfer request) {
        uploads.add(request);
    }

    public void deleteFile(String filename) {
        storedFiles.removeIf(file -> file.getFilename().equals(filename));
    }

    public void requestDownload(FileTransfer request, int requestedBytes) {
        downloads.put(request, requestedBytes);
    }

    public boolean canAcceptIncoming() {
        return downloads.size() < MAX_SPEED_DOWNLOAD;
    }

    public boolean canAcceptOutgoing() {
        return uploads.size() < MAX_SPEED_UPLOAD;
    }
}
