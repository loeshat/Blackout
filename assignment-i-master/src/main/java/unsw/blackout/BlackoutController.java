package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import unsw.response.models.*;
import unsw.utils.Angle;


/**
 * BlackoutController class
 * The brains of the whole operation 
 * We run all of the funcitons from a controller
 */
public class BlackoutController {

    private Map<String, Entity> entities = new HashMap<>();

    /**
     * Creating a new device
     * 
     * @param deviceId
     * @param type
     * @param position
     */

    private void removeEntity(String id) {
        Entity obliterate = entities.remove(id);
        for (Entity entity : entities.values()) {
            entity.registerRemove(obliterate);
        }
    }

    public void createDevice(String deviceId, String type, Angle position) {
        // TODO: Task 1a)
        if (type.equals("HandheldDevice")) {
            HandheldDevice newHandheldDevice = new HandheldDevice(deviceId, position);
            entities.put(deviceId, newHandheldDevice);
        } else if (type.equals("LaptopDevice")) {
            LaptopDevice newLaptopDevice = new LaptopDevice(deviceId, position);
            entities.put(deviceId, newLaptopDevice);
        } else if (type.equals("DesktopDevice")) {
            DesktopDevice newDesktopDevice = new DesktopDevice(deviceId, position);
            entities.put(deviceId, newDesktopDevice);
        }
    }

    /**
     * removing a device from the list
     * 
     * @param deviceId
     */
    public void removeDevice(String deviceId) {
        // TODO: Task 1b)
        removeEntity(deviceId);
    }

    /**
     * Creating a new satellite
     * 
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        // TODO: Task 1c)
        if (type.equals("StandardSatellite")) {
            StandardSatellite newStandardSatellite = new StandardSatellite(satelliteId, height, position);
            entities.put(satelliteId, newStandardSatellite);
        } else if (type.equals("TeleportingSatellite")) {
            TeleportingSatellite newTeleportingSatellite = new TeleportingSatellite(satelliteId, height,
                    position);
            entities.put(satelliteId, newTeleportingSatellite);
        } else if (type.equals("RelaySatellite")) {
            RelaySatellite newRelaySatellite = new RelaySatellite(satelliteId, height, position);
            entities.put(satelliteId, newRelaySatellite);
        }
    }

    /**
     * Removing a satellite from the list
     * 
     * @param satelliteId
     */
    public void removeSatellite(String satelliteId) {
        // TODO: Task 1d)
        removeEntity(satelliteId);
    }

    public List<String> listDeviceIds() {
        // TODO: Task 1e)
        List<String> deviceIds = new ArrayList<>();
        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            String deviceId = entry.getKey();
            Entity entity = entry.getValue();
            if (entity instanceof Device) {
                deviceIds.add(deviceId);
            }
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        // TODO: Task 1f)
        List<String> satelliteIds = new ArrayList<>();
        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            String satelliteId = entry.getKey();
            Entity entity = entry.getValue();
            if (entity instanceof Satellite) {
                satelliteIds.add(satelliteId);
            }
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // TODO: Task 1g)
        Entity device = entities.get(deviceId);
        FileTransfer file = new FileTransfer(filename, content, device);
        // handle the exception
        try {
            device.addFile(file);
        } catch (FileTransferException e) {
        }
    }

    public EntityInfoResponse getInfo(String id) {
        // TODO: Task 1h)
        Entity entity = entities.get(id);
        return entity.getInfo();
    }

    public void simulate() {
        // TODO: Task 2a)
        // Simulate movement for 1 minute
        List<Entity> allEntities = List.copyOf(entities.values());
        for (Entity entity : this.entities.values()) {
            if (entity instanceof Satellite) {
                ((Satellite) entity).move();
            }
        }
        // check if downloads are still possible (because of movement)
        for (Entity entity : this.entities.values()) {
            entity.resetDownloads();
        }
        // update uploads
        for (Entity entity : this.entities.values()) {
            entity.updateUploads(allEntities);
        }
        // update downloads
        for (Entity entity : this.entities.values()) {
            entity.updateDownloads(allEntities);
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        // TODO: Task 2 b)
        Entity entityInQuestion = entities.get(id);
        List<Entity> allEntities = List.copyOf(entities.values());
        return entityInQuestion.getReachableEntities(allEntities).stream().map(Entity::getId)
                .collect(Collectors.toList());
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
        Entity sender = entities.get(fromId);
        Entity receiver = entities.get(toId);
        sender.sendFile(fileName, receiver);
    }
}
