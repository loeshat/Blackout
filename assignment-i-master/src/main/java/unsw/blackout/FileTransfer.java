package unsw.blackout;

import unsw.response.models.FileInfoResponse;

/**
 * FileTransfer class
 * Deals with everything involving the transferring of files
 * Download, uploads, etc.
 */
public class FileTransfer {

    private String filename;
    private String content;
    private int contentSize;
    private int bytesTransferred;

    private Entity sender;
    private Entity receiver;

    // for Task 2
    public FileTransfer(String filename, String content, Entity receiver, Entity sender) {
        this.filename = filename;
        this.content = content;
        this.contentSize = content.length();
        this.receiver = receiver;
        this.sender = sender;

    }

    // for Task 1
    public FileTransfer(String filename, String content, Entity receiver) {
        this.filename = filename;
        this.content = content;
        this.receiver = receiver;
        this.sender = null;
        download(contentSize);
    }

    public String getFilename() {
        return filename;
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(filename, getData(), content.length(), isComplete());
    }

    public Entity getSender() {
        return sender;
    }

    public Entity getReceiver() {
        return receiver;
    }

    public int getBytesTransferred() {
        return bytesTransferred;
    }

    public String getData() {
        return content.substring(0, bytesTransferred);
    }

    public int getFileSize() {
        return getData().length();
    }

    public boolean isComplete() {
        return bytesTransferred == contentSize;
    }

    public void download(int bytes) {
        int newSize = bytesTransferred + bytes;
        bytesTransferred = Math.min(newSize, contentSize);
    }

    public FileTransfer prepareTransfer(Entity newReceiver) {
        FileTransfer copy = new FileTransfer(filename, content, newReceiver, receiver);
        return copy;
    }

}
