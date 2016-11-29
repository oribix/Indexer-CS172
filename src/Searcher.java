import java.io.IOException;
import java.nio.file.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.*;


public class Searcher {
    
    public static void search(String indexDir, String q) throws IOException, ParseException{
        
        //creates an index searcher
        Path indexPath = Paths.get(indexDir);
        Directory dir = FSDirectory.open(indexPath);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        
        //Parses the query
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);
        
        //gets top 10 documents
        TopDocs hits = is.search(query, 10);
        
        for(ScoreDoc scoreDoc : hits.scoreDocs){
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("url"));
        }
        reader.close();
    }
    
    public static void main(String[] args) {
        
        if(args.length < 2){
            System.out.println("invalid arguments.\n\n"
                    + "Arguments are of the form: \n"
                    + "[index directory] <query>\n\n"
                    + "Querys must be in quotes(\"\")");
            return;
        }
        
        System.out.println("Index Location: " + args[0]);
        System.out.println("Search Query: \"" + args[1] + "\"\n");
        
        try {
            search(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\nDid you input a valid path for argument 0?");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        
    }

}
