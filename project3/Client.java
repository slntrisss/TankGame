package project3;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client extends Application {
    private Map map;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private Scene scene;
    private Tank tank;
    //Initiates start configuration
    public void start(Stage stage) throws FileNotFoundException {
        map = new Map(new Scanner(new File("src/project3/images/map.txt")),14);

        Position position = getValidPosition();
        map.setStart(position);
        tank = new Tank(position.getX(), position.getY(), map);
        map.getChildren().add(tank.getTank());
        map.getChildren().addAll(map.getTrees());

        scene = new Scene(map, 560,560);

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.RIGHT) {
                tank.moveRight();
                try {
                    //rights movement direction to server
                    moveClient('R');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.UP) {
                tank.moveUp();
                try {
                    //rights movement direction to server
                    moveClient('U');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.LEFT) {
                tank.moveLeft();
                try {
                    //rights movement direction to server
                    moveClient('L');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.DOWN) {
                tank.moveDown();
                try {
                    //rights movement direction to server
                    moveClient('D');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.SPACE){
                Bullet bullet = new Bullet(map, tank);
                map.getChildren().remove(tank.getTank());
                map.getChildren().removeAll(map.getTrees());
                map.getChildren().addAll(bullet.getBullet(), tank.getTank());
                map.getChildren().addAll(map.getTrees());
                try {
                    //rights movement direction to server
                    moveClient('S');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();
        stage.setResizable(false);
        connect(stage);
    }
    private void connect(Stage stage){
        //connects to Server
        try{
            socket = new Socket("localhost",8000);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }catch (IOException exc){
            exc.printStackTrace();
        }
        new Thread(() -> {
            try {
                //writes the start position of the tank to Server
                output.writeInt(tank.getX());
                output.writeInt(tank.getY());
                while (true) {
                    //reads string from the server if string is OVER then client window closes and game ends
                    String s = input.readUTF();
                    if(s.equals("over")) {
                        Platform.runLater(stage::close);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void moveClient(char ch) throws IOException {
        //writes direction of the tank
        output.writeChar(ch);
    }
    private Position getValidPosition(){
        //three valid positions created to place the tank on those positions
        Position[] positions = new Position[]{new Position(1, 9), new Position(11, 9),
                new Position(1, 11)};
        return positions[0];
    }
    private void distractBrick(int i, int j){
        //distract brick on the front
        ArrayList<Brick> bricks = map.getBricks();
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
