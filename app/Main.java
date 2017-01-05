package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("Primary.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("OpenVPN-GUI");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// let's check if we have everything to run this piece of shit
		String home_dir = System.getProperty("user.home");
		String ovpn_dir = home_dir + "/.openvpn-gui/";

		// checking if openvpn-gui existing
		if (Files.isDirectory(Paths.get(ovpn_dir))) {

			// not a first launch
			System.out.println(ovpn_dir + " existing");
		} else {
			System.out.println(ovpn_dir + " not existing, creating one");
			try {
				Files.createDirectories(Paths.get(ovpn_dir));
				System.out.println(ovpn_dir + " successfully created");
			} catch (Exception kala) {
				System.out.println("Could not create conf directory: " + kala);
				JOptionPane.showMessageDialog(null, "Ei suuda luua vajalikku kataloogi konfiguratsioonifailide jaoks.");
				System.exit(0);

			}
		}

		// checking if we have openvpn installed
		try {

			ProcessBuilder ps = new ProcessBuilder("which", "openvpn");
			ps.redirectErrorStream(true);
			Process pr = ps.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = in.readLine();

			if (line != null) {
				System.out.println("Openvpn at " + line);
			} else {
				System.out.println(line);
				JOptionPane.showMessageDialog(null, "Programm vajab oma tööks OpenVPN programmi.");
				System.exit(0);
			}

			pr.waitFor();
			in.close();

		} catch (Exception kala) {
			JOptionPane.showMessageDialog(null,
					"Ei suuda tuvastada OpenVPN asukohta. Mitte et mul seda ilmtingimata vaja oleks, aga ikkagi ei suuda.");
			System.exit(0);
		}

		// Launch UI
		System.out.println("Firing up our GUI.");
		launch(args);
	}
}
