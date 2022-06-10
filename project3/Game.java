package project3;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Game extends Application {
    static Pane pane;
    private Map map;
    private Player player;

    public Game(Map map){
        this.map = map;
    }

    public Game() {
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void addPlayer(Player player) {
        this.player = player;
    }
    public void start(Stage stage) throws FileNotFoundException {
        //initiates startup configuration for the game
        init();
        Scene scene = new Scene(map, 400, 400);
        Tank tank = new Tank(map.getStartPosition().getX(), map.getStartPosition().getY(),map);
        map.getChildren().add(tank.getTank());
        map.getChildren().addAll(map.getTrees());
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP)
                tank.moveUp();
            if(e.getCode() == KeyCode.DOWN)
                tank.moveDown();
            if(e.getCode() == KeyCode.RIGHT)
                tank.moveRight();
            if(e.getCode() == KeyCode.LEFT)
                tank.moveLeft();
            if(e.getCode() == KeyCode.SPACE){
            }
        });

        stage.setScene(scene);
        stage.setTitle("Tank");
        stage.show();
    }
    public void init() throws FileNotFoundException {
        //        Scanner input = new Scanner(System.in);
//        System.out.print("Enter a file name: ");
//        String fileName = input.next();
//        File file = new File(fileName);
//        if(!file.exists())
//            throw new InvalidMapException("Given file does not exist");
//        System.out.print("Enter a size of map: ");
//        int size = input.nextInt();
        map = new Map(new Scanner(new File("map1.txt")), 14);
    }
}
