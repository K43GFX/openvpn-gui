package app;

import java.io.File;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

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
    private JFXComboBox<Label> configList;
    
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
			
			//Desktop.* not working in Ubuntu? :/
			Runtime.getRuntime().exec("nautilus " + ovpn_dir);
			System.out.println("Successfully opened conf dir");
		} catch(Exception kala) {
			System.out.println(kala);
			System.out.println("Conf dir open failed: " + kala);
		}
    	
    }
    @FXML
    public void initialize() {
    	
    	//Setting IP's in UI
    	externalIP.setText(Global.getExternalIP());
    	internalIP.setText("Sisevõrgu IP: " + Global.getInternalIP());
    	
    	//adding configuration files to combobox
    	populateConfigs();
    }
    
    public void populateConfigs() {
    	
    	//again...
		String home_dir = System.getProperty("user.home");
		String ovpn_dir = home_dir + "/.openvpn-gui/";
		
		File dir = new File(ovpn_dir);
		File[] cfg_files = dir.listFiles((d, name) -> name.endsWith(".ovpn"));
			

		for (File ovpn : cfg_files) {
			System.out.println("Found config " + ovpn + ". Adding to list.");
			configList.getItems().add(new Label(ovpn.getName()));    
		}
		
    }

}