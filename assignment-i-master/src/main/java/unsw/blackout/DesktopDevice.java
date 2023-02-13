package unsw.blackout;

import unsw.utils.*;

/**
 * DesktopDevice, one of the three types of devices 
 * Has a different range to other devices.
 */
public class DesktopDevice extends Device {
    public DesktopDevice(String id, Angle position) {
        super(id, position);
        setMaxRange(200000);
    }
}
