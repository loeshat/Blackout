package unsw.blackout;

import unsw.utils.*;

/**
 * TeleportingSatellite class
 * One of the three satellite
 * Has funky movement where it teleports
 * Also has funky behaviour dealing with file transfer but i didnt implement it too hard
 */

public class TeleportingSatellite extends Satellite {
    private boolean setHasTeleported = false;
    private int direction = MathsHelper.ANTI_CLOCKWISE;

    public TeleportingSatellite(String id, double height, Angle position) {
        super(id, height, position);
        setVelocity(1000);
        setMaxRange(200000);
        setFileSystem(Integer.MAX_VALUE, 200, 15, 10);
    }

    @Override
    public void move() {
        Angle position = getPosition();
        // positiveDirection == true: add (anticlockwise), false: subtract (clockwise)
        Angle newPosition;
        // we can't subtract from 0, so change to 360 if going clockwise
        if (position.toDegrees() == 0.0 && direction == MathsHelper.CLOCKWISE) {
            position = Angle.fromDegrees(360);
        }
        Angle temp = Angle.fromRadians(getVelocity() / getHeight());

        if (direction == MathsHelper.ANTI_CLOCKWISE) {
            newPosition = position.add(temp);
        } else {
            newPosition = position.subtract(temp);
        }
        // teleportation
        if (direction == MathsHelper.ANTI_CLOCKWISE && newPosition.toDegrees() >= 180.0 && position.toDegrees() < 180.0
                || direction == MathsHelper.CLOCKWISE && newPosition.toDegrees() <= 180.0
                        && position.toDegrees() > 180.0) {
            Angle teleportPosition = Angle.fromDegrees(0);
            newPosition = teleportPosition;
            direction *= -1;
            this.setHasTeleported(true);
        }
        this.setHasTeleported(false);
        setPosition(newPosition);
    }

    private void setHasTeleported(boolean hasTeleported) {
        this.setHasTeleported = hasTeleported;
    }

}