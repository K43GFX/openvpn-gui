package app;
import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class PrimaryController {

    @FXML
    private JFXButton connectButton;

    @FXML
    private Hyperlink ConfLink;

    @FXML
    void establishConnection(ActionEvent event) {
    	System.out.println("Proov");
    }

    @FXML
    void openConfLink(ActionEvent event) {
    	
    }

}