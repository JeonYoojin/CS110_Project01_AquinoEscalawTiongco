import java.util.*;
import java.io.*;

public class btdb{

	public static void main(String[] args) throws IOException{
		Scanner read = new Scanner(System.in);

		BTree tree = new BTree(args[0]);
		ValuesEdit values = new ValuesEdit(args[1]);

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
				
			} else if(input[0].equals("update")){
				long key = Long.parseLong(input[1]);
				String word = "";
				long offset = tree.searchNodes(key,tree.findRoot());
				
				for(int i = 2; i < input.length; i++){
						word = word + input[i] + " ";
				}
				
				if(offset != -1){
					values.updateEntry(key,word);
					System.out.printf("< %d updated.\n", key);
				}
				else{
					System.out.println("ERROR: key does not exist.");
				}
			} else if(input[0].equals("select")){
				long key = Long.parseLong(input[1]);
				String word = "";
				long offset = tree.searchNodes(key,tree.findRoot());
				if(offset != -1){
					System.out.printf("> %d => %s\n",key,values.readEntry(offset));
				}
				else{
					System.out.println("ERROR: key does not exist.");
				}
			} else if(input[0].equals("exit")){
				break;
			} else{
				System.out.println("ERROR: invalid command.");
			}
		}

	}

}