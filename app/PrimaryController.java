package app;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class PrimaryController {

    @FXML
    private Label externalIP;

    @FXML
    private Label internalIP;
    
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
    	
    	//Opening configuration directory
    	
		String home_dir = System.getProperty("user.home");
		String ovpn_dir = home_dir + "/.openvpn-gui/";
		
		try {
			Runtime.getRuntime().exec("nautilus " + ovpn_dir);
			System.out.println("Successfully opened conf dir");
		} catch(Exception kala) {
			System.out.println(kala);
			System.out.println("Conf dir open failed: " + kala);
		}
    	
    }

}