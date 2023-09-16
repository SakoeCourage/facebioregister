package application;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

public class Filemonitor {
	
public static void startMonitoryFaceHistoryFolder(Path folderPath, Consumer<Path> callback) {
    try {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        
        // Register all the event kinds you want to monitor
        folderPath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        Thread monitorThread = new Thread(() -> {
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE ||
                        kind == StandardWatchEventKinds.ENTRY_DELETE ||
                        kind == StandardWatchEventKinds.ENTRY_MODIFY) {  
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path filePath = folderPath.resolve(pathEvent.context());
                        if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".jpg")) {
                            callback.accept(filePath);
                        }
                    }
                }

                key.reset();
            }
        });

        monitorThread.setDaemon(true);
        monitorThread.start();

    } catch (IOException e) {
        e.printStackTrace();
    }
}



	}
