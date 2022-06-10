package project3;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.*;
public class Map extends Pane implements Serializable {
    private int size;
    private char[][] map;
    private Position start;
    private ArrayList<ImageView> list;
    private ArrayList<Brick> bricks;
    //reads file from scanner
    public Map(Scanner input, int size){
        this.size = size;
        map = new char[size][size];
        list = new ArrayList<>();
        bricks = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                map[i][j] = input.next().charAt(0);
                //creates borders
                Rectangle layout = new Rectangle(j * 40, i * 40, 40,40);
                getChildren().add(layout);
                //creates borders
                if(map[i][j] == '#'){
                    Rectangle rec = new Rectangle(j * 40, i * 40, 40, 40);
                    rec.setFill(Color.GREY);
                    getChildren().add(rec);
                }
                //creates bricks
                else if(map[i][j] == 'B'){
                    Brick brick = new Brick(j,i);
                    getChildren().add(brick.getBrick());
                    bricks.add(brick);
                }
                //creates trees
                else if(map[i][j] == 'T'){
                    list.add(new Tree(j, i).getTree());
                }
                //creates walls
                else if(map[i][j] =='S'){
                    getChildren().add(new Steel(j,i).getSteel());
                }
                //initiates start position of the Tank
                else if(map[i][j] == 'P'){
                    start = new Position(j,i);
                }//creates water

                else if(map[i][j] == 'W'){
                    getChildren().add(new Water(j, i).getWater());
                }
            }
        }
    }
    public void setStart(Position position){
        this.start = position;
    }
    //modifies map elemenst on specified position
    public void modifyMap(int i, int j, char ch){
        map[i][j] = ch;
    }
    //return bricks
    public ArrayList<Brick> getBricks(){
        return bricks;
    }
    //modifies map element
    public void modifyMap(int i, int j){
        map[i][j] = '0';
    }
    public int getSize(){
        return size;
    }
    public Position getStartPosition(){
        return start;
    }
    //return value on specific position
    public char getValueAt(int i, int j){
        return map[i][j];
    }
    public ArrayList<ImageView> getTrees(){
        return list;
    }
    public char[][] getMap(){
        return map;
    }
    public void print(){
        for(int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}

