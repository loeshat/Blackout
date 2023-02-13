package unsw.blackout;

import unsw.utils.*;

/**
 * RelaySatellite class
 * One of the three satellite
 * Has funky movement where it only goes from 140 to 190 degrees
 */

public class RelaySatellite extends Satellite {
    private boolean positiveDirection = true;
    private Angle lower = Angle.fromDegrees(140);
    private Angle upper = Angle.fromDegrees(190);
    private Angle threshold = Angle.fromDegrees(345);

    public RelaySatellite(String id, double height, Angle position) {
        super(id, height, position);
        setVelocity(1500);
        setMaxRange(300000);
        setupFileSystem(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public boolean canRelay() {
        return true;
    }

    private boolean inRegion(Angle position) {
        return (position.compareTo(lower) == 1 && position.compareTo(upper) == -1);
    }

    @Override
    public void move() {
        Angle position = getPosition();
        boolean inRegion = inRegion(position);
        if (!inRegion) {
            // satellite not in the suitable range at the moment
            // move in a negative direction
            boolean moveToUpperRegion = position.compareTo(threshold) == -1 && position.compareTo(upper) == 1;
            positiveDirection = moveToUpperRegion;
        }
        Angle temp = Angle.fromRadians(getVelocity() / getHeight());
        Angle newPosition;
        if (positiveDirection) {
            newPosition = position.subtract(temp);
        } else {
            newPosition = position.add(temp);
        }
        // convert to degrees
        double degrees = newPosition.toDegrees() % 360;
        if (degrees < 0) {
            degrees *= -1;
        }
        newPosition = Angle.fromDegrees(degrees);
        // check new position - coming back
        if (inRegion && !inRegion(newPosition)) {
            positiveDirection = !positiveDirection;
        }
        setPosition(newPosition);
    }
}
