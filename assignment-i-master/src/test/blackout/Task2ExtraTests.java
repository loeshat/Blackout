package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.blackout.FileTransferException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExtraTests {
    @Test
    public void sendBehaviourOutOfRange() {
        // extension of the test case within the sample tests
        BlackoutController controller = new BlackoutController();

        // Creates 2 satellite and 2 devices
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(319));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(319));

        // 1 message too long to be completely sent to the satellite, will return null
        String msg = "u cute";
        String msg2 = "i think u cute and pretty good";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        controller.addFileToDevice("DeviceB", "FileBeta", msg2);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertDoesNotThrow(() -> controller.sendFile("FileBeta", "DeviceB", "Satellite2"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        assertEquals(new FileInfoResponse("FileBeta", "", msg2.length(), false),
                controller.getInfo("Satellite2").getFiles().get("FileBeta"));

        // halfway through the msg
        controller.simulate(3);
        assertEquals(new FileInfoResponse("FileAlpha", "u c", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        assertEquals(new FileInfoResponse("FileBeta", "i t", msg2.length(), false),
                controller.getInfo("Satellite2").getFiles().get("FileBeta"));

        // all the way through the msg, not through msg2
        controller.simulate(3);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        assertEquals(new FileInfoResponse("FileBeta", "i thin", msg2.length(), false),
                controller.getInfo("Satellite2").getFiles().get("FileBeta"));

        // still in range
        controller.simulate(9);
        assertEquals(new FileInfoResponse("FileBeta", "i think u cute ", msg2.length(), false),
                controller.getInfo("Satellite2").getFiles().get("FileBeta"));

        // no longer in range
        controller.simulate(1);
        assertEquals(null, controller.getInfo("Satellite2").getFiles().get("FileBeta"));
    }

    @Test
    public void testReceiveBandwidthExceptions() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(100));
        controller.createDevice("Device2", "HandheldDevice", Angle.fromDegrees(100));
        controller.createDevice("Device3", "HandheldDevice", Angle.fromDegrees(100));
        controller.createSatellite("Standard1", "StandardSatellite", RADIUS_OF_JUPITER + 1000, Angle.fromDegrees(95));

        // create file
        String msg = "l";
        controller.addFileToDevice("Device1", "test1", msg);

        assertDoesNotThrow(() -> controller.sendFile("test1", "Device1", "Standard1"));

        controller.simulate(1);
        assertDoesNotThrow(() -> controller.sendFile("test1", "Standard1", "Device2"));

        // StandardSatellite can't have two outgoing connections
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("test1", "Standard1", "Device3"), "Standard1");
    }
}
