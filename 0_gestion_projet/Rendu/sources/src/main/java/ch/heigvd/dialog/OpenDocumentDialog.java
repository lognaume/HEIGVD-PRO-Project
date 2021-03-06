/**
 * Fichier: OpenDocumentDialog.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.dialog;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * <h1>OpenDocumentDialog</h1>
 *
 * Shows a dialog that allow users to open a project file. (*.gemms)
 */
public class OpenDocumentDialog {

    private final FileChooser fileChooser;
    private final Stage stage;

    /**
     * Constructor
     *
     * @param s stage for FileChooser
     */
    public OpenDocumentDialog(Stage s) {
        stage = s;

        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GEMMS", "*.gemms"));
    }

    /**
     * Shows a new file open dialog
     *
     * @return File
     */
    public File showAndWait() {
        return fileChooser.showOpenDialog(stage);
    }
}
