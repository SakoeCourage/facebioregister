package application;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public class Uitility {
	
    public static void setControlsDisabled(boolean disabled, Control... controls) {
        for (Control control : controls) {
            if(control != null) {
            	control.setDisable(disabled);
            }
        }
    }
    
    
    public static void setTextControlsToEmpty( TextField... txtfields) {
        for (TextField tf : txtfields) {
            if(tf != null) {
            	tf.setText("");
            }
        }
    }
    
    public static boolean areAnyFieldsEmpty(TextField... textFields) {
        for (TextField textField : textFields) {
            if (textField.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    
    public static void showMessages(Pane pane,String message,boolean error,EventHandler<ActionEvent> buttonClickHandler) {
    		pane.getChildren().clear();
    		HBox hb = new HBox();
    		Label lbl = new Label();
    		SVGPath svgPath = new SVGPath();
    		if(error == false) {
    			Platform.runLater(()->{
    				svgPath.setContent("M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm.93-9.412-1 4.705c-.07.34.029.533.304.533.194 0 .487-.07.686-.246l-.088.416c-.287.346-.92.598-1.465.598-.703 0-1.002-.422-.808-1.319l.738-3.468c.064-.293.006-.399-.287-.47l-.451-.081.082-.381 2.29-.287zM8 5.5a1 1 0 1 1 0-2 1 1 0 0 1 0 2z");
        			pane.setStyle("-fx-background-color:#0891b2;");
    			});
    			
    		}else if(error == true) {
    			Platform.runLater(()->{
    				svgPath.setContent("M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm.93-9.412-1 4.705c-.07.34.029.533.304.533.194 0 .487-.07.686-.246l-.088.416c-.287.346-.92.598-1.465.598-.703 0-1.002-.422-.808-1.319l.738-3.468c.064-.293.006-.399-.287-.47l-.451-.081.082-.381 2.29-.287zM8 5.5a1 1 0 1 1 0-2 1 1 0 0 1 0 2z");
        			pane.setStyle("-fx-background-color:#dc2626;");	
    			});
    		}
    		
    		SVGPath closeSvg = new SVGPath();
    		closeSvg.setContent("M16 2C8.2 2 2 8.2 2 16s6.2 14 14 14s14-6.2 14-14S23.8 2 16 2zm5.4 21L16 17.6L10.6 23L9 21.4l5.4-5.4L9 10.6L10.6 9l5.4 5.4L21.4 9l1.6 1.6l-5.4 5.4l5.4 5.4l-1.6 1.6z");
    		closeSvg.setStyle("-fx-background-color:ff0000;");
    		Button closeNotificationButton = new Button("close");
    		closeNotificationButton.setStyle("-fx-text-fill: ffffff;"
    				+ "-fx-background-color:transparent;");
    		
    		closeNotificationButton.setLayoutX(pane.getWidth() - pane.getWidth()); 
    		closeNotificationButton.setLayoutY(0);
    		closeNotificationButton.setOnAction(buttonClickHandler);
    		closeNotificationButton.setGraphic(closeSvg);
    		closeNotificationButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    		
    		svgPath.setStyle("-fx-fill: #ffffff;");
    		lbl.setText(message);
    		lbl.setStyle("-fx-text-fill: #ffffff;"
    				+ "-fx-font-size: 15;");
    		lbl.setWrapText(true);
    		hb.setSpacing(5);
    		hb.getChildren().addAll(svgPath,lbl);
    		hb.setStyle("-fx-padding: 25;");
    		hb.setMaxWidth(pane.getWidth());
    		lbl.setMaxWidth(Double.MAX_VALUE);
    		
    		pane.getChildren().add(hb);
    		pane.getChildren().add(closeNotificationButton);
    		closeNotificationButton.toFront();
    }
    
    public static void checkImagesFolder() {
        String userHome = System.getProperty("user.home");
        String documentsPath = userHome + File.separator + "Documents";

        String facesFolderPath = documentsPath + File.separator + ConfigKeys.fileDefaultFolder + File.separator + "images" + File.separator + "faces";
        File facesFolder = new File(facesFolderPath);

        if (!facesFolder.exists()) {
            boolean folderCreated = facesFolder.mkdirs();
            if (folderCreated) {
                Configuration.setPreference(ConfigKeys.facesDirectory, facesFolder.toString());
            } else {
                // Handle the case where folder creation failed
            }
        } else {
            Configuration.setPreference(ConfigKeys.facesDirectory, facesFolder.toString());
        }
        
        String tempFaceFolder = documentsPath + File.separator + ConfigKeys.fileDefaultFolder + File.separator + "images" + File.separator + "tempface";
        File tempFacesFolder = new File(tempFaceFolder);

        if (!tempFacesFolder.exists()) {
            boolean folderCreated = tempFacesFolder.mkdirs();
            if (folderCreated) {
                Configuration.setPreference(ConfigKeys.tempFaces, tempFacesFolder.toString());
            } else {
                // Handle the case where folder creation failed
            }
        } else {
            Configuration.setPreference(ConfigKeys.tempFaces, tempFacesFolder.toString());
        }
    }

    

}
