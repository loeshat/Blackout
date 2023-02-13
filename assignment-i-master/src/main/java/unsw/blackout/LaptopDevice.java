package unsw.blackout;

import unsw.utils.*;

/**
 * LaptopDevice, one of the three types of devices 
 * Has a different range to other devices.
 */
public class LaptopDevice extends Device {
    public LaptopDevice(String id, Angle position) {
        super(id, position);
        setMaxRange(100000);
    }
}