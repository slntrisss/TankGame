package project3;

import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class Tank extends MyPlayer implements Serializable {
    private int x;
    private int y;
    private int lives = 3;
    private Map map;
    private transient ImageView tank;
    public Tank(int x, int y, Map map){
        this.x = x;
        this.y = y;
        this.map = map;
        tank = new ImageView("project3/images/tank.png");
        tank.setFitWidth(40);
        tank.setFitHeight(40);
        tank.setX(x * 40);
        tank.setY(y * 40);
    }
    public ImageView getTank() {
        return tank;
    }
    public void moveRight(){
        //rotates tank
        tank.setRotate(-90);
        //ensures tank to not go out of the borders
        if(x + 1 < map.getSize() && canPass(y, x + 1)){
            tank.setX(tank.getX() + 40);
            synchronized (this){
                map.modifyMap(y, x, '0');
                ++x;
                map.modifyMap(y, x, 'P');
            }
        }
    }
    public void moveLeft(){
        tank.setRotate(90);
        if(x - 1 > 0 && canPass(y, x - 1)){
            tank.setX(tank.getX() - 40);
            synchronized (this){
                map.modifyMap(y, x, '0');
                --x;
                map.modifyMap(y, x, 'P');
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveUp(){
        tank.setRotate(180);
        if(y - 1 > 0 && canPass(y - 1, x)){
            tank.setY(tank.getY() - 40);
            synchronized (this){
                map.modifyMap(y, x, '0');
                --y;
                map.modifyMap(y, x, 'P');
            }
        }
    }
    public void moveDown(){
        tank.setRotate(0);
        if(y + 1 < map.getSize() && canPass(y + 1, x)){
            tank.setY(tank.getY() + 40);
            synchronized (this){
                map.modifyMap(y, x, '0');
                ++y;
                map.modifyMap(y, x, 'P');
            }
        }
    }
    private boolean canPass(int i, int j){
        return map.getValueAt(i,j) != 'S' && map.getValueAt(i,j) != 'B'
                && map.getValueAt(i,j) != 'W' && map.getValueAt(i,j) != '#';
    }
    //draws tank lives in the scene(3 yellow tanks on the game window)
    void setIndicators(){
        int count = 0;
        Server.botIndicators.forEach(el -> map.getChildren().remove(el));
        ArrayList<ImageView> botIndicators = new ArrayList<>();
        Server.botIndicators = botIndicators;
        for(int i = 0; i < Server.bots; i++){
            ImageView lives = new ImageView("project3/images/lives.png");
            int x = map.getSize() - 2;
            if(i % 3 == 0)
                count++;
            int y = 3 + count;
            lives.setX(x * 40 + i * 20 + 5);
            lives.setY(y * 40);
            lives.setFitHeight(20);
            lives.setFitWidth(20);
            botIndicators.add(lives);
        }

        map.getChildren().addAll(botIndicators);
    }
    //draws players lives on the scene (3 hearts on the Game window)
    void  initPlayerLives(ClientInfo info){
        if(info.getPlayerLives().size() > 0){
            info.getPlayerLives().forEach(e -> map.getChildren().remove(e));
        }
        ArrayList<ImageView> playerIndicators = new ArrayList<>();
        info.setPlayerLives(playerIndicators);
        Text text = new Text("P" + info.getNumber());
        text.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        text.setX((map.getSize() - 2) * 40 + 5);
        text.setY((8 + info.getNumber()) * 40);
        for(int i = 0; i < info.getLives(); i++){
            ImageView lives = new ImageView("project3/images/hp.png");
            int x = map.getSize() - 2;
            int y = 8 + info.getNumber();
            lives.setX(x * 40 + i * 20 + 5);
            lives.setY(y * 40);
            lives.setFitHeight(20);
            lives.setFitWidth(20);
            playerIndicators.add(lives);
        }
        map.getChildren().addAll(playerIndicators);
        map.getChildren().add(text);
    }
}
