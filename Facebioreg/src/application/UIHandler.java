package application;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


import java.io.ByteArrayInputStream;
import java.sql.Blob;

import application.Apiclient;
import application.Person;
import application.SceneController;

public class UIHandler {
	
	//****************************************************************************************************************
	static AnchorPane searchUserUIAnchorPane; 
	static BorderPane loadingBorderPane; 
	static TextField txtEmployeeID; 
	static Button btnSearchEmployee; 
	static Label lblErrorMessage = new Label("Error");
	static VBox mainVB;
	static VBox personVB;
	static SceneController mainSene;
	static String CurrentEmployeeID=null;
	
	
	
	//****************************************************************************************************************
	static Person currentEmployee;
	
	public UIHandler(SceneController sc) {
		mainSene = sc;
	}
	
	static {
		setTxtEmployee();
		setBtnSearchEmployee();
		setSearchAnchorPane();
		setLoadingBorderPane();
//		ShowpersonProfile();
		
	}
	
	
	public static void setErrorMessage(String Text) {
		Platform.runLater(()->{
			lblErrorMessage.setText(Text);
			lblErrorMessage.setVisible(true);
		});
	}
	
	public static void hideErrorMessage() {
		Platform.runLater(()->{
			lblErrorMessage.setVisible(false);
		});
	}
	
	public static void handleEmployeeDataReset() {
		currentEmployee = null;
		CurrentEmployeeID=null;
		Platform.runLater(()->{
			if(personVB != null) personVB.getChildren().clear();
		});
	}
	
	
	
