package project3;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(){}

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Position p){
        return this.x == p.getX() && this.y == p.getY();
    }
    public String toString(){
        return "(" + getX() + "," + getY() + ")";
    }
}

