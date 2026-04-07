package app.javaJostle;

public interface RobotReadOnly {
    int getHealth();
    int getMaxHealth();
    int getSpeed();
    String getName();
    int getX();
    int getY();
    int getXMovement();
    int getYMovement();
    int getHealthPoints();
    int getSpeedPoints();
    int getAttackSpeedPoints();
    int getProjectileStrengthPoints();
    boolean isAlive();
    boolean hasSpeedBoost();
    boolean hasAttackBoost();
}
