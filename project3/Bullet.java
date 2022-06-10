package project3;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Bullet {
    private int x;
    private int y;
    private Timeline timeline;
    private ImageView bullet;
    private Tank tank;
    private Map map;
    private ArrayList<ClientInfo> players;
    private DataOutputStream outputStream;
    private Lock lock = new ReentrantLock();
    //creates bullet
    public Bullet(Map map, Tank tank, ArrayList<ClientInfo> players, DataOutputStream outputStream){
        this.tank = tank;
        this.map = map;
        this.outputStream = outputStream;
        this.players = players;
        x = (int)tank.getTank().getX() / 40;
        y = (int)tank.getTank().getY() / 40;
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> pew()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        int coordinate = (int)tank.getTank().getRotate();
        bullet = new ImageView("project3/images/bullet.png");
        bullet.setFitWidth(3);
        bullet.setFitHeight(3);
        bullet.setX(tank.getTank().getX() + 20);
        bullet.setY(tank.getTank().getY() + 20);
        bullet.setRotate(coordinate);
        timeline.play();
    }
    //creates bullets
    public Bullet(Map map, Tank tank){
        this.tank = tank;
        this.map = map;
        x = (int)tank.getTank().getX() / 40;
        y = (int)tank.getTank().getY() / 40;
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> pew()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        int coordinate = (int)tank.getTank().getRotate();
        bullet = new ImageView("project3/images/bullet.png");
        bullet.setFitWidth(3);
        bullet.setFitHeight(3);
        bullet.setX(tank.getTank().getX() + 20);
        bullet.setY(tank.getTank().getY() + 20);
        bullet.setRotate(coordinate);
        timeline.play();
    }
    public ImageView getBullet(){
        return bullet;
    }
    public void pew(){
        //based on rotation of the bullet directs it to that position
        if(bullet.getRotate() == -90){
            //checks whether there is a barrier(wall, brick) and checks if it is an end of border
            if(x + 1 < map.getSize() && canCross(y, x + 1)){
                //if condition holds then bullets travel cell by cell
                bullet.setX(bullet.getX() + 40);
                ++x;
            }
            else
                //If there is an end of the border or barrier sets initial speed to 0 and removes bullet from Pane
                //stops it
                stopBullet();
        }
        if(bullet.getRotate() == 90){
            if(x - 1 > 0 && canCross(y, x - 1)){
                bullet.setX(bullet.getX() - 40);
                --x;
            }
            else
                stopBullet();
        }
        if(bullet.getRotate() == 180){
            if(y - 1 > 0 && canCross(y - 1, x)){
                bullet.setY(bullet.getY() - 40);
                --y;
            }
            else
                stopBullet();
        }
        if(bullet.getRotate() == 0){
            if( y + 1 < map.getSize() && canCross(y + 1, x)){
                bullet.setY(bullet.getY() + 40);
                ++y;
            }
            else
                stopBullet();
        }
    }
    private boolean canCross(int i, int j){
        if(map.getValueAt(i,j) == 'B')
            distractBrick(i,j);
        if(players != null) {
            //synchronize to prevent data corruption
            lock.lock();
            //if there is match on coordinates of the buulet and players then decrease player live
            players.forEach(e -> {
                Tank tank = e.getTank();
                if (tank.getX() == j && tank.getY() == i) {
                    e.decreaseLives();
                    tank.initPlayerLives(e);
                    if(e.getLives() == 0){
                        //remove player from the game
                        map.getChildren().remove(e.getTank().getTank());
                        e = null;
                    }
                }
            });
            lock.unlock();
        }
        if(map.getValueAt(i, j) == 'b'){
            //synchronize to prevent data corruption
            synchronized (this){
                //for each botplayer check the coordinates
                BotPlayer[] botPlayers = Server.botPlayers;
                for(BotPlayer e: botPlayers){
                    //if there is match on coordinates of the bullet and tank
                    if(e.getX() == j && e.getY() == i){
                        //kill tank
                        map.getChildren().remove(e.getBot());
                        e.setAlive(false);
                        map.modifyMap(i,j,'0');
                        Server.bots--;
                        tank.setIndicators();
                    }
                }
            }
        }
        return map.getValueAt(i,j) != 'S' && map.getValueAt(i,j) != 'B'
                && map.getValueAt(i,j) != '#';
    }
    private void stopBullet(){
        //If there is an end of the border or barrier sets initial speed to 0 and removes bullet from Pane
        //stops it
        timeline.jumpTo(Duration.ZERO);
        timeline.stop();
        bullet.setVisible(false);
    }
    private void distractBrick(int i, int j){
        ArrayList<Brick> bricks = map.getBricks();
        //distract the bullets on the front
        synchronized (this) {
            bricks.forEach(e -> {
                if (e.getX() == j && e.getY() == i) {
                    e.decreaseLives();
                    if (e.getLives() == 0) {
                        e.getBrick().setVisible(false);
                        map.modifyMap(i, j);
                    }
                }
            });
        }
    }
}

