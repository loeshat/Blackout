package unsw.blackout;

import unsw.utils.*;

/**
 * Satellite class
 * Satellites exist in the orbit of Jupiter
 * some Satellites cannot download files
 * some Satellites have slow download/upoad speed, while others have fast
 * all have a byte capacity
 */
public abstract class Satellite extends Entity {

    private int velocity;

    public Satellite(String id, double height, Angle position) {
        super(id, height, position);
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public boolean isVisible(Entity entity) {
        if (entity instanceof Device) {
            return ((Device) entity).isVisible(this);
        }
        return MathsHelper.isVisible(this.getHeight(), this.getPosition(), entity.getHeight(), entity.getPosition());
    }

    // move the satellite for 1 minute, take into consideration of 360 degrees
    public void move() {
        double temp = velocity / getHeight();
        Angle tempAngle = Angle.fromRadians(temp);
        // revolution - return to 360 degrees
        Angle newPosition;
        // have to change to degrees to modulus by 360
        if (tempAngle.toDegrees() > getPosition().toDegrees()) {
            Angle filler = tempAngle.subtract(getPosition());
            Angle revolution = Angle.fromDegrees(360);
            newPosition = revolution.subtract(filler);
        } else {
            newPosition = getPosition().subtract(tempAngle);
        }
        setPosition(Angle.fromDegrees(newPosition.toDegrees()));
    }
}
