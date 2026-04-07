package app.javaJostle;

public class ReadOnlyProjectile implements ProjectileReadOnly {
    private final Projectile projectile;

    public ReadOnlyProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    @Override
    public double getX() {
        return projectile.getX();
    }

    @Override
    public double getY() {
        return projectile.getY();
    }

    @Override
    public double getAngle() {
        return projectile.getAngle();
    }

    @Override
    public RobotReadOnly getOwner() {
        return new ReadOnlyRobot(projectile.getOwner());
    }

    @Override
    public int getProjectileSpeed() {
        return projectile.getProjectileSpeed();
    }

    @Override
    public int getProjectileDamage() {
        return projectile.getProjectileDamage();
    }

    @Override
    public boolean isAlive() {
        return projectile.isAlive();
    }
}
