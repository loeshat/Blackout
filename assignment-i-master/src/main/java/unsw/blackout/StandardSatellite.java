package unsw.blackout;

import unsw.utils.*;

/**
 * StandardSatellite class
 * One of the three satellite
 * Behaves normally
 */

public class StandardSatellite extends Satellite {

    public StandardSatellite(String id, double height, Angle position) {
        super(id, height, position);
        setVelocity(2500);
        setMaxRange(150000);
        setFileSystem(3, 80, 1, 1);
    }

    @Override
    public boolean communicable(Entity entity) {
        return (entity instanceof Satellite || entity instanceof HandheldDevice || entity instanceof LaptopDevice);
    }
}