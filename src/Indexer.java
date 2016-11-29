import java.io.*;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Collections;

//Lucene libraries
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;

//jsoup
import org.jsoup.Jsoup;

//Write a program that uses the Lucene libraries to index all the html files in the folder you created in Part A
//Handle different fields like title, body, creation date (if available).
public class Indexer {
    
    private static IndexWriter writer;
    private static List<DocUrlPair> docUrlPairs;
    
    //gets indexWriter
    //returns indexWriter on success or null on failure
    private static IndexWriter getIndexWriter(String dir){
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
    
    //returns the URL of the document
    private static String getDocUrl(File f){
        String result = "";
        DocUrlPair key = new DocUrlPair();
        key.docName = f.getName();
        int index = Collections.binarySearch(docUrlPairs, key);
        try{
            DocUrlPair docUrlPair = docUrlPairs.get(index);
            System.out.println(docUrlPair.toString()); //prints pairing so we keep track of progress
            result = docUrlPair.url.toString(); 
        } catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    //returns title of page
    private static String getDocTitle(File f){
        String title = "";
        try {
            title = Jsoup.parse(f, "UTF-8", "").title();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return title;
    }
    
    //gets document Throws IOException if something goes wrong
    protected static Document getDocument(File f) throws IOException{
        Document doc = new Document();
        FileReader fileReader = new FileReader(f);
        
        //add fields to the document
        doc.add(new StringField("url", getDocUrl(f), Field.Store.YES));
        
        //add title field with an extra boost
        TextField title = new TextField("title", getDocTitle(f), Field.Store.YES);
        title.setBoost(5);
        doc.add(title);
        
        //boost score for contents
        TextField contents = new TextField("contents", fileReader);
        contents.setBoost(3);
        doc.add(contents);
        
        //filename and file path fields
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));
        doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));
        return doc;
    }
    
    //indexes a file
    private static void indexFile(File f) throws IOException {
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
    
    //indexes a folder of .html files
    public static int index(String dataDir, FileFilter filter) throws IOException{
        //gets list of all .html files in folder
        Path docPath = Paths.get(dataDir);
        File docFolder = docPath.toFile();
        File[] files = docFolder.listFiles(filter);
        
        //indexes each file
        System.out.println("indexing...");
        for(File f : files){
            indexFile(f);
        }
        
        //return number of documents indexed
        return writer.numDocs();
    }
    
    //closes the IndexWriter
    public static void close() throws IOException{
        writer.close();
    }
    
    public static void main(String[] args) {
        //prints error message if arguments are wrong then exits
        if(args.length != 2){
            System.out.println("Incorrect arguments passed. Arguments are of the form:\n"
                    + "[index path] [document storage path]");
            return;
        }
        
        //gets the argument variables
        String indexDir = args[0];
        String docStoreDir = args[1];
        
        //holds the doc-url mapping for all docs
        docUrlPairs = new LinkedList<DocUrlPair>();
        
        //getting _url_doc_map.txt
        Path docStorePath = Paths.get(docStoreDir);
        File[] docStoreFiles = docStorePath.toFile().listFiles();
        File urlDocPairFile = docStoreFiles[docStoreFiles.length - 1];
        
        //parsing _url_doc_map.txt for the mappings
        try {
            BufferedReader br = new BufferedReader(new FileReader(urlDocPairFile));
            String s = "";
            while((s = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(s);
                LinkedList<String> tokens = new LinkedList<String>();
                while(st.hasMoreTokens()){
                    tokens.add(st.nextToken());
                }
                
                String docName = tokens.pollLast();
                String URL = "";
                for(String token : tokens){
                    if(token == tokens.getFirst()) URL += token;
                    else URL += " " + token;
                }
                DocUrlPair docUrlPair = new DocUrlPair(docName, URL);
                docUrlPairs.add(docUrlPair);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with _url_doc_map.txt");
            return;
        }
        
        docUrlPairs.sort(null);
        
        //stops the program if getIndexWriter fails
        if((writer = getIndexWriter(indexDir)) == null){
            System.out.println("getIndexWriter failed!");
            return;
        }
        
        FileFilter filter = new DocFilter();
        try {
            int numIndexed = index(docStoreDir, filter);
            System.out.println(numIndexed + " files indexed");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
