import java.io.File;
import java.io.FileFilter;

public class DocFilter implements FileFilter{
    private final String validFileExtension = "html";
    
    public boolean accept(File file){
        return file.getName().toLowerCase().endsWith(validFileExtension);
    }
}
