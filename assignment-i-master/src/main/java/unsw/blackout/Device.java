package unsw.blackout;

import unsw.utils.*;

/**
 * Device class
 * Devices exist on the surface of Jupiter
 * some devices cannot communicate with some satellites
 * devices cannot communicate with other devices
 */
public abstract class Device extends Entity {

    public Device(String id, Angle position) {
        super(id, MathsHelper.RADIUS_OF_JUPITER, position);
    }

    @Override
    public boolean communicable(Entity entity) {
        return entity instanceof Satellite;
    }

    @Override 
    public boolean isVisible(Entity entity) {
        return MathsHelper.isVisible(entity.getHeight(), entity.getPosition(), this.getPosition());
    }
}
