package project3;

import javafx.scene.image.ImageView;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/*###############################
###############################
###########################
#########################
####################
###################
##########3
 */


/*######################-----------Writes every client info------------#####################*/

public class ClientInfo implements Serializable {
    private int lives;
    private Tank tank;
    private transient ArrayList<Brick> bricks;
    private DataOutputStream outputStream;
    private String name;
    private ArrayList<ImageView> playerLives = new ArrayList<>();
    private int number;
    public void setPlayerLives(ArrayList<ImageView> playerLives) {
        this.playerLives = playerLives;
    }

    public ArrayList<ImageView> getPlayerLives() {
        return playerLives;
    }

    public int getNumber() {
        return number;
    }

    public ClientInfo(int lives, Tank tank, ArrayList<Brick> bricks,
                      DataOutputStream outputStream, String name, int number) {
        this.lives = lives;
        this.outputStream = outputStream;
        this.number = number;
        this.tank = tank;
        this.bricks = bricks;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
    public void decreaseLives(){
        --lives;
    }
    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public void setBricks(ArrayList<Brick> bricks) {
        this.bricks = bricks;
    }

    public int getLives() {
        return lives;
    }

    public Tank getTank() {
        return tank;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }
}
