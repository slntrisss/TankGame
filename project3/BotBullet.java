package project3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.*;

public class BotBullet {
    private int x;
    private int y;
    private Timeline animation;
    private ImageView bullet;
    private BotPlayer botPlayer;
    private Lock lock;
    private Condition cross;
    private Map map;
    //Inititaes bullet
    public BotBullet(Map map, BotPlayer botPlayer, Lock lock, Condition cross){
        this.botPlayer = botPlayer;
        this.map = map;
        this.lock = lock;
        this.cross = cross;
        x = (int)botPlayer.getBot().getX() / 40;
        y = (int)botPlayer.getBot().getY() / 40;
        //creates animation with  millisecond speed
        animation = new Timeline(new KeyFrame(Duration.millis(50), e -> boom()));
        animation.setCycleCount(Timeline.INDEFINITE);
        int coordinate = (int)botPlayer.getBot().getRotate();
        bullet = new ImageView("project3/images/bullet.png");
        bullet.setFitWidth(3);
        bullet.setFitHeight(3);
        bullet.setX(botPlayer.getBot().getX() + 20);
        bullet.setY(botPlayer.getBot().getY() + 20);
        bullet.setRotate(coordinate);
        animation.play();
    }
    public ImageView getBullet(){
        return bullet;
    }
    private void boom(){
        //based on rotation of the bullet directs it to that position
        if(bullet.getRotate() == -90){
            //checks whether there is a barrier(wall, brick) and checks if it is an end of border
            if(x + 1 < map.getSize() && canCross(y, x + 1)){
                //if condition holds then bullets travel cell by cell
                bullet.setX(bullet.getX() + 40);
                ++x;
            }
            else
                stopBullet();
        }
        //based on rotation of the bullet directs it to that position
        if(bullet.getRotate() == 90){
            //checks whether there is a barrier(wall, brick) and checks if it is an end of border
            if(x - 1 > 0 && canCross(y, x - 1)){
                //if condition holds then bullets travel cell by cell
                bullet.setX(bullet.getX() - 40);
                --x;
            }
            else
                stopBullet();
        }
        //based on rotation of the bullet directs it to that position
        if(bullet.getRotate() == 180){
            //checks whether there is a barrier(wall, brick) and checks if it is an end of border
            if(y - 1 > 0 && canCross(y - 1, x)){
                //if condition holds then bullets travel cell by cell
                bullet.setY(bullet.getY() - 40);
                --y;
            }
            else
                stopBullet();
        }
        //based on rotation of the bullet directs it to that position
        if(bullet.getRotate() == 0){
            //checks whether there is a barrier(wall, brick) and checks if it is an end of border
            if( y + 1 < map.getSize() && canCross(y + 1, x)){
                //if condition holds then bullets travel cell by cell
                bullet.setY(bullet.getY() + 40);
                ++y;
            }
            else
                stopBullet();
        }
    }
    private boolean canCross(int i, int j){
        //distracts brick
        if(map.getValueAt(i,j) == 'B')
            distractBrick(i,j);
        //synchronyzes method
        lock.lock();
        //if there is a match kills client tanks
        Server.players.forEach(e -> {
            Tank tank = e.getTank();
            if (tank.getX() == j && tank.getY() == i) {
                //decreases client lives
                e.decreaseLives();
                tank.initPlayerLives(e);
                if(e.getLives() <= 0){
                    //if lives <= 0 removes them from game
                    map.getChildren().remove(tank.getTank());
                    try {
                        //writes to clients over
                        e.getOutputStream().writeUTF("over");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        lock.unlock();
        //returns whether there is a barrier(wall water brick)
        return map.getValueAt(i,j) != 'S' && map.getValueAt(i,j) != 'B'
                && map.getValueAt(i,j) != '#';
    }
    private void stopBullet(){
        //stops bullet removes from Pane
        animation.jumpTo(Duration.ZERO);
        animation.stop();
        bullet.setVisible(false);
    }
    private void distractBrick(int i, int j){
        //to prevent we use synchronization
        synchronized (this) {
            //selects all the bricks from map
            ArrayList<Brick> bricks = map.getBricks();
            bricks.forEach(e -> {
                //If there is a match on coordinates
                if (e.getX() == j && e.getY() == i) {
                    //decreases lives of brrick
                    e.decreaseLives();
                    System.out.println(e.getLives() + " brick");
                    if (e.getLives() == 0) {
                        //simply removes brick from game
                        lock.lock();
                        e.getBrick().setVisible(false);
                        map.modifyMap(i, j);
                        cross.signalAll();
                        lock.unlock();
                    }
                }
            });
        }
    }
}
