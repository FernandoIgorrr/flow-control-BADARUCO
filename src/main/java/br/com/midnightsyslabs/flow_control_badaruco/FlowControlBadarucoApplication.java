package br.com.midnightsyslabs.flow_control_badaruco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

@SpringBootApplication
public class FlowControlBadarucoApplication extends Application {

	private ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() {
		applicationContext = SpringApplication.run(getClass());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.getIcons().add(
				new Image(getClass().getResourceAsStream("/images/logo.png")));

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		double width = screenBounds.getWidth() * 0.9;
		double height = screenBounds.getHeight() * 0.9;

		Scene scene = new Scene(fxmlLoader.load(), width, height);

		// primaryStage.setMaximized(true);
		primaryStage.setTitle("Flow Control - BADARUCO");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() {
		applicationContext.close();
		Platform.exit();
	}

}
