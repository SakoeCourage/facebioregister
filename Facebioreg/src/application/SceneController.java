package application;




import javafx.scene.SnapshotParameters;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import application.Uitility;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

import org.bytedeco.javacv.FrameGrabber.Exception;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javafx.scene.control.ProgressIndicator;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.fxml.Initializable;

import java.awt.Desktop;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import application.FaceDetector;
import application.Uitility;
import application.Filemonitor;
import application.Apiclient;
import application.UIHandler;
import application.Faceposture;

import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SceneController implements Initializable  {
	
	//**********************************************************************************************
	public boolean isInit = false;
	
	
	//**********************************************************************************************
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane mainMidAnchorPane;
	@FXML
	private TitledPane faceRegSectionTitledPane;
	@FXML
	private VBox postureSectionVbox;
	@FXML
	private Button startCam;
	@FXML
	private Button saveBtn;
	@FXML
	private Button capBtn;
	@FXML
	private Button recapBtn;
	@FXML
	private Button recogniseBtn;
	@FXML
	private Button sources;
	@FXML
	private Button stopRecBtn;
	@FXML
	private ImageView frame;
	@FXML
	private ImageView motionView;
	@FXML
	private AnchorPane pdPane;
	@FXML
	private TitledPane dataPane;
	@FXML
	private TextField textFirstName;
	@FXML
	private TextField textLastName;
	@FXML
	private TextField textEmployeeID;
	@FXML
	private TextField reg;
	@FXML
	private TextField textGender;
	@FXML
	private TextField textAge;
	@FXML
	private TextField textFullName;
	@FXML
	public ListView<String> logList;
	@FXML
	public ListView<String> output;
	@FXML
	public ProgressIndicator pb;
	@FXML
	public Label savedLabel;
	@FXML
	public Label imageCount;
	@FXML
	public Label personLabel;
	@FXML
	public Label title;
	@FXML
	public TilePane tile;
	@FXML
	public StackPane imageHolders;
	@FXML
	public ImageView tempimage;
	@FXML
	private Pane errorPanel;
	@FXML
	private AnchorPane userCaptureAnchoPane;
	@FXML
	private AnchorPane profileSectionAnchorPane;
	//**********************************************************************************************
	
	
	public int maxImageCaptures = 5;
	
	static FaceDetector faceDetect = new FaceDetector();	
	
	Faceposture faceposture = new Faceposture();
	
	ImageView imageView1;
	
	
	private List<String> event = new CopyOnWriteArrayList<>();
	public static ObservableList<String> outEvent = FXCollections.observableArrayList();
	
	public boolean enabled = false;
	public boolean isDBready = false;
	
	Apiclient my = new Apiclient();
	
	UIHandler uiHandler = new UIHandler(this);
	
	

	Thread moveTempToFacesThread;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		CompletableFuture<Void> allTasks = CompletableFuture
				.runAsync(this::initializeFileMonitoring)
				.thenRun(this::reinitializeuicomponents)
				.thenRun(Uitility::checkImagesFolder);
		allTasks.thenRun(()->{
			Platform.runLater(()->{
				initializeImages();
			});
		});
	} 
	
	
	
	public void reinitializeuicomponents() {
		Uitility.setControlsDisabled(true,recogniseBtn,capBtn,sources);
		root.getChildren().remove(errorPanel);
		recapBtn.setVisible(false);
		InputStream inputStream = getClass().getResourceAsStream("appimages/notrecording.jpg");
		Image image = new Image(inputStream);
		this.frame.setImage(image);
		
		this.setWrappedImageView();
		initializeSearchUserMode();
//		*****************
	
	}
	
	public  void setWrappedImageView() {
		HBox imageViewContainer = new HBox();
		for (ImageView view : faceposture.getPostureImages()){
			imageViewContainer.getChildren().add(view);
			HBox.setHgrow(view, Priority.ALWAYS);
		}
		
		imageViewContainer.setMaxWidth(Double.MAX_VALUE);
		imageViewContainer.setSpacing(10);
		postureSectionVbox.setMaxWidth(faceRegSectionTitledPane.getMaxWidth());
		postureSectionVbox.setSpacing(5);
	
		postureSectionVbox.getChildren().add(imageViewContainer);
	}
	
	
	
	
	
	public void initializeSearchUserMode() {
		profileSectionAnchorPane.getChildren().remove(this.userCaptureAnchoPane);
		faceposture.reset();
		Platform.runLater(()->{
			UIHandler.handleEmployeeDataReset();
			UIHandler.txtEmployeeID.setText(null);
			if(this.isInit) {
				this.capBtn.setDisable(true);
			}
			
			profileSectionAnchorPane.getChildren().add(UIHandler.searchUserUIAnchorPane);	
		});		
		
	}
	
	public void setOnPersonConfirm(Person person) {
		faceposture.Init();
		profileSectionAnchorPane.getChildren().remove(UIHandler.searchUserUIAnchorPane);			
		Platform.runLater(()->{
			profileSectionAnchorPane.getChildren().add(this.userCaptureAnchoPane);
			if(this.isInit) {
				this.capBtn.setDisable(false);
			}
			this.setFormData(person);
			
		});	
	}
	
	public void initializeImages() {
		new Thread(()->{
			Platform.runLater(()->{
				this.removeTempImages();
				this.loadFaceHistory();
				Uitility.setControlsDisabled(true,recogniseBtn,capBtn,sources);
			});
		}).start();
	}
	
	public void initializeFileMonitoring() {
		Filemonitor.startMonitoryFaceHistoryFolder(Paths.get(Configuration.getPreference(ConfigKeys.facesDirectory, null)), filePath -> {
			Platform.runLater(() -> {
				ImageView imageView = createImageView(filePath.toFile());
				tile.getChildren().add(0, imageView);
			});
		});
		
		Filemonitor.startMonitoryFaceHistoryFolder(Paths.get(Configuration.getPreference(ConfigKeys.tempFaces, null)), filePath -> {
			Platform.runLater(() -> {
				this.loadtempFaces();
			});
		});
	}
	
	
	private final ScheduledExecutorService notificationClearExecutorService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> notificationClear;
	
	
	
	public void hideNotificationPanel() {
		boolean exist = root.getChildren().contains(errorPanel);
		if(exist) {
			FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(1), errorPanel);
			fadeTransition1.setFromValue(1);
			fadeTransition1.setToValue(0);
			fadeTransition1.setInterpolator(Interpolator.EASE_OUT);
			ParallelTransition parallelTransition = new ParallelTransition();
			parallelTransition.getChildren().addAll(fadeTransition1);
			parallelTransition.play();
			parallelTransition.setOnFinished(event->{
				root.getChildren().remove(errorPanel);
			});
		}
	}
	
	public void showNotificationPanel() {
		boolean exist = root.getChildren().contains(errorPanel);
		if (notificationClear != null) {
			notificationClear.cancel(true); 
		}
		if(!exist) {
			root.getChildren().add(errorPanel);
			errorPanel.toFront();
			FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), errorPanel);
			fadeTransition1.setFromValue(0);
			fadeTransition1.setToValue(1);
			fadeTransition1.setInterpolator(Interpolator.EASE_OUT);
			ParallelTransition parallelTransition = new ParallelTransition();
			parallelTransition.getChildren().addAll(fadeTransition1);
			parallelTransition.play();
			
		}
		notificationClear = notificationClearExecutorService.schedule(() -> {
			hideNotificationPanel();
		}, 3, TimeUnit.SECONDS);
		
	}
	
	
	
	EventHandler<ActionEvent> closeButtonEventHanlder = event -> {
		this.hideNotificationPanel();
	};
	
	public void notify(String data, boolean error) {
		Instant now = Instant.now();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(now, java.time.ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = localDateTime.format(formatter);
		
		String logs = formattedDateTime + ":\n" + data;
		event.add(logs);
		
		new Thread(() -> {
			for (String message : event) {
				Platform.runLater(() -> {
					Uitility.showMessages(errorPanel, message, error,closeButtonEventHanlder);
					showNotificationPanel();
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			event.clear();
		}).start();
		
	}	
	
	
    private void openDirectory(File directory) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(directory);
            }
        } catch (IOException e) {
            
        }
    }
	
	private ImageView createImageView(final File imageFile) {
        try {
            final Image img = new Image(new FileInputStream(imageFile), 120, 0, true, true);
            final ImageView imageView1 = new ImageView(img);
            imageView1.setPreserveRatio(true);
            imageView1.setSmooth(true);
            imageView1.setCache(true);

            imageView1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                	openDirectory(imageFile.getParentFile());
                }
            });

            return imageView1;
        } catch (FileNotFoundException e) {
            this.notify(e.getMessage(), true);
        }

        return null;
    }

	
	
	@FXML 
	protected void toggleCamara() {
		if(this.isInit == true) {
			this.stopCam();	
		}else {
			new Thread(()->{

			this.startCamera();				
			}).start();
		}
	}
	
	
	protected void startCamera() {
		Platform.runLater(()->{
			Uitility.setControlsDisabled(true,this.startCam);
			startCam.setText("STARTING...");			
		});
		try {
			faceDetect.init(frame);
		} catch (Exception e) {
			Platform.runLater(()->{
				this.startCam.setDisable(false);
				startCam.setText("START");		
			});
			notify(e.getMessage(),true);
		}
		
		faceDetect.start();
		faceDetect.setLabelPerson(personLabel);
		faceDetect.setMaxImages(maxImageCaptures);
		Platform.runLater(()->{
			tile.setPadding(new Insets(15, 15, 55, 15));
			tile.setHgap(30);
			this.isInit = true;
			Uitility.setControlsDisabled(false,capBtn,recogniseBtn,sources);
			startCam.setText("STOP");
			startCam.setStyle("-fx-background-color: #dc2626;" + "	-fx-text-fill: #ffffff;" + "-fx-font-weight: bold;");
			notify("Video Stream Started",false);
		});	

	}
	
	protected void loadFaceHistory() {
		String path = Configuration.getPreference(ConfigKeys.facesDirectory, null);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles != null && listOfFiles.length > 0) {
			// Sort the files by last modified time stamp in descending order (most recent first)
			Arrays.sort(listOfFiles, (file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));
			int fileCount = 0;
			for (final File file : listOfFiles) {
				if (fileCount >= 20) {
					break;
				}
				imageView1 = createImageView(file);
				tile.getChildren().add( imageView1);
				fileCount++;
			}
		}
	}
	
	
	
	protected void loadtempFaces() {
		String path = Configuration.getPreference(ConfigKeys.tempFaces, null);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		if(listOfFiles.length == 0) {
			new Thread(()->{
				Platform.runLater(()->{
					imageCount.setVisible(false);
					recapBtn.setVisible(false);
				});
			}).start();
		} else {
			new Thread(()->{
				Platform.runLater(()->{
					imageCount.setVisible(true);
					imageCount.setText(String.valueOf(listOfFiles.length));
					recapBtn.setVisible(true);
				});
			}).start();
			
		}
		
		if(faceposture.getInit() == true) {
			faceposture.setSelectedIndex(listOfFiles.length);
		}
		
		if(listOfFiles.length == maxImageCaptures) {
			capBtn.setDisable(true);
		}else {
			capBtn.setDisable(false);
		}
		
		
		
		
		
		if (listOfFiles != null && listOfFiles.length > 0) {
			imageHolders.getChildren().clear();
			
			int fileCount = 0;
			for (final File file : listOfFiles) {
				if (fileCount == maxImageCaptures) {
					break;
				}
				if(file != null) {
					ImageView imageView = createImageView(file);
					imageView.setFitWidth(172);
					imageView.setFitHeight(278);
					imageView.setPreserveRatio(true);
					imageView.setTranslateX(fileCount * 10); 
					imageView.setTranslateY(fileCount * 10);
					Rectangle clip = new Rectangle();
					clip.setWidth(imageView.getFitWidth() + 3);
					clip.setHeight(imageView.getFitHeight() + 3);
					clip.setArcHeight(50);
					clip.setArcWidth(50);
					clip.setStroke(Color.WHEAT);
					SnapshotParameters parameter = new SnapshotParameters();
					parameter.setFill(Color.TRANSPARENT);
					WritableImage image = imageView.snapshot(parameter, null);
					imageView.setClip(null);
					imageView.setEffect(new DropShadow(5,Color.WHITE));
					imageView.setImage(image);
					
					
					imageHolders.getChildren().add(imageView);
					fileCount++;
				}
				
			}
		}
	}
	
	protected void stopCam()  {
		faceDetect.stop();
		notify("Cam Stream Stopped!",false);
		
		notify("Database Connection Closed",false);
		isDBready=false;
		this.isInit = false;
		Platform.runLater(()->{
			frame.setImage(null);	
			personLabel.setVisible(false);
			
		});
		faceDetect.setIsRecFace(false);
		Uitility.setControlsDisabled(true,capBtn,recogniseBtn,sources);
		startCam.setText("START");
		startCam.setStyle("-fx-background-color: #365314;" + "	-fx-text-fill: #ffffff;" + "-fx-font-weight: bold;");
	}
	
	
	public  void handleonClose() throws SQLException {
		if(isInit == true) {
			this.stopCam();	
		}
	}
	
	
	public static void handleWindowClose() {
		faceDetect.setIsRecFace(false);
		faceDetect.stop();
		Platform.exit();
		System.exit(0);
	}
	
	
	@FXML
	public void handleTempImageReset() {
		new Thread(() -> {
			Platform.runLater(() -> {
				removeTempImages();
				InputStream inputStream = getClass().getResourceAsStream("appimages/imageavatar.png");
				Image image = new Image(inputStream);
				ImageView imageAvatar = new ImageView();
				imageAvatar.setImage(image);
				imageAvatar.setFitWidth(172);
				imageAvatar.setFitHeight(278);
				imageAvatar.setPreserveRatio(true);
				imageAvatar.setSmooth(true);
				imageAvatar.setCache(true);
				imageHolders.getChildren().clear();
				imageCount.setVisible(false);
				this.capBtn.setDisable(false);
				this.recapBtn.setVisible(false);
				imageHolders.getChildren().add(imageAvatar);    
			});
		}).start();
	}
	
	@FXML
	protected void saveFace(){	
		new Thread(() -> {
			faceDetect.setSaveFace(true);
		}).start();
	}
	
	public boolean capturedTempImages() {
		String folderPath = Configuration.getPreference(ConfigKeys.tempFaces, null);
		File folder = new File(folderPath);
		if (folder.exists() && folder.isDirectory()) {
			// List all files in the folder
			File[] files = folder.listFiles();
			int jpgFileCount = 0;
			for (File file : files) {
				if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
					jpgFileCount++;
				}
			}
			if (jpgFileCount == maxImageCaptures) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public void removeTempImages() {
		File folder = new File(Configuration.getPreference(ConfigKeys.tempFaces, null));
		if (folder.exists() && folder.isDirectory()) {
			// List all files in the folder
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
					if (file.delete()) {
						notify("Previous face data removed",false);
					} else {
						notify("Failed remove face data",true);
					}
				}
			}
		}
	}
	
	
	
	
	protected void deleteIfExist(File[] files) {
		for (File file : files) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
				String sourceFilePath = file.getAbsolutePath();
				String destinationDirectory = Configuration.getPreference(ConfigKeys.facesDirectory, null);
				
				File sourceFile = new File(sourceFilePath);
				File destinationDirectoryFile = new File(destinationDirectory);
				
				if (!destinationDirectoryFile.exists()) {
					destinationDirectoryFile.mkdirs();
				}
				
				Path sourcePath = sourceFile.toPath();
				Path destinationPath = new File(destinationDirectory, file.getName()).toPath();
				
//	            System.out.println("Exist: "+ Files.exists(destinationPath) +" "+ sourcePath+ " :");
				if (Files.exists(destinationPath)) {
					try {
						Files.delete(destinationPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}
	}
	
	public static void deleteFilesIfExistsInFacesFolder(String[] fileNames) {
		File folder = new File(Configuration.getPreference(ConfigKeys.facesDirectory, null));
		
		// Check if the folder exists
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			
			if (files != null) {
				for (String fileName : fileNames) {
					for (File file : files) {
						// Check if the file has the same name as the target file name
						if (file.isFile() && file.getName().equals(fileName)) {
							// File found, delete it
							if (file.delete()) {
								System.out.println("Deleted: " + file.getAbsolutePath());
							} else {
								System.err.println("Failed to delete: " + file.getAbsolutePath());
							}
						}
					}
				}
			}
		}
	}
	
	public boolean checkIfAnyExist(String[] fileNames) {
		File folder = new File(Configuration.getPreference(ConfigKeys.facesDirectory, null));
		
		// Check if the folder exists
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			
			if (files != null) {
				for (String fileName : fileNames) {
					for (File file : files) {
						// Check if the file has the same name as the target file name
						if (file.isFile() && file.getName().equals(fileName)) {
							return true; // Found a matching file, return true
						}
					}
				}
			}
		}
		
		return false; // No matching file found, return false
	}
	
	
	protected void handleImageMove() throws IOException {
		File sourceFolder = new File(Configuration.getPreference(ConfigKeys.tempFaces, null));
		File[] sourceFiles = sourceFolder.listFiles();
		
		String destinationDirectory = Configuration.getPreference(ConfigKeys.facesDirectory, null);
		int count = 1;
		for (File file : sourceFiles) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
				String sourceFilePath = file.getAbsolutePath();
				String newFileName = textEmployeeID.getText() + "-" + textFullName.getText().replace(" ", "_")+String.valueOf(count) + ".jpg";
				
				newFileName = newFileName.replace("N\\A", "_");
				count ++;
				File sourceFile = new File(sourceFilePath);
				File destinationDirectoryFile = new File(destinationDirectory);
				if (!destinationDirectoryFile.exists()) {
					destinationDirectoryFile.mkdirs();
				}
				Path sourcePath = sourceFile.toPath();
				Path destinationPath = new File(destinationDirectory, newFileName).toPath();
				Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
	
	public void moveJpgImageFiles() throws IOException {
		File sourceDir = new File(Configuration.getPreference(ConfigKeys.tempFaces, null));
		File destDir = new File(Configuration.getPreference(ConfigKeys.facesDirectory, null));
		
		// Check if the source directory exists and is a directory
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			return;
		}
		
		// Check if the destination directory exists; create it if not
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		
		// List all files in the source directory
		File[] files = sourceDir.listFiles();
		int count = 1;
		
		if (files != null) {
			for (File file : files) {
				String newName = UIHandler.CurrentEmployeeID + "-" + textFullName.getText().replace(" ", "_") + count + ".jpg";
				newName = newName.replace("N/A", "");
				// Check if the file is a JPG image
				if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
					Path sourcePath = file.toPath();
					Path destinationPath = new File(destDir, newName).toPath();
					Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
					count++;
				}
			}
		}
	}
	
	
	
	protected void moveTempToFaces() throws IOException {
		File folder = new File(Configuration.getPreference(ConfigKeys.tempFaces, null));
		if (folder.exists() && folder.isDirectory()) {
			try {
				moveJpgImageFiles();
				
				OnNewFaceAdded();
			} catch (IOException e) {
//				e.printStackTrace();
				notify("Failed: Unable to overwrite previous record", true);
			}
		}
	}
	
	
	public  String[] getFileNamesInFolder(String folderPath) {
		File folder = new File(folderPath);
		
		// Check if the folder exists
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			
			if (files != null) {
				String[] fileNames = new String[files.length];
				
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						fileNames[i] = files[i].getName();
					}
				}
				
				return fileNames;
			}
		}
		
		// Return an empty array if the folder doesn't exist or is empty
		return new String[0];
	}
	
	
	public  void renameFiles() throws IOException {
		File folder = new File(Configuration.getPreference(ConfigKeys.tempFaces, null));
		File[] files =folder.listFiles();
		int count = 1;
		for (File file : files) {
			if (file.isFile()) {
				String newFileName = textEmployeeID.getText() + "-" + textFullName.getText().replace(" ", "_")+count;
				String parentPath = file.getParent();
				String extension = getFileExtension(file.getName());
				// Generate the new file name
				String newFileNameWithExtension = newFileName + extension;
				// Create the new File object with the updated name
				File newFile = new File(parentPath, newFileNameWithExtension);
				if (file.renameTo(newFile)) {
					count++;
				} else {
					// Handle renaming failure (you can log an error message or take other actions)
					System.err.println("Failed to rename file: " + file.getAbsolutePath());
				}
			}
		}
		
	}
	
	private static String getFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex != -1) {
			return fileName.substring(lastDotIndex);
		}
		return ""; // No file extension
	}
	
	@FXML
	protected void saveRecord() {
		if(!this.capturedTempImages()) {
			this.notify("Minimum "+ maxImageCaptures + " faces required ",true);
			return;
		}else if(Uitility.areAnyFieldsEmpty(this.textEmployeeID,this.textFullName)) {
			this.notify("All fields are required",true);
		}else {
			
			try {
				moveTempToFaces();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}	
	}
	
	private void OnNewFaceAdded() {
		// TODO Auto-generated method stub
		this.resetFormData();	
		
	}
	
	
	
	@FXML
	protected void resetFormData() {
		Platform.runLater(()->{
			Uitility.setTextControlsToEmpty(textEmployeeID,textFullName,textGender);
			handleTempImageReset();
			initializeSearchUserMode();
			notify("New face added", false);
		});
	}
	
	
	
	
	protected void setFormData(Person personData) {
		new Thread(()->{
			Platform.runLater(()->{
				textFullName.setText(personData.getFullName());
				textGender.setText(personData.getGender());
				textEmployeeID.setText(personData.getEmployeeID());
			});
		}).start();
	}
	
	
	@FXML
	protected void faceRecognise() {
		String trainingDir = Configuration.getPreference(ConfigKeys.facesDirectory, null);
		File root = new File(trainingDir);
		FilenameFilter imgFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
			}
		};
		
		File[] imageFiles = root.listFiles(imgFilter);
		if(imageFiles.length == 0 ) {
			this.notify("No Trained faces!", true);
		}else {
			if(!faceDetect.getIsRecFace()) {
				faceDetect.setIsRecFace(true);
				notify("Face Recognition Activated !",false);
			}else {
				faceDetect.setIsRecFace(false);
				personLabel.setVisible(false);
				notify("Face Recognition De-Activated !",false);
			}
		}
	}
	
	
	@FXML
	protected void showsourceSideBar() {
		uiHandler.sourceSideBar(root);
	}
	
	@FXML
	protected void showapiSideBar() {
		uiHandler.apiSideBar(root);
	}
	
	
	
	
}
