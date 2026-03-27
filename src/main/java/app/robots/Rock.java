package app.robots;

import java.util.ArrayList;
import app.javaJostle.Map;
import app.javaJostle.PowerUp;
import app.javaJostle.Projectile;
import app.javaJostle.Robot;

public class Rock extends Robot {
    public Rock(int x, int y){
        super(x, y, 5, 1, 3, 1,"Rock", "rock.png", "rock.png");
        
        // Health: 5, Speed: 1, Attack Speed: 3, Projectile Strength: 1
        // Total = 10
    }

    public void think(ArrayList<Robot> robots, ArrayList<Projectile> projectiles, Map map, ArrayList<PowerUp> powerups) {
        //rock robot is not smart and doesn't think very well. 
                
    }

}