package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Global {
	
	public static String getExternalIP() {
		
		String ip;
		try {
			URL aws = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(aws.openStream()));

			ip = in.readLine(); //gotcha
			//System.out.println("Discovered external ip: " + ip);
			
		} catch(Exception kala) {
			ip = "N/A";
		}
		
		return ip;
	}
	
	public static String getInternalIP() {

		String ip;
		try {
			
			@SuppressWarnings("resource")
			final DatagramSocket socket = new DatagramSocket();
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ip = socket.getLocalAddress().getHostAddress();

		} catch(Exception kala) {
			System.out.println("putsis");
			ip = "127.0.0.1"; //lol
		}
		//System.out.println("Discovered internal ip: " + ip);
		return ip;
	}
	
	//mingi kala on FX threadiga, ei saa kasutada seda?
	public static void somethingHappened(String title, String header, String message, Boolean type) {

			if(type = false) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(message);
				alert.showAndWait();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(message);
				alert.showAndWait();
				//System.exit(0);
			}
	}
}
