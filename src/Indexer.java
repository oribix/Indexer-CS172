import java.io.*;
import java.nio.file.*;

//Lucene libraries
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;

//Write a program that uses the Lucene libraries to index all the html files in the folder you created in Part A
//Handle different fields like title, body, creation date (if available).
public class Indexer {
    
    private IndexWriter writer;
    
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
    
    //gets document Throws IOException if something goes wrong
    protected Document getDocument(File f) throws IOException{
        Document doc = new Document();
        FileReader fileReader = new FileReader(f);
        
        //add fields to the document
        doc.add(new TextField("contents", fileReader));
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));         //IDK if it should be yes or no
        doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));//IDK if it should be yes or no
        return doc;
    }
    
    private void indexFile(File f) throws IOException {
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
    
    public int index(String dataDir, FileFilter filter) throws IOException{
        Path docPath = Paths.get(dataDir);
        File docFolder = docPath.toFile();
        File[] files = docFolder.listFiles(filter);
        
        for(File f : files){
            System.out.println(f);
            indexFile(f);
        }
        
        return writer.numDocs();
    }
    
    public void close() throws IOException{
        writer.close();
    }
    
    public static void main(String[] args) {
        //prints error message if arguments are wrong then exits
        if(args.length != 2){
            System.out.println("Incorrect arguments passed. Arguments are of the form:\n"
                    + "[index path] [document storage path]");
            return;
        }
        
        Indexer indexer = new Indexer();
        indexer.writer = indexer.getIndexWriter(args[0]);
        
        //stops the program if getIndexWriter fails
        if(indexer.writer == null){
            System.out.println("getIndexWriter failed!");
            return;
        }
        
        FileFilter filter = new DocFilter();
        try {
            indexer.index(args[1], filter);
            indexer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
    }
}
