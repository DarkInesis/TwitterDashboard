package TseInfo6.TwitterDashboard;

import java.io.IOException;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
	private static class SingletonHolder {
		private static final App instance = new App();
	}

	public static App getApp() {
		return SingletonHolder.instance;
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Twitter Dashboard");
		primaryStage.getIcons()
				.add(new Image("http://icons.iconarchive.com/icons/sicons/basic-round-social/512/twitter-icon.png"));
		Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
		primaryStage.setScene(new Scene(root, 1200, 600));
		// primaryStage.setFullScreen(true);
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(1200);
		primaryStage.show();

		//To close properly the Application
		primaryStage.setOnCloseRequest(event -> {
			DatabaseManager.getManager().close();
			Platform.exit();
			System.exit(0);
		});
	}

	//Close manager if needed
	@Override
	public void stop() {
		DatabaseManager.getManager().close();
	}
}
