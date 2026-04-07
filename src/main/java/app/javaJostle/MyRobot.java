package app.javaJostle;

import java.util.List;

public class MyRobot extends Robot {
    public MyRobot(int x, int y){
        super(x, y, 3, 2, 2, 3,"MyRobot", "myRobot.png", "defaultProjectile.png");
        // Health: 3, Speed: 2, Attack Speed: 2, Projectile Strength: 3
        // Total = 10
    }

    @Override
    public void think(List<RobotReadOnly> robots, List<ProjectileReadOnly> projectiles, MapReadOnly map, List<PowerUpReadOnly> powerups) {
        // Put your logic here
    }
}
