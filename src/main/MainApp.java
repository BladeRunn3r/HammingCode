package main;
import javafx.application.Application;
import javafx.stage.Stage;
import main.Controller;

public class MainApp extends Application implements Controller {
	
	public static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		MainApp.primaryStage = primaryStage;
		primaryStage.setTitle("Hamming");

		initClientLayout();		
	}
	
	private void initClientLayout() {
		loadScene(primaryStage, "Hamming.fxml", 572, 592, false, 0, 0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}