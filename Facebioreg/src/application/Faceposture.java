package application;

import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.ObservableList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;

public class Faceposture {
	
	AnchorPane parentContainer;
	private Boolean init;
	
	 private final List<FadeTransition> activeTransitions = new ArrayList<>();

	
	String[] imageList = {
			"appimages/fontaldirect.png",
			"appimages/fontalleft.png",
			"appimages/fontaldirect.png",
			"appimages/frontalright.png",
			"appimages/fontaldirect.png"
	};
	
	ObservableList<ImageView> PostureImageViews;
	
	SimpleIntegerProperty selectedIndex = new SimpleIntegerProperty(-1);
	
	
	public Faceposture() {
		  PostureImageViews = createImageViews(imageList);
		  applyFadeTransitions();
	}
	
	public void Init(){
		setInit(true);
		setSelectedIndex(0);
	};

	
	public void reset(){
		setSelectedIndex(-1);
		setInit(false);
	};
	
	public void applyFadeTransitions() {
        selectedIndex.addListener((obs, oldValue, newValue) -> {
            int index = newValue.intValue();

            // Stop any active transitions
            activeTransitions.forEach(FadeTransition::stop);
            activeTransitions.clear();

            for (int i = 0; i < PostureImageViews.size(); i++) {
                ImageView imageView = PostureImageViews.get(i);
                if (i == index) {
                    // i is equal to selectedIndex, play the fade transition
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), imageView);
                    fadeTransition.setFromValue(1.0); // Fully opaque
                    fadeTransition.setToValue(0.5);   // Mostly transparent
                    fadeTransition.setAutoReverse(true); // Reverse the transition
                    fadeTransition.setCycleCount(FadeTransition.INDEFINITE); // Repeat indefinitely
                    fadeTransition.play();

                    // Add the active transition to the list
                    activeTransitions.add(fadeTransition);
                } else {
                    imageView.setOpacity(i < index ? 1.0 : 0.3);
                }
            }
        });
    }
	
	
	public ObservableList<ImageView> createImageViews(String... imagePaths) {
		ObservableList<ImageView> imageViews = FXCollections.observableArrayList();
		for (String imagePath : imagePaths) {
			InputStream inputStream = getClass().getResourceAsStream(imagePath);
			Image image = new Image(inputStream);
			ImageView imageView = new ImageView(image);
			imageView.setPreserveRatio(true);
			imageView.setFitWidth(150);
			imageView.setOpacity(0.3);
			imageView.setFitHeight(150); 
			imageViews.add(imageView);
		}
		
		return imageViews;
	}
	
	

	public ObservableIntegerValue selectedIndexProperty() {
		return selectedIndex;
	}
	
	
	
	public ObservableList<ImageView> getPostureImages(){
		return this.PostureImageViews;
	}
	
	public int getSelectedIndex() {
		return selectedIndex.get();
	}
	
	public void setSelectedIndex(int index) {
		selectedIndex.set(index);
	}

	public Boolean getInit() {
		return this.init;
	}

	public void setInit(Boolean init) {
		this.init = init;
	}
	
}