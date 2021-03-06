/**
 * Fichier: RightController.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.controller;

import ch.heigvd.tool.ColorSet;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RightController {

    // Main controller
    private MainController mainController;

    @FXML
    private VBox layerController;

    @FXML
    private VBox historyViewer;

    @FXML
    private AnchorPane colorController;

    // Init this controller
    public void init(MainController mainController) {
        this.mainController = mainController;

        colorController.getChildren().add(ColorSet.getInstance().getColorController());
    }

    public void addLayerController(VBox b) {
        layerController.getChildren().add(b);
    }

    public void addHistoryController(ListView view) {
        historyViewer.getChildren().add(view);
    }

    public void clearLayerBox() {
        layerController.getChildren().clear();
    }

    public void clearHistoryBox() {
        historyViewer.getChildren().clear();
    }
}
