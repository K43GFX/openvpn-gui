package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.NetworkInterface;

import javax.swing.JOptionPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class PrimaryController{

    @FXML
    private Label externalIP;

    @FXML
    private Label internalIP;
    
    @FXML
    private JFXButton connectButton;

    @FXML
    private JFXButton terminateOVPN;
    
    @FXML
    private Label plskilme;
    
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
    	
    	//checking if OpenVPN running
    	
    	if(ovpnRunning()) {
    		//yup
        	configList.setVisible(false);
        	connectButton.setVisible(false);
        	plskilme.setText("OpenVPN-GUI väline tunneliteenus juba töötab. Palun sulgege see.");
        	
    	} else {
    		//nope. 
    		plskilme.setVisible(false);
    		terminateOVPN.setVisible(false);
    		
    		//adding config files to combobox
    		populateConfigs();
    	}
    }
    
    public void populateConfigs() {
    	
    	//again...
		String home_dir = System.getProperty("user.home");
		String ovpn_dir = home_dir + "/.openvpn-gui/";
		
		File dir = new File(ovpn_dir);
		File[] cfg_files = dir.listFiles((d, name) -> name.endsWith(".ovpn"));
			
		//clearing previous entries before adding new ones to list
		//configList.getSelectionModel().clearSelection();
		//configList.getItems().clear();
		
		//adding new config files to list
		for (File ovpn : cfg_files) {
			System.out.println("Found config " + ovpn + ". Adding to list.");
			configList.getItems().add(new Label(ovpn.getName()));    
		}
		
    }
    
    public boolean ovpnRunning() {
    	
    	//I usually have vpn tunnel on tun0 or tun0:00. Let's check if it's established.
    	try {
    		NetworkInterface networkInterface = NetworkInterface.getByName("tun0");
    		NetworkInterface networkInterface_00 = NetworkInterface.getByName("tun0:00");
    		
    		if(networkInterface != null) {
    			System.out.println("Discovered interface on tun0. (asking user to kill it)");
    			return true;
    		} else if(networkInterface_00 != null) {
    			System.out.println("Discovered interface on tun0:00 (asking user to kill it)");
    			return true;
    		} else {
    			System.out.println("No tun0 or tun0:00 interface found. Good.");
    			return false;
    		}
    		
		} catch (Exception kala) {
			System.out.println("Couldn't get network interfaces: " + kala);
			return false;
		}
    }
    @FXML
    void terminateOVPN(ActionEvent event) {
    	
    	//It could be previous openvpn. kill().
		try {
			
		ProcessBuilder ps = new ProcessBuilder("killall", "openvpn");
		ps.redirectErrorStream(true);
		Process pr = ps.start();  

		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

		String line = in.readLine();
		
		if(line == null) {
			System.out.println("Tunnel process killed successfully");
		}
		    
		pr.waitFor();
		in.close();

		} catch(Exception kala) {
			JOptionPane.showMessageDialog(null, "Ei saa sulgeda eelmist tunneli ühendust.");
			System.out.println(kala);
			System.exit(0);
		}
		
		//resetting view
		plskilme.setVisible(false);
		terminateOVPN.setVisible(false);
		connectButton.setVisible(true);
		configList.setVisible(true);
		
		//adding config entries
		populateConfigs();
		
		System.out.println("Showing main interface");
    	
    }

}