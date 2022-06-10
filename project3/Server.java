package project3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Server extends Application {
    private Scene scene;
    private Map map;
    static ArrayList<ClientInfo> players = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition cross = lock.newCondition();
    static int bots = 3;
    static ArrayList<ImageView> botIndicators = new ArrayList<>();
    static BotPlayer[] botPlayers = new BotPlayer[bots];
    static ArrayList<ArrayList<ImageView>> playerIndicators = new ArrayList<>();
    public void start(Stage stage) throws FileNotFoundException {
        //creates map and starts game
        map = new Map(new Scanner(new File("src/project3/images/map.txt")),14);
        map.getChildren().addAll(map.getTrees());
        scene = new Scene(map, 560,560);
        stage.setScene(scene);
        stage.setTitle("Server");
        stage.show();
        createServer();
    }
    private void createServer(){
        //creates Server
        new Thread(() -> {
            try{
                ServerSocket server = new ServerSocket(8000);
                boolean added = false;
                while(true){
                    //Every time accepts client whenver possible
                    Socket socket = server.accept();
                    new Thread(new OnlinePlayers(socket, map, players)).start();
                    //adds bots
                    if(!added){
                        addBots();
                        added = true;
                    }
                }
            }catch (IOException exc){
                exc.printStackTrace();
            }
        }).start();
    }
    private void addBots(){
        //Initiates bots
        for(int i = 0; i < bots; i++){
            botPlayers[i] = new BotPlayer(map, lock, cross);
        }
        //creates and executes thread on schedule
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(bots);
        int seconds = 0;
        //for each bot creates thread
        for(BotPlayer e: botPlayers){
//            Thread thread = new Thread(() -> {
//                while (e.isAlive()) {
//                    Platform.runLater(() -> {
//                        BotBullet bullet = new BotBullet(map, e, lock, cross);
//                        map.getChildren().add(bullet.getBullet());
//                    });
//                    try {
//                        Thread.sleep(1000L);
//                    } catch (InterruptedException exc) {
//                        exc.getCause();
//                    }
//                }
//            });
            executorService.schedule(new Thread(e), seconds, TimeUnit.SECONDS);
            //executorService.schedule(thread, seconds, TimeUnit.SECONDS);
            //every 15 seconds adds bots to game
            seconds += 15;
        }
    }
}
//Handles every client
class OnlinePlayers implements Runnable{
    private Tank tank;
    private Socket socket;
    private Map map;
    private ArrayList<ClientInfo> players;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private ClientInfo clientInfo;
    private static int numberOfClients;
    //Initiates basic configuration for client
    OnlinePlayers(Socket socket, Map map, ArrayList<ClientInfo> players){
        this.map = map;
        this.socket = socket;
        this.players = players;
        numberOfClients++;
    }
    public void run(){
        try{
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            while(true){
                //gives start positions for clients
                int startX = inputStream.readInt();
                int startY = inputStream.readInt();
                Platform.runLater(() -> {
                    //creeates client tanks
                    tank = new Tank(startX, startY, map);
                    //synchronizes to prevent data corruption
                    synchronized (this){
                        //sets stat position for tank
                        map.setStart(new Position(startX,startY));
                        map.getTrees().forEach(e -> {
                            map.getChildren().remove(e);
                        });
                        map.getChildren().add(tank.getTank());
                        map.getTrees().forEach(e -> {
                            map.getChildren().add(e);
                        });
                        System.out.println(numberOfClients);
                        //creates every time slinetinfo when new client joined
                        clientInfo = new ClientInfo(3, tank, map.getBricks(), outputStream,
                                "P" + numberOfClients, numberOfClients);
                        //adds clients to players
                        players.add(clientInfo);
                        map.modifyMap(startY, startX, 'P');
                        //draws the lives of the bots on scene
                        tank.setIndicators();
                        //draws the lives of the players on the scene
                        tank.initPlayerLives(clientInfo);
                    }
                });
                while(true){
                    //reads movement from clients
                    char movement = inputStream.readChar();
                    Platform.runLater(() -> {
                        moveTank(movement);
                    });
                }
            }
        }catch(IOException exc){
            exc.printStackTrace();
        }
    }
    private void moveTank(char movement){
        //moves client tank
        switch (movement){
            case 'U': tank.moveUp();break;
            case 'R': tank.moveRight();break;
            case 'D': tank.moveDown();break;
            case 'L': tank.moveLeft();break;
            case 'S': {
                Platform.runLater(() -> {
                    //creates bullet when clients shoot
                    Bullet bullet = new Bullet(map, tank, players,outputStream);
                    synchronized (this) {
                        map.getChildren().remove(tank.getTank());
                        map.getChildren().removeAll(map.getTrees());
                        map.getChildren().addAll(bullet.getBullet(), tank.getTank());
                        map.getChildren().addAll(map.getTrees());
                    }
                });break;
            }
        }
    }
}
