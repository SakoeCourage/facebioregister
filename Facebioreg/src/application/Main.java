package application;
	

import java.io.InputStream;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,1350,720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());		
	        primaryStage.setTitle("Connect Solution | Face Bio Register");
			primaryStage.setScene(scene);
			InputStream inputStream = getClass().getResourceAsStream("appimages/connectsolutionlogo.png");
	       	Image image = new Image(inputStream);
			primaryStage.getIcons().add(image);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public void stop() {     
   	SceneController.handleWindowClose(); 
    }
    
    
	public static void main(String[] args) {
		launch(args);
	}
}
