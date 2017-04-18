package main;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application implements Controller {
	
	static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		MainApp.primaryStage = primaryStage;
		primaryStage.setTitle("Hamming");

		initClientLayout();		
	}
	
	private void initClientLayout() {
		loadScene(primaryStage, 572, 592, false, 0, 0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}