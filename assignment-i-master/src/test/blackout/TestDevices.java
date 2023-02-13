package blackout;

import org.junit.jupiter.api.Test;

import unsw.blackout.BlackoutController;

import unsw.utils.Angle;
import java.util.List;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

public class TestDevices {
    @Test
    public void testCommunicableTypes() {
        BlackoutController controller = new BlackoutController();

        // create all devices and satellites
        List<String> devices = Arrays.asList("HandheldDevice", "LaptopDevice", "DesktopDevice");
        for (String device : devices) {
            controller.createDevice(device, device, Angle.fromDegrees(100));
        }
        List<String> satellites = Arrays.asList("StandardSatellite", "TeleportingSatellite", "RelaySatellite");
        for (String satellite : satellites)
            controller.createSatellite(satellite, satellite, RADIUS_OF_JUPITER + 100, Angle.fromDegrees(100));

        // check what each device is communicable with
        assertListAreEqualIgnoringOrder(
                Arrays.asList("StandardSatellite", "TeleportingSatellite", "RelaySatellite"),
                controller.communicableEntitiesInRange("HandheldDevice"));
        assertListAreEqualIgnoringOrder(
                Arrays.asList("StandardSatellite", "TeleportingSatellite", "RelaySatellite"),
                controller.communicableEntitiesInRange("LaptopDevice"));
        assertListAreEqualIgnoringOrder(
                Arrays.asList("RelaySatellite", "TeleportingSatellite"),
                controller.communicableEntitiesInRange("DesktopDevice"));
    }
}
