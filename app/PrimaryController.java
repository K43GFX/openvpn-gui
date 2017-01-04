package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.NetworkInterface;

import javax.swing.JOptionPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private JFXSpinner LoaderSpinner;

    @FXML
    private Label LoaderStatus;

    @FXML
    private Label LoaderTitle;
    
    @FXML
    private JFXComboBox<Label> configList;
    
	@FXML
    void establishConnection(ActionEvent event) throws InterruptedException {
    	
		
    	String chosen_config = configList.getSelectionModel().getSelectedItem().getText().toString();
    	System.out.println("-------");
    	System.out.println("Trying to establish connection to " + chosen_config);
    	
    	//hiding unneeded elements
    	connectButton.setVisible(false);
    	configList.setVisible(false);
    	ConfLink.setVisible(false);
    	
    	//and showing stuff we need
    	LoaderSpinner.setVisible(true);
    	LoaderTitle.setVisible(true);
    	LoaderStatus.setVisible(true);
    	
    	//changing text of labels
    	LoaderTitle.setText("Tunneli loomine serverisse " + chosen_config + "...");
    	LoaderStatus.setText("loon ühendust serveriga");
    	
		
    	//launching openvpn daemon
    	new Thread(ovpnDaemon).start();
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
			
		} catch (Exception kala) {
			System.out.println(kala);
			System.out.println("Conf dir open failed: " + kala);
		}
    	
    }
    @FXML
    public void initialize() {
    	
    	
    	//Setting IP's in UI
    	externalIP.setText(Global.getExternalIP());
    	internalIP.setText("Sisevõrgu IP: " + Global.getInternalIP());
    	
    	//hiding some items that we don't need at the moment
    	LoaderSpinner.setVisible(false);
    	LoaderTitle.setVisible(false);
    	LoaderStatus.setVisible(false);
    	
    	
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
    		
    		//automatically selecting first one
    		configList.getSelectionModel().select(0);
    		String chosen_config = configList.getSelectionModel().getSelectedItem().getText().toString();
    		System.out.println("Selected " + chosen_config + " as default option");
    	}
    }
    
    public void populateConfigs() {
    	
    	//again...
		String home_dir = System.getProperty("user.home");
		String ovpn_dir = home_dir + "/.openvpn-gui/";
		
		File dir = new File(ovpn_dir);
		File[] cfg_files = dir.listFiles((d, name) -> name.endsWith(".ovpn"));
		
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
    void terminateOVPN(ActionEvent event) throws InterruptedException {
    	
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
    	
		
		//adding config entries
		populateConfigs();
		
    	new Thread(updateIP).start();
		
		configList.getSelectionModel().select(0);
		String chosen_config = configList.getSelectionModel().getSelectedItem().getText().toString();
		System.out.println("Selected " + chosen_config + " as default option");
    	
		//resetting view
		plskilme.setVisible(false);
		terminateOVPN.setVisible(false);
		connectButton.setVisible(true);
		configList.setVisible(true);
		
		System.out.println("Showing main interface");
    	
    }
    
    Task<Void> ovpnDaemon = new Task<Void>() {
        @Override public Void call() {

        	String home_dir = System.getProperty("user.home");
        	String ovpn_dir = home_dir + "/.openvpn-gui/";
        	String chosen_config = configList.getSelectionModel().getSelectedItem().getText().toString();
        	
        	Platform.runLater(() -> LoaderStatus.setText("Serveriga ühenduse loomine"));
        	
    		try {
    			
    		ProcessBuilder ps = new ProcessBuilder("openvpn", ovpn_dir + chosen_config);
    		ps.redirectErrorStream(true);
    		Process pr = ps.start();  

    		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

    		String line;
    		while(!((line = in.readLine()) == null)) {
    			System.out.println(line);
    			
    			if(line.contains("Received control message")) {
    				Platform.runLater(() -> LoaderStatus.setText("Tunneli loomine"));
    			}
    			
    			if(line.contains("route add")) {
    				Platform.runLater(() -> LoaderStatus.setText("Võrguliikluse ümbersuunamine"));
    			}
    			if(line.contains("VERIFY OK:")) {
    				Platform.runLater(() -> LoaderStatus.setText("Sertifikaadi autentsuse kontrollimine"));
    			}
    			if(line.contains("Sequence Completed")) {
    				Platform.runLater(() -> LoaderStatus.setText("Tunnel edukalt loodud"));
    				break;
    			}
    		}
    		
    		//retrieving new IP's
    		Thread.sleep(1000);
    		Platform.runLater(() -> LoaderStatus.setText("Kasutajaliidese IP väärtuste uuendamine"));
    		Thread.sleep(2000);
    		Platform.runLater(() -> internalIP.setText(Global.getInternalIP()));
    		Platform.runLater(() -> externalIP.setText(Global.getExternalIP()));
    		
    		} catch(Exception kala) {
    			Platform.runLater(() -> LoaderStatus.setText("Tunneli loomine ebaõnnestus"));
    			System.exit(0);
    		}
    		return null;
        }
    };
    
    Task<Void> updateIP = new Task<Void>() {
        @Override public Void call() throws InterruptedException {

        	Thread.sleep(3000);
    		Platform.runLater(() -> internalIP.setText(Global.getInternalIP()));
    		Platform.runLater(() -> externalIP.setText(Global.getExternalIP()));
        	
    		return null;
        }
    };

}