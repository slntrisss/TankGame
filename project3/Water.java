package project3;
import javafx.scene.image.ImageView;

public class Water {
    private ImageView water;
    Water(int x, int y){
        water = new ImageView("project3/images/water.jpg");
        water.setX(x * 40);
        water.setY(y * 40);
        water.setFitHeight(40);
        water.setFitWidth(40);
    }

    ImageView getWater() {
        return water;
    }
}