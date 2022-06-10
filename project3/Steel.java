package project3;
import javafx.scene.image.ImageView;

public class Steel {
    private ImageView steel;
    Steel(int x, int y){
        steel = new ImageView("project3/images/steel.jpg");
        steel.setFitHeight(40);
        steel.setFitWidth(40);
        steel.setX(x * 40);
        steel.setY(y * 40);
    }

    ImageView getSteel() {
        return steel;
    }
}
