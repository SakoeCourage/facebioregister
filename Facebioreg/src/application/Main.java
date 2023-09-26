package application;
	

import java.io.InputStream;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,1350,720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());		
	       
			Screen screen = Screen.getPrimary();
			
		    double screenWidth  =  screen.getBounds().getWidth();
		    double screenHeight = screen.getBounds().getHeight();
		  
	        
			InputStream inputStream = getClass().getResourceAsStream("appimages/connectsolutionlogo.png");
	       	Image image = new Image(inputStream);
	        primaryStage.setTitle("Connect Solution | Face Bio Register");
	        primaryStage.setWidth(screenWidth);
	        primaryStage.setHeight(screenHeight);
			primaryStage.setScene(scene);
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
