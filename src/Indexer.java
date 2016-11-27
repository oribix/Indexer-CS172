import java.io.File;
import java.io.FileFilter;
import java.nio.file.*;

//Write a program that uses the Lucene libraries to index all the html files in the folder you created in Part A
//Handle different fields like title, body, creation date (if available).
public class Indexer {
    
    public static void main(String[] args) {
        
        
        //prints error message if arguments are wrong then exits
        if(args.length != 1){
            System.out.println("Incorrect arguments passed. Arguments are of the form: \n[document storage path]");
            return;
        }
        
        ////print out the arguments
        //for(String arg : args){
        //    System.out.println(arg);
        //}
        
        
        //get document location
        File[] docs = null;
        try {
            Path docPath = Paths.get(args[0]);
            File docFolder = docPath.toFile();
            
            FileFilter docFilter = new DocFilter();
            docs = docFolder.listFiles(docFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for(File f: docs){
            System.out.println(f);
        }
        
    }
}
