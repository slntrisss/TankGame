package project3;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class BotPlayer implements Runnable{
    private int x;
    private int y;
    private ImageView bot;
    private Map map;
    private int lives = 1;
    private LinkedList<Character> path;
    private Lock lock;
    private Condition cross;
    private boolean isAlive = true;
    //Creates botplayer
    BotPlayer(Map map,Lock lock, Condition cross){
        this.map = map;
        this.cross = cross;
        this.lock = lock;
        //Initiates bot
        initBot();
    }
    public ImageView getBot(){
        return bot;
    }

    //sets aliveness of the bot
    public void setAlive(boolean alive) {
        synchronized (this) {
            isAlive = alive;
        }
    }
    //delcares whether bot is alive or not
    public boolean isAlive() {
        synchronized (this) {
            return isAlive;
        }
    }

    private void initBot(){
        //Set initial positions for enemy tanks
        Position[] enemyPositions = new Position[]{new Position(1, 1),
                new Position(6, 1), new Position(11, 1)};
        bot = new ImageView("project3/images/tank2.png");
        //randomly selects positions
        Position initEnemy = enemyPositions[(int)(Math.random() * 3)];
        x = initEnemy.getX();
        y = initEnemy.getY();
        //sets sizes of the bot
        bot.setX(x * 40);
        bot.setY(y * 40);
        bot.setFitWidth(40);
        bot.setFitHeight(40);
        Platform.runLater(() -> {
            //to prevent data corruption we use syncronization
            synchronized (this) {
                //modifies the map to indicate that there is a bot in the map
                map.modifyMap(initEnemy.getY(), initEnemy.getX(), 'b');
                //removes and adds trees again to hide the tank under them
                map.getTrees().forEach(e -> {
                    map.getChildren().remove(e);
                });
                Server.players.forEach(e -> {
                    map.getChildren().remove(e.getTank().getTank());
                });
                Server.players.forEach(e -> {
                    map.getChildren().add(e.getTank().getTank());
                });
                map.getChildren().add(bot);
                map.getTrees().forEach(e -> map.getChildren().add(e));
            }
        });
        bot.setVisible(false);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void run(){
        bot.setVisible(true);
        //add bullets to tanks
        addBullets();
        //while bots are alive moves them towards players
        while(isAlive) {
            //every time players and tanks move calcultes path to players
            getPath();
            for (Character e : path) {
                if(!isAlive)
                    break;
                //if U then moves tank to up
                if (e == 'U') {
                    bot.setRotate(180);
                    //to prevent data corruption we use syncronization
                    lock.lock();
                    try {
                        //waits untill brick is distracted
                        while (map.getValueAt(y - 1,x) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    //moves tank
                    Platform.runLater(() -> {
                        bot.setY(bot.getY() - 40);
                        lock.lock();
                        map.modifyMap(y,x,'0');
                        --y;
                        map.modifyMap(y,x, 'b');
                        lock.unlock();
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                //moves tank to right
                if (e == 'R') {
                    bot.setRotate(-90);
                    //to prevent data corruption we use syncronization
                    lock.lock();
                    try {
                        //waits untill brick is distracted
                        while (map.getValueAt(y,x + 1) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setX(bot.getX() + 40);
                        lock.lock();
                        map.modifyMap(y,x,'0');
                        ++x;
                        map.modifyMap(y,x, 'b');
                        lock.unlock();
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                //moves tank to down
                if (e == 'D') {
                    bot.setRotate(0);
                    //to prevent data corruption we use syncronization
                    lock.lock();
                    try {
                        //waits untill brick is distracted
                        while (map.getValueAt(y + 1,x) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setY(bot.getY() + 40);
                        lock.lock();
                        map.modifyMap(y,x,'0');
                        ++y;
                        map.modifyMap(y,x, 'b');
                        lock.unlock();
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                //moves tank to left
                if (e == 'L') {
                    bot.setRotate(90);
                    //to prevent data corruption we use syncronization
                    lock.lock();
                    try {
                        //waits untill brick is distracted
                        while (map.getValueAt(y,x - 1) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setX(bot.getX() - 40);
                        lock.lock();
                        map.modifyMap(y,x,'0');
                        --x;
                        map.modifyMap(y,x, 'b');
                        lock.unlock();
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
            }
        }
        bot.setVisible(false);
    }
    //add bullets
    private void addBullets(){
        new Thread(() -> {
            //while bot is alive keeps creating bullets to kill players
            while (isAlive) {
                Platform.runLater(() -> {
                    BotBullet botBullet = new BotBullet(map, this, lock, cross);
                    map.getChildren().add(botBullet.getBullet());
                });
                try {
                    //shoots every 1 second
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getPath(){
        //destination x
        int destX = 0;
        //destination y
        int destY = 0;
        //marks visited cell to not visit them again
        boolean[][] visited = new boolean[map.getSize()][map.getSize()];
        //stores path in char format
        path = new LinkedList<>();
        //stores path to players in decimal coordinate format
        Position[][] predecessor = new Position[map.getSize()][map.getSize()];
        //caluclates path to players
        bfs(map.getMap(), predecessor, visited, x, y, destX, destY);
        //System.out.println(path);
    }
    /*########################---BFS---#######################*/
    private void bfs(char[][] map, Position[][] predecessor,
                     boolean[][] visited, int x, int y, int destX, int destY){
        Queue<Position> q = new LinkedList<>();
        q.add(new Position(x, y));
        int dx , dy;
        while(!q.isEmpty()){
            Position pos = q.remove();
            x = pos.getX();
            y = pos.getY();
            visited[y][x] = true;
            if(map[y][x] == 'P') {
                destX = x; destY = y;
                int stX = destX, stY = destY;
                while(predecessor[destY][destX] != null){
                    Position position = predecessor[destY][destX];
                    destX = position.getX();
                    destY = position.getY();
                    if(stX > destX && stY == destY){
                        path.add('R');
                        stX = destX;
                    }
                    else if(stX == destX && stY < destY){
                        path.add('U');
                        stY = destY;
                    }
                    else if(stX < destX && stY == destY){
                        path.add('L');
                        stX = destX;
                    }
                    else if(stX == destX && stY > destY){
                        path.add('D');
                        stY = destY;
                    }
                }
                Collections.reverse(path);
                return;
            }
            //search Up
            dx = 0; dy = -1;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Right
            dx = 1; dy = 0;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Down
            dx = 0; dy = 1;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Left
            dx = -1; dy = 0;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
        }
    }
    //Validates if tank can go further
    private boolean isValid(int i, int j, char[][] map){
        return map[i][j] != 'S' && map[i][j] != 'W' && map[i][j] != '#';
    }
    //Validates if tank can go further
    private boolean canCross(boolean[][] visited, int y, int x,char[][] map){
        return !visited[y][x] && isValid(y, x, map);
    }
    //    private void drawPath(ArrayList<Position> list, int[][] path, int count){
//        for(Position e: list){
//            path[e.getY()][e.getX()] = count;
//            count++;
//        }
//    }
    //return lives of the bot
    public int getLives() {
        synchronized (this) {
            return this.lives;
        }
    }
    //decreases lives of the bot everu=y time when bullet of player hits him
    public void decreaseLives(){
        lock.lock();
        --lives;
        lock.unlock();
    }
}
