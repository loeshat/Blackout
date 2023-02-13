package unsw.blackout;

import unsw.utils.*;

/**
 * HandheldDevice, one of the three types of devices 
 * Has a different range to other devices.
 */
public class HandheldDevice extends Device {
    public HandheldDevice(String id, Angle position) {
        super(id, position);
        setMaxRange(50000);
    }
}
