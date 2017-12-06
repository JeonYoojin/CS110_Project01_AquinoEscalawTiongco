import java.util.*;
import java.io.*;
/**
* <h1> btdb </h1>
* Project Driver Class
* Handles Inputs, as well as Errors for the BTree and the Value files
* 
* @author Mico Aquino, Elise Gabriel Escalaw, John Eugene Tiongco 
* @1.0
* @since November 28, 2017
*/
public class btdb{
    
    /**
     * Main method for btdb class
     * Initializes and passes required fields, such as BTree tree and Values values, to other files
     * @param args accepts relevant inputs for BTree and Values
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException{
        Scanner read = new Scanner(System.in);
	BTree tree = new BTree(args[0]);
        Values values = new Values(args[1]);
	while(true){
            System.out.print("> "); //the pointer thing for inputs
            String[] input = read.nextLine().split("\\s"); // splits input by spaces???
            if(input[0].equals("insert")){
                long key = Long.parseLong(input[1]);
                long amt = values.amtRecord();
                if(tree.searchNodes(key,tree.findRoot()) == -1){ //means it doesn't exist yet
                    String word = "";
                    for(int i = 2; i < input.length; i++){
                        word = word + input[i] + " ";
                    }					
                    tree.insert(key,tree.findRoot(),amt);
                    values.insertEntry(word);
                    System.out.printf("< %d inserted.\n",key);
                }
                else{
                    System.out.printf("ERROR: %d already exists.\n",key);
                }
            } 
            else if(input[0].equals("update")){
		long key = Long.parseLong(input[1]);
		String word = "";
		long offset = tree.searchNodes(key,tree.findRoot());
		for(int i = 2; i < input.length; i++){
                    word = word + input[i] + " ";
		}				
		if(offset != -1){
                    values.updateEntry(word,offset);
                    System.out.printf("< %d updated.\n", key);
		}
		else{
                    System.out.println("ERROR: key does not exist.");
		}
            } 
            else if(input[0].equals("select")){
            	long key = Long.parseLong(input[1]);
            	String word = "";
		long offset = tree.searchNodes(key,tree.findRoot());
		if(offset != -1){
                    System.out.printf("> %d => %s\n",key,values.readEntry(offset));
		}
		else{
                    System.out.println("ERROR: key does not exist.");
		}
            } 
            else if(input[0].equals("exit")){
            	break;
            } 
            else{
                System.out.println("ERROR: invalid command.");
            }
	}
    }
}
