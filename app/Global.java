package app;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Global {

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