	public static void searchEmployee() {
		showLoadingAnchorPane();
    	CompletableFuture<Void> fetchEmployeeFuture = CompletableFuture.runAsync(() -> {
		    try {
		    	if(txtEmployeeID.getText() != "") {
					try {
				 hideErrorMessage();
				 showLoadingAnchorPane();
				 handleEmployeeDataReset();
				 currentEmployee = Apiclient.fetchEmployee(txtEmployeeID.getText());
			
				} catch (java.lang.Exception e) {							
						setErrorMessage(e.getMessage());
						hideLoadingAnchorPane();
				}
		    	}	
		    } catch (Exception e) {
		    	setErrorMessage(e.getMessage());
		        hideLoadingAnchorPane();
		    }
		});
		fetchEmployeeFuture.thenRun(() -> {
		    Platform.runLater(() -> {
		    	hideLoadingAnchorPane();
		    	if(currentEmployee != null) {
					 ShowpersonProfile();
				 }
		    	
		    });
		});
    }
	
	
	public static EventHandler<ActionEvent> searchEmployeeEventHandler() {
		EventHandler<ActionEvent> searchEmployeeHandler = new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	searchEmployee();
		    } 
		};
		return searchEmployeeHandler;
	}

	public static void showLoadingAnchorPane() {
		loadingBorderPane.setVisible(true);
	}
	
	public static void hideLoadingAnchorPane() {
		loadingBorderPane.setVisible(false);
	}
	
	
	private static void setTxtEmployee() {
		txtEmployeeID = searchTF();
		txtEmployeeID.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            @Override
	            public void handle(KeyEvent event) {
	                if (event.getCode() == KeyCode.ENTER) {
	                	searchEmployee();
	                }
	            }
	        });
	}
	
	
	
	
	private static void setBtnSearchEmployee() {
		btnSearchEmployee = searchButton();	
		btnSearchEmployee.setOnAction(searchEmployeeEventHandler());
	}
	
	public VBox generateSideBarVB(AnchorPane root,String title) {
		
//		Titled Label
		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-size: 20;"
				+ "-fx-padding: 10 15 10 15;"
				+ "-fx-border-style: solid; "
				+ "-fx-border-width: 0 0 2 0;"
				+ "-fx-text-fill:#71717a;"
				+ "-fx-border-color: #d4d4d4;");
		titleLabel.setMaxWidth(Double.MAX_VALUE);
		
		VBox sideBarMainVB = new VBox();
		sideBarMainVB.setPrefWidth(400);
		sideBarMainVB.setStyle( "-fx-background-color:#f8fafc;");
		sideBarMainVB.prefHeightProperty().bind(root.prefHeightProperty());
		sideBarMainVB.getChildren().add(0, titleLabel);
		sideBarMainVB.setSpacing(10);
		return sideBarMainVB;
	}
	
	
	public static void setLoadingBorderPane() {
	    ImageView loadingImageView = new ImageView();
	    Class<?> clazz = UIHandler.class; 
	    InputStream inputStream = clazz.getResourceAsStream("appimages/loading.gif");
	    if (inputStream != null) {
	        Image image = new Image(inputStream);
	        loadingImageView.setImage(image);
	        loadingImageView.setFitWidth(172);
	        loadingImageView.setFitHeight(278);
	        loadingImageView.setPreserveRatio(true);
	    } else {
	        System.err.println("Image resource not found.");
	    }
	    BorderPane loadingBP = new BorderPane();
	    loadingBP.setStyle("-fx-background-color: rgba(255,255,255,0.7);");
	    loadingBP.setCenter(loadingImageView);
	    AnchorPane.setTopAnchor(loadingBP, 0.0); 
	    AnchorPane.setLeftAnchor(loadingBP, 0.0); 
	    AnchorPane.setRightAnchor(loadingBP, 0.0); 
	    AnchorPane.setBottomAnchor(loadingBP, 0.0); 
	    loadingBorderPane = loadingBP;
	    loadingBorderPane.toFront();
	    searchUserUIAnchorPane.getChildren().add(loadingBP);
	    hideLoadingAnchorPane();
	}

	
    public void ToggleSideBar(AnchorPane root,VBox childVB) {
    	boolean exist = root.getChildren().contains(childVB);
    	if(exist) {
    		TranslateTransition transition1 = new TranslateTransition(Duration.seconds(0.3), childVB);
    	    transition1.setInterpolator(Interpolator.EASE_OUT);
    	    transition1.setByX(500);
    	    FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.3), childVB);
    	    fadeTransition1.setFromValue(1);
    	    fadeTransition1.setToValue(0);
    	    fadeTransition1.setInterpolator(Interpolator.EASE_OUT);
    	    ParallelTransition parallelTransition = new ParallelTransition();
    	    parallelTransition.getChildren().addAll(transition1);
    	    parallelTransition.play();
    		parallelTransition.setOnFinished(event->{
    			root.getChildren().remove(childVB);
    		});

    	}else {
    		childVB.setTranslateX(500);
    		root.getChildren().add(childVB);
    		TranslateTransition transition1 = new TranslateTransition(Duration.seconds(0.3), childVB);
    	    transition1.setInterpolator(Interpolator.EASE_OUT);
    	    transition1.setByX(-500);
    	    FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.3), childVB);
    	    fadeTransition1.setFromValue(0);
    	    fadeTransition1.setToValue(1);
    	    fadeTransition1.setInterpolator(Interpolator.EASE_OUT);
    	    ParallelTransition parallelTransition = new ParallelTransition();
    	    parallelTransition.getChildren().addAll(transition1);
    	    parallelTransition.play();
    
    	}
    }
    
    
	
	public void sourceSideBar(AnchorPane root) {
		VBox rootChild = generateSideBarVB(root,"Video Source");
		AnchorPane.setRightAnchor(rootChild, 00.00);
		rootChild.toFront();
		
		VBox camListVB = new VBox();
		camListVB.setStyle("-fx-padding: 7;");
		
		ComboBox<String> cmbCamList = new ComboBox<>();
		cmbCamList.setMaxWidth(Double.MAX_VALUE);
		cmbCamList.itemsProperty().bind(Bindings.createObjectBinding(() -> CameraManager.AvailableVideoCamsList));
		
		cmbCamList.getStyleClass().add("text-input");
		camListVB.setSpacing(10);
		camListVB.getChildren().addAll(new Label("Available videos sources"),cmbCamList);
		camListVB.setMaxWidth(Double.MAX_VALUE);
		cmbCamList.setValue(Configuration.getPreference(ConfigKeys.cameraIndex, CameraManager.AvailableVideoCamsList.get(0)));
		Button btnClose = new Button("Close");
		btnClose.getStyleClass().add("btn");
		btnClose.setMaxWidth(Double.MAX_VALUE);
		rootChild.getChildren().addAll(camListVB,btnClose);
		cmbCamList.setOnAction((e)->{
			Configuration.setPreference(ConfigKeys.cameraIndex,cmbCamList.getValue().toString());
			camListVB.getChildren().add(1, new Label("Rest"));
		});
		
		btnClose.setOnAction((e)->{
			ToggleSideBar(root,rootChild);
		});
		ToggleSideBar(root,rootChild);
	}
	
	
	public void apiSideBar(AnchorPane root) {
		VBox rootChild = generateSideBarVB(root,"API EndPoint");
		AnchorPane.setRightAnchor(rootChild, 00.00);
		rootChild.toFront();
		
		VBox ListVB = new VBox();
		ListVB.setStyle("-fx-padding: 7;");
		
		TextField txtApiSource = new TextField();
		txtApiSource.setMaxWidth(Double.MAX_VALUE);
		
		txtApiSource.getStyleClass().add("text-input");
		ListVB.setSpacing(10);
		ListVB.getChildren().addAll(new Label("Api Endpoint"),txtApiSource);
		ListVB.setMaxWidth(Double.MAX_VALUE);
		txtApiSource.setText(Configuration.getPreference(ConfigKeys.apiEndPoint, null));
		Button btnClose = new Button("Close");
		btnClose.getStyleClass().add("btn");
		btnClose.setMaxWidth(Double.MAX_VALUE);
		rootChild.getChildren().addAll(ListVB,btnClose);
		
		txtApiSource.textProperty().addListener(new ChangeListener<String>() {
	            @Override
	            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	                Configuration.setPreference(ConfigKeys.apiEndPoint,newValue);
	            }
	        });
		
		btnClose.setOnAction((e)->{
			ToggleSideBar(root,rootChild);
			
		});
		ToggleSideBar(root,rootChild);
	}
	
	
	
	public static Button searchButton() {
		   Button btnSearch = new Button("Go");
		   btnSearch.setStyle("-fx-border-color: transparent transparent transparent transparent; "
			   		+ "-fx-border-width: 0 0 0 0;"
			   		+ "-fx-background-color:transparent");
		   
		   SVGPath searchGraphic = new SVGPath();
		   searchGraphic.setContent("M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0a5.5 5.5 0 0 1 11 0z");
		  
		   btnSearch.setGraphic(searchGraphic);
		   btnSearch.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		   
		   return btnSearch;
	}
	
	
	
	public static TextField searchTF()
	{
		TextField txtSearch = new TextField();
		   txtSearch.setPromptText("Search By Employee ID");
		   txtSearch.setStyle("-fx-border-color: transparent transparent transparent transparent; "
			   		+ "-fx-border-width: 0 0 0 0;");
		   HBox.setHgrow(txtSearch, Priority.ALWAYS);
		  return txtSearch;
	}	
	
	
	public static  HBox searchBar() {	   
		   HBox searchHBox = new HBox();
		   searchHBox.getChildren().addAll(txtEmployeeID,btnSearchEmployee);
		   searchHBox.setMaxWidth(Double.MAX_VALUE);
		   searchHBox.setStyle("-fx-padding: 10 3 10 3;"
		   		+ "-fx-background-radius: 5px;"
		   		+ "-fx-background-color:#f7fee7;"
		   		+ "-fx-border-width: 2;"
		   		+ "-fx-border-color: #d9f99d;"
		   		+ "-fx-border-radius: 5px;"
		   		+ "-fx-margin:5;");
		   
		   AnchorPane.setTopAnchor(searchHBox, 0.0); 
		   AnchorPane.setLeftAnchor(searchHBox, 0.0); 
		   AnchorPane.setRightAnchor(searchHBox, 0.0); 
		   HBox.setMargin(searchHBox, new Insets(5,10,5,10));
		   return searchHBox;
		}
	
	public static void setSearchAnchorPane() {
	   AnchorPane userSearchAnchorPane = new AnchorPane();
	   userSearchAnchorPane.setMaxWidth(Double.MAX_VALUE);
	   AnchorPane.setTopAnchor(userSearchAnchorPane, 0.0); 
	   AnchorPane.setLeftAnchor(userSearchAnchorPane, 0.0); 
	   AnchorPane.setRightAnchor(userSearchAnchorPane, 0.0); 
	   AnchorPane.setBottomAnchor(userSearchAnchorPane, 0.0); 
	   
	   mainVB = new VBox();
	   AnchorPane.setTopAnchor(mainVB, 0.0); 
	   AnchorPane.setLeftAnchor(mainVB, 0.0); 
	   AnchorPane.setRightAnchor(mainVB, 0.0); 
	   AnchorPane.setBottomAnchor(mainVB, 0.0); 
	   mainVB.setPadding(new Insets(10,5,10,5));
	   lblErrorMessage.setMaxWidth(Double.MAX_VALUE);
	   lblErrorMessage.setStyle("-fx-padding: 10;"
	   		+ "-fx-background-color: #fca5a5;"
	   		+ "-fx-background-radius:7;"
	   		+ "-fx-text-fill: #dc2626;"
	   		+ "-fx-font-size:15px;");
	   mainVB.setSpacing(5.00);
	   lblErrorMessage.setVisible(false);
	   mainVB.getChildren().addAll(searchBar(),lblErrorMessage);
	   userSearchAnchorPane.getChildren().addAll(mainVB);
	   
	   searchUserUIAnchorPane = userSearchAnchorPane;
	}

	  
	  
	  public static void formatPersonDataUiLabels(Label ...controls) {
		    for (Label control : controls) {
	            if(control != null) {
	            	control.setMaxWidth(Double.MAX_VALUE);
	            	control.setPadding(new Insets(5,5,5,5));
	            	control.setAlignment(Pos.CENTER);
	            	control.setTextAlignment(TextAlignment.CENTER);
	            	control.setWrapText(true);
	            	control.setStyle("-fx-font-size: 18px;"
	            			+ "-fx-text-fill: #ffffff;"
	            			+ "-fx-background-color: #06b6d4;"
	            			+ "-fx-padding:10px;"
	            			+ "-fx-border-radius:5px;");
	            }
	        }
	  }
	  
	  public static void ShowpersonProfile() {
		  personVB = new VBox();
		  personVB.setMaxWidth(Double.MAX_VALUE);
		  personVB.setSpacing(10);
		  personVB.setAlignment(Pos.CENTER); 
		  
		  ImageView employeeImage = new ImageView();
		  employeeImage.setFitWidth(380);
		  employeeImage.setFitHeight(350);
		  employeeImage.setPreserveRatio(true);
		  Image javafxImage = SwingFXUtils.toFXImage(currentEmployee.getImage(), null);
		  employeeImage.setImage(javafxImage);
		  Label lblFullName = new Label(currentEmployee.getFullName());
		  Label lblEmployeeNumber = new Label(currentEmployee.getEmployeeID());
		  Label lblGender = new Label(currentEmployee.getGender());
		  CurrentEmployeeID = currentEmployee.getID();
		  
		  Button btnConfirm = new Button("CONFIRM");
		  btnConfirm.setStyle("-fx-background-color:  #3f6212;"
		  		+ "-fx-font-size:18px;"
		  		+ "-fx-padding:7px;"
		  		+ "-fx-text-fill: #ffffff;"
		  		+ "-fx-background-radius:5px;");
		  btnConfirm.setMaxWidth(Double.MAX_VALUE);
		  btnConfirm.setOnAction((event)->{
			  mainSene.setOnPersonConfirm(currentEmployee);
		  });
		  
		  Button btnCancel = new Button("CANCEL");
		  btnCancel.setStyle("-fx-background-color:#9f1239;"
			  		+ "-fx-font-size:18px;"
			  		+ "-fx-padding:7px;"
			  		+ "-fx-text-fill:  #ffffff;"
			  		+ "-fx-background-radius:5px;");
		  btnCancel.setMaxWidth(Double.MAX_VALUE);
		  
		  btnCancel.setOnAction((event)->{
			  handleEmployeeDataReset();
		  });
		  
		  HBox.setHgrow(btnCancel, Priority.ALWAYS);
		  HBox.setHgrow(btnConfirm, Priority.ALWAYS);
		  
		  VBox controlVBox = new VBox();
		  controlVBox.setMaxWidth(Double.MAX_VALUE);
		  controlVBox.getChildren().addAll(btnConfirm,btnCancel);
		  controlVBox.setPadding(new Insets(10,0,10,0));
		  controlVBox.setSpacing(3);
		  controlVBox.setAlignment(Pos.CENTER); 
		  
		  formatPersonDataUiLabels(lblFullName,lblEmployeeNumber,lblGender);
		  
		  personVB.getChildren().addAll(employeeImage,lblFullName,lblEmployeeNumber,lblGender,controlVBox);
		  
		  mainVB.getChildren().add(personVB);
	  }
	
}
