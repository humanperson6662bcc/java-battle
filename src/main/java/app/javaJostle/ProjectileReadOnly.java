package app.javaJostle;

public interface ProjectileReadOnly {
    double getX();
    double getY();
    double getAngle();
    RobotReadOnly getOwner();
    int getProjectileSpeed();
    int getProjectileDamage();
    boolean isAlive();
}
