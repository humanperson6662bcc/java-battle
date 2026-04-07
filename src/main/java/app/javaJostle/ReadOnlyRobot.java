package app.javaJostle;

public class ReadOnlyRobot implements RobotReadOnly {
    private final Robot robot;

    public ReadOnlyRobot(Robot robot) {
        this.robot = robot;
    }

    @Override
    public int getHealth() {
        return robot.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return robot.getMaxHealth();
    }

    @Override
    public int getSpeed() {
        return robot.getSpeed();
    }

    @Override
    public String getName() {
        return robot.getName();
    }

    @Override
    public int getX() {
        return robot.getX();
    }

    @Override
    public int getY() {
        return robot.getY();
    }

    @Override
    public int getXMovement() {
        return robot.getXMovement();
    }

    @Override
    public int getYMovement() {
        return robot.getYMovement();
    }

    @Override
    public int getHealthPoints() {
        return robot.getHealthPoints();
    }

    @Override
    public int getSpeedPoints() {
        return robot.getSpeedPoints();
    }

    @Override
    public int getAttackSpeedPoints() {
        return robot.getAttackSpeedPoints();
    }

    @Override
    public int getProjectileStrengthPoints() {
        return robot.getProjectileStrengthPoints();
    }

    @Override
    public boolean isAlive() {
        return robot.isAlive();
    }

    @Override
    public boolean hasSpeedBoost() {
        return robot.hasSpeedBoost();
    }

    @Override
    public boolean hasAttackBoost() {
        return robot.hasAttackBoost();
    }
}
