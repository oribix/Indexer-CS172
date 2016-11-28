import java.net.MalformedURLException;
import java.net.URL;

public class DocUrlPair implements Comparable<DocUrlPair> {
    public String docName;
    public URL url;
    
    public DocUrlPair(String docName, URL url){
        this.docName = docName;
        this.url = url;
    }
    
    public DocUrlPair(String docName, String url) throws MalformedURLException{
        this.docName = docName;
        this.url = new URL(url);
    }
    
    public String toString(){
        return docName + " " + url;
    }
    
    public int compareTo(DocUrlPair other){
        return this.docName.compareTo(other.docName);
    }
}
