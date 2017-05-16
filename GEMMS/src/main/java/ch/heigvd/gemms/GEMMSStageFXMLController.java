package ch.heigvd.gemms;

import ch.heigvd.dialog.ImportImageDialog;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.tool.Brush;
import ch.heigvd.tool.Selection;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Pair;

public class GEMMSStageFXMLController implements Initializable {
    
    // Stage from main
    private Stage stage;

    
    /**
     * GridPanes containing the tools buttons
     */
    @FXML
    private GridPane gridCreationTools;
    @FXML
    private GridPane gridDrawingTools;
    @FXML
    private GridPane gridColorTools;
    @FXML
    private GridPane gridFilterTools;
    @FXML
    private GridPane gridSliders;
    @FXML
    private GridPane gridModificationTools;
    
    
    // Contains all workspace (tab)
    @FXML
    private TabPane workspaces;
    
    
    @FXML
    private GridPane layerController;
    

    // List of documents
    private ArrayList<Document> documents;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridCreationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));

        // Document list
        documents = new ArrayList<>();
        
        
        // Tab changed action
        workspaces.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
           Workspace w = getCurrentWorkspace();
           if(w != null) {
                layerController.getChildren().clear();
                layerController.getChildren().add(w.getWorkspaceController());
            }
            // Suppress tab
            else {
                // Get workspace
                w = (Workspace)t.getContent();
                
                // Research document with workspace
                Document d = getDocument(w);
                documents.remove(d);
                
                // TODO : save when close ?
                
                // Clear
                layerController.getChildren().clear();
            }
        });
        
        // Create text button action
        Button textCreation = createToolButton("", gridCreationTools);
        textCreation.getStyleClass().add(CSSIcons.TEXT_CREATION);
        textCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.addLayer(new GEMMSText(50, 50, "Ceci est un texte"));
            }
        });

        // Create canvas button action
        Button canvasCreation = createToolButton("", gridCreationTools);
        canvasCreation.getStyleClass().add(CSSIcons.CANVAS_CREATION);
        canvasCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.addLayer(new GEMMSCanvas(w.width(), w.height()));  
            }
        });

        // Create image button action
        Button imageCreation = createToolButton("", gridCreationTools);
        imageCreation.getStyleClass().add(CSSIcons.IMAGE_CREATION);
        imageCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                ImportImageDialog dialog = new ImportImageDialog(stage);
                Image image = dialog.showAndWait();
                if(image != null) {
                    GEMMSImage i = new GEMMSImage(image);
                    i.setViewport(new Rectangle2D(0, 0, w.width(), w.height()));
                    w.addLayer(i);
                }
            }
        });

        // Create symetrie horizontal button action
        createToolButton("Sym hori", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth()/2,node.getBoundsInParent().getHeight()/2,0,Rotate.Y_AXIS));
                }
            }
        });

        // Create symetrie vertical button action
        createToolButton("Sym vert", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth()/2,node.getBoundsInParent().getHeight()/2,0,Rotate.X_AXIS));
                }
            }
        });
        
        // Create brush tool
        Button brush = createToolButton("", gridDrawingTools);
        brush.getStyleClass().add(CSSIcons.BRUSH);
        brush.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Brush(w));
            }
        });
        
        // Create selection button action
        createToolButton("Se", gridModificationTools).setOnAction((ActionEvent e) -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Selection(stage.getScene(), w));
            }
        });
        
        // Create filter button
        createToolButton("B&W", gridFilterTools).setOnAction((ActionEvent e) -> {
            Workspace w = (Workspace) workspaces.getSelectionModel().getSelectedItem().getContent();
            ColorAdjust c = new ColorAdjust();
            c.setSaturation(-1);
            for (Node n : w.getCurrentLayers()) {
                n.setEffect(c);
            }
        });
        
        
        //Create various sliders
        final Slider opacity = new Slider(0, 1, 1);
        final Slider sepia = new Slider(0, 1, 0);
        final Slider saturation = new Slider(-1, 1, 0);
        
        final Label opacityLabel = new Label("Opacity:");
        final Label sepiaLabel = new Label("Sepia:");
        final Label saturationLabel = new Label("Saturation:");
        
        final Label opacityValue = new Label(
                Double.toString(opacity.getValue()));
        final Label sepiaValue = new Label(
                Double.toString(sepia.getValue()));
        final Label saturationValue = new Label(
                Double.toString(saturation.getValue()));

        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {

                Workspace w = getCurrentWorkspace();
                if (w != null) {

                    for (Node n : w.getCurrentLayers()) {
                        n.setOpacity(new_val.doubleValue());
                    }
                }
                opacityValue.setText(String.format("%.2f", new_val));

            }
        });

        sepia.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {

                    for (Node n : w.getCurrentLayers()) {
                        n.setEffect(new SepiaTone(new_val.doubleValue()));
                    }
                }
                sepiaValue.setText(String.format("%.2f", new_val));

            }
        });

        saturation.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                ColorAdjust c = new ColorAdjust();
                c.setSaturation(new_val.doubleValue());
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        n.setEffect(c);
                    }
                }
                saturationValue.setText(String.format("%.2f", new_val));
            }
        });

        gridSliders.setPadding(new Insets(10, 10, 10, 10));
        createSlider(gridSliders, opacityLabel, opacity, opacityValue, 1);
        createSlider(gridSliders, sepiaLabel, sepia, sepiaValue, 2);
        createSlider(gridSliders, saturationLabel, saturation, saturationValue, 3);

        
    }

    /**
     * Create a tool button and add it in the corresponding grid pane
     * @param text  text of the button
     * @param pane  grid pane to add the button
     * @return the button created
     */
    // TODO: replace text by an image
    private Button createToolButton(String text, GridPane pane) {
        Button button = new Button(text);

        // Calculate the button's position in the grid
        int row = pane.getChildren().size() / 3;
        int col = pane.getChildren().size() % 3;

        // Add a buttons row if needed
        if (row > pane.getRowConstraints().size() - 1) {
            pane.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        }

        button.setPrefHeight(Double.MAX_VALUE);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(0, 0, 0, 0));
        button.getStyleClass().add("tool-button");

        pane.add(button, col, row);

        return button;
    }
    
    
    @FXML
    private void newButtonAction(ActionEvent e) {
        
        // Create a new dialog
        NewDocumentDialog dialog = new NewDocumentDialog();
        
        // Display dialog
        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();

        // Dialog OK
        if(result.isPresent()) {

            // Create a new document
            Document document = new Document(stage, result.get().getKey(), result.get().getValue());

            // Get workspace
            Workspace w = document.workspace();

            // Clear
            layerController.getChildren().clear();
            layerController.getChildren().add(w.getWorkspaceController());

            // Create tab
            Tab tab = new Tab("untitled", w);
            workspaces.getTabs().add(tab);
            workspaces.getSelectionModel().select(tab);

            documents.add(document);
        }
    }
    
    
    @FXML
    private void openButtonAction(ActionEvent e) {
        OpenDocumentDialog dialog = new OpenDocumentDialog(stage);
        
        File f = dialog.showAndWait();
        if(f != null) {
            Document document = null;
            try {
                document = new Document(stage, f);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Workspace w = document.workspace();

            // Clear
            layerController.getChildren().clear();
            layerController.getChildren().add(w.getWorkspaceController());

            // Create tab
            Tab tab = new Tab(document.name(), w);
            workspaces.getTabs().add(tab);
            workspaces.getSelectionModel().select(tab);

            documents.add(document);
        }
    }
    
    
    @FXML
    private void saveButtonAction(ActionEvent e) {
        Workspace w = getCurrentWorkspace();
        if(w != null) {
            // Get current tab
            Tab tab = workspaces.getSelectionModel().getSelectedItem();

            // Research document with workspace
            Document d = getDocument(w);

            // Save document
            if(d != null) {
                try {
                    d.save();
                } catch (IOException ex) {
                    Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Set tab title
            tab.setText(d.name());
        }
    }
    
    
    @FXML
    private void exportButtonAction(ActionEvent e) {
        Workspace w = getCurrentWorkspace();
        if(w != null) {
            // Research document with workspace
            Document d = getDocument(w);

            // export document as image
            if(d != null) {
                try {
                    d.export();
                } catch (IOException ex) {
                    Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
    /**
     * Set the main stage
     * 
     * @param stage stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Get the current workspace displayed
     * 
     * @return current workspace
     */
    private Workspace getCurrentWorkspace() {
        if (workspaces.getTabs().size() > 0) {
            return (Workspace) workspaces.getSelectionModel().getSelectedItem().getContent();
        }
        
        return null;
    }
    
    /**
     * Get document that contain the workspace
     * 
     * @param w workspace to find
     * @return Document that contain the workspace
     */
    private Document getDocument(Workspace w) {
        for(Document d : documents) {
            if(d.workspace() == w) {
               return d;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a slider in a pane at a certain position. Used to create opacity,
     * sepia and saturation sliders.
     * @param gridSliders
     * @param opacityLabel
     * @param opacity
     * @param opacityValue 
     */
    private void createSlider(GridPane pane, Label label, Slider opacity, Label value, int position) {
        label.setMinWidth(50);
        value.setMinWidth(30);
        GridPane.setConstraints(label, 0, position);
        pane.getChildren().add(label);
        GridPane.setConstraints(opacity, 1, position);
        pane.getChildren().add(opacity);
        GridPane.setConstraints(value, 2, position);
        pane.getChildren().add(value);

    }
}
