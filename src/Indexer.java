import java.io.*;
import java.nio.file.*;

//Lucene libraries
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.*;

//Write a program that uses the Lucene libraries to index all the html files in the folder you created in Part A
//Handle different fields like title, body, creation date (if available).
public class Indexer {
    
    public static final Version version = Version.LUCENE_6_3_0;
    
    //gets indexWriter
    //returns indexWriter on success or null on failure
    IndexWriter getIndexWriter(String dir){
        Path indexPath = null;
        try{
            indexPath = Paths.get(dir);
            Directory indexDir = FSDirectory.open(indexPath);
            IndexWriterConfig luceneConfig = new IndexWriterConfig();
            return new IndexWriter(indexDir, luceneConfig);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    ////gets document
    ////returns Document on success, null on failure
    //protected Document getDocument(File f){
    //    Document doc = null;
    //    try {
    //        FileReader fileReader = new FileReader(f);
    //        doc.add(new TextField("contents", fileReader));
    //        doc.add(new StringField("filename", f.getName()));
    //        doc.add(new StringField("fullpath", f.getCanonicalPath()));
    //    } catch (FileNotFoundException e) {
    //        e.printStackTrace();
    //    }
    //    
    //    return doc;
    //}
    
    public static void main(String[] args) {
        //prints error message if arguments are wrong then exits
        if(args.length != 1){
            System.out.println("Incorrect arguments passed. Arguments are of the form: \n[document storage path]");
            return;
        }
        
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
