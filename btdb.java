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
				int key = Integer.parseInt(input[1]);
				if(tree.search(tree.root,key) == null){ //means it doesn't exist yet
					String word = "";
				
					for(int i = 2; i < input.length; i++){
						word = word + input[i] + " ";
					}
					
					values.insertEntry(word);
					tree.insertDF(tree,key);
					System.out.printf("< %d inserted.\n",key);
					//testing purposes
					System.out.println("key:" + input[1]);
					System.out.println("value:"+ word);
				}
				else{
					System.out.printf("ERROR: %d already exists.\n",key);
				}
				
			} else if(input[0].equals("update")){
				//find key in BTree
				
				//get value from key in BTree
				//overwrite the value
			} else if(input[0].equals("select")){
				System.out.println(values.readEntry(Integer.parseInt(input[1])));
				//find key in BTree
				//get value from key in BTree
				//return and display value
			} else if(input[0].equals("exit")){
				break;
			} else{
				System.out.println("ERROR: invalid command.");
			}
		}

	}

}