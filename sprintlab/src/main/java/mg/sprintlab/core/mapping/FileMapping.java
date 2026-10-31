package mg.sprintlab.core.mapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileMapping {
    private static final String DEFAULT_UPLOAD_DIRECTORY = "/tmp/uploads";
    private static String uploadDirectory = DEFAULT_UPLOAD_DIRECTORY;

    private String filename;
    private String mimeType;
    private String targetDirectory = "";
    private byte[] data;

    public static void configureUploadDirectory(String directory) {
        if (directory != null && !directory.isBlank()) {
            uploadDirectory = directory.trim();
        }
    }


    private void generateName() {
        if (filename == null || filename.isEmpty()) {
            filename = "filename_placeholder";
        }

        String extension = "";
        int lastIndex = filename.lastIndexOf(".");

        if (lastIndex != -1) {
            extension = filename.substring(lastIndex); 
            filename = filename.substring(0, lastIndex);
        }

        String ender = UUID.randomUUID().toString();
        this.filename = filename + "_" + ender + extension;
    }

    public void persistFile() {
        if( data == null){
            throw new RuntimeException("No data in the file");
        }
        
        generateName();

        try {
            Path destination = Path.of(uploadDirectory, targetDirectory, filename);

            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }

            System.out.println(filename);

            Files.write(destination, data);

            System.out.println("Does this work bro");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
