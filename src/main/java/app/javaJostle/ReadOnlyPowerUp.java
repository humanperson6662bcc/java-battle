package app.javaJostle;

public class ReadOnlyPowerUp implements PowerUpReadOnly {
    private final PowerUp powerUp;

    public ReadOnlyPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    @Override
    public double getX() {
        return powerUp.getX();
    }

    @Override
    public double getY() {
        return powerUp.getY();
    }

    @Override
    public String getType() {
        return powerUp.getType();
    }
}
