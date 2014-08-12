
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Parse 
{
	public enum Type {BNODE, LITERAL, URI};
	public static class Triple {
		public Node sub;
		public Node pred;
		public Node obj;
		public Triple(String triple) {
			//at which points of this string do the predicate and object start?
			int startOfPred = triple.indexOf(' ')+1;
			int startOfObj = triple.indexOf(' ', startOfPred)+1;
			
			//take indexes above to return the nodes in this triple
			sub = new Node(triple.substring(0, startOfPred-1));
			pred = new Node(triple.substring(startOfPred, startOfObj-1));
			obj = new Node(triple.substring(startOfObj, triple.length()-2));//remove final ' .' as well
		}
	}
	public static class Node {
		private final String stringRepresentation;
		public Node(String node) {
			this.stringRepresentation = node;
		}
		public Type type() {
			if (stringRepresentation.charAt(0) == '"') return Type.LITERAL;
			if (stringRepresentation.startsWith("<http://lodlaundromat.org/.well-known")) return Type.BNODE;
			return Type.URI;
		}
		public String value() {
			if (stringRepresentation.charAt(0) == '<') return stringRepresentation.substring(1, stringRepresentation.length()-1);
//			
			//it is a literal
			return stringRepresentation.substring(1, stringRepresentation.lastIndexOf("\""));
		}
		
		public String dataType() {
			if (stringRepresentation.charAt(0) == '"') {
				int typeIndex = stringRepresentation.lastIndexOf("^", stringRepresentation.length() - stringRepresentation.lastIndexOf("\""));
				if (typeIndex > 0) return stringRepresentation.substring(typeIndex + 2, stringRepresentation.length()-1);//this removes the < and > from datatype as well
			}
			return null;
		}
		public String lang() {
			if (stringRepresentation.charAt(0) == '"') {
				int langIndex = stringRepresentation.lastIndexOf("@", stringRepresentation.length() - stringRepresentation.lastIndexOf("\""));
				if (langIndex > 0) return stringRepresentation.substring(langIndex + 1);
			}
			return null;
		}
		public String toString() {
			return stringRepresentation;
		}
	}
	
	public static void readStream(InputStream inputStream) throws IOException {
    	BufferedReader buffered = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream), "UTF-8"));
    	String tripleString;
    	while ((tripleString = buffered.readLine()) != null) {
    		Triple triple = new Triple(tripleString);
    		
    		//Do something!
    		System.out.println("==================");
    		System.out.println("subject: ");
    		System.out.println("\ttoString: " + triple.sub.toString());
    		System.out.println("\tvalue: " + triple.sub.value());
    		System.out.println("\ttype: " + triple.sub.type());
    		System.out.println("predicate:");
    		System.out.println("\ttoString: " + triple.pred.toString());
    		System.out.println("\tvalue: " + triple.pred.value());
    		System.out.println("\ttype: " + triple.pred.type());
    		System.out.println("object:");
    		System.out.println("\ttoString: " + triple.obj.toString());
    		System.out.println("\tvalue: " + triple.obj.value());
    		System.out.println("\ttype: " + triple.obj.type());
    		System.out.println("\tdata type: " + triple.obj.dataType());
    		System.out.println("\tlang: " + triple.obj.lang());
    	}
    	buffered.close();
		
	}
    public static void main( String[] args ) throws IOException
    {
    	if (args.length == 0) {
    		System.out.println("Please pass the gzip data source (either URL or file) as argument");
    		System.exit(1);
    	}
    	if (args[0].startsWith("http")) {
    		readStream(new URL(args[0]).openStream());
    	} else {
    		readStream(new FileInputStream(args[0]));
    	}
    }
}
