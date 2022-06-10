package project3;
import javafx.scene.image.ImageView;

public class Tree {
    private ImageView tree;
    Tree(int x, int y){
        tree = new ImageView("project3/images/trees.png");
        tree.setX(x * 40);
        tree.setY(y * 40);
        tree.setFitWidth(40);
        tree.setFitHeight(40);
    }
    ImageView getTree(){
        return tree;
    }
}

