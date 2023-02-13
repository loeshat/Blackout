package unsw.blackout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import unsw.response.models.EntityInfoResponse;

/**
 * Entity class
 * A entity is any object within the blackout; a satellite or a device
 */
public abstract class Entity {
    private String id;
    private double height;
    private Angle position;
    private int maxRange;

    private FileSystem fs = new FileSystem(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public Entity(String id, double height, Angle position) {
        this.id = id;
        this.height = height;
        this.position = position;
    }

    public EntityInfoResponse getInfo() {
        return new EntityInfoResponse(id, position, height, getClass().getSimpleName(), fs.getInfo());
    }

    public String getId() {
        return id;
    }

    public double getHeight() {
        return height;
    }

    public Angle getPosition() {
        return position;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public void setFileSystem(int maxFilesNum, int maxFilesSize, int maxBandwidthDown, int maxBandwidthUp) {
        this.fs = new FileSystem(maxFilesNum, maxFilesSize, maxBandwidthDown, maxBandwidthUp);
    }

    public void setupFileSystem(int maxFilesNum, int maxFilesSize, int maxBandwidthDown, int maxBandwidthUp) {
        this.fs = new FileSystem(maxFilesNum, maxFilesSize, maxBandwidthDown, maxBandwidthUp);
    }

    public void setPosition(Angle position) {
        double tempDegrees = position.toDegrees() % 360;
        if (tempDegrees < 0) {
            tempDegrees *= -1;
        }
        this.position = Angle.fromDegrees(tempDegrees);
    }

    public void addFile(FileTransfer file) throws FileTransferException {
        fs.addFile(file);
    }

    public void registerRemove(Entity remove) {
        fs.registerRemove(remove);
    }

    public void resetDownloads() {
        fs.resetDownloads();
    }

    public void updateUploads(List<Entity> entities) {
        fs.updateUploads(entities);
    }

    public void updateDownloads(List<Entity> entities) {
        fs.updateDownloads(entities);
    }

    public void requestDownload(FileTransfer request, int requestedBytes) {
        fs.requestDownload(request, requestedBytes);
    }

    public boolean canAcceptOutgoing() {
        return fs.canAcceptOutgoing();
    }

    public boolean canAcceptIncoming() {
        return fs.canAcceptIncoming();
    }

    public void sendFile(String filename, Entity receiver) throws FileTransferException {
        // check if sender and receiver can support the file transfer (have enough send
        // power)
        // file does not exist
        FileTransfer file = fs.getFile(filename);
        if (file == null || !file.isComplete()) {
            throw new FileTransferException.VirtualFileNotFoundException(filename);
        }
        // receiver is processing file already
        if (receiver.checkFileExists(filename)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(filename);
        }
        // sender does not have enough send power
        if (!this.canAcceptOutgoing()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(this.id);
        }
        // receiver does not have enough receive power
        if (!receiver.canAcceptIncoming()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(receiver.getId());
        }
        FileTransfer copy = file.prepareTransfer(receiver);
        receiver.addFile(copy);
        fs.startUpload(copy);
    }

    public void deleteFile(String filename) {
        fs.deleteFile(filename);
    }

    public boolean checkFileExists(String filename) {
        return fs.getFile(filename) != null;
    }

    public boolean isReachable(List<Entity> entities, Entity entity) {
        return getReachableEntities(entities).contains(entity);
    }

    public boolean isImmediatelyReachable(Entity entity) {
        double withinDistance = MathsHelper.getDistance(this.getHeight(), this.getPosition(), entity.getPosition());
        return communicable(entity) && isVisible(entity) && withinDistance <= getMaxRange();
    }

    public List<Entity> getReachableEntities(List<Entity> entities) {
        return depthFirstSearch(entities, new HashSet<Entity>(), this);
    }

    private List<Entity> depthFirstSearch(List<Entity> entities, Set<Entity> visited, Entity sender) {
        if (visited.contains(this)) {
            return new ArrayList<>();
        }
        visited.add(this);
        Set<Entity> temp = new HashSet<>();
        for (Entity entity : entities) {
            boolean reachable = this.isImmediatelyReachable(entity) && sender.communicable(entity)
                    && entity.communicable(sender);
            if (!reachable || this == entity || visited.contains(entity)) {
                continue;
            }
            temp.add(entity);
            if (entity.canRelay()) {
                temp.addAll(entity.depthFirstSearch(entities, visited, sender));
            }
        }
        return temp.stream().collect(Collectors.toList());
    }

    /**
     * Checks if an entity is able to communicate with another entity
     * Overriden in StandardSatellite
     * 
     * @param entity
     * @return true if communicable, false otherwise
     */
    public boolean communicable(Entity entity) {
        return true;
    }

    public boolean canRelay() {
        return false;
    }

    public abstract boolean isVisible(Entity entity);

    /**
     * Checks if an entity can relay conditions
     * Overriden in RelaySatellite
     * 
     * @return true if can relay, false otherwise
     */

}
