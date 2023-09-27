package application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;



public class Configuration {
	   private static Properties properties;

	    static {
	        properties = new Properties();
	        loadPreferences();
	    }
	    
	    public static String getPreference(String key, String defaultValue) {
	        return properties.getProperty(key, defaultValue);
	    }

	    public static void setPreference(String key, String value) {
	        properties.setProperty(key, value);
	        savePreferences();
	    }


	    public static String getDefaultConfigDir() {
	        String userHome = System.getProperty("user.home");
	        String documentsPath = userHome + File.separator + "Documents";
	        String configDirPath = documentsPath + File.separator + ConfigKeys.fileDefaultFolder;
	        String filePath = configDirPath + File.separator + "config.properties";
	        return filePath;
	    }

	    static void loadPreferences() {
	        File configFile = new File(getDefaultConfigDir());

	        try (FileInputStream input = new FileInputStream(configFile)) {
	            properties.load(input);
	        } catch (IOException e) {   
	            // Check if the parent directory of the file exists, and create it if not
	            File parentDir = configFile.getParentFile();
	            if (!parentDir.exists()) {
	                if (parentDir.mkdirs()) {
	                    System.out.println("Created directory: " + parentDir.getAbsolutePath());
	                } else {
	                    System.err.println("Failed to create directory: " + parentDir.getAbsolutePath());
	                }
	            }

	            // Create a new properties file with default values
	            try (FileOutputStream output = new FileOutputStream(configFile)) {
	                properties.store(output, null);
	            } catch (IOException ioException) {
	                ioException.printStackTrace();
	            }
	        }
	    }

	    private static void savePreferences() {
	        try (FileOutputStream output = new FileOutputStream(getDefaultConfigDir())) {
	            properties.store(output, null);
	        } catch (IOException e) {
	            // Handle the exception
	            e.printStackTrace();
	        }
	    }






}
