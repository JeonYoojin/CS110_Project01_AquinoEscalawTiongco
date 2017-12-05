import java.io.*; //I put this out of habit pls forgib
import java.util.*;

//GGWP mga repa I have no idea what I'm doing how do you do Print method
//UPDATE: I have finished print method, it's so jackass

public class BTree {
    static int order; //order of BTree
    BNode root; //B Tree Root Node
    long splitCnt; //data.bt
    long insertCnt; //Data.values
    
    public BTree(int order){ //BTree Constructor
        this.order = order;
        root = new BNode(order,null);
        //splitCnt = -1; insertCnt = -1;
    }
    
    //Method to search for given Node where we want to Insert a Key value
    //Returns a Node with Key values in it
    public BNode search(BNode root, int key){ 
        int dex = 0;
        while(dex < root.count && key > root.key[dex]){ //Increment in Node while Key > Current Value
            dex++;
        }
        if(dex <= root.count && key == root.key[dex]){ //Return Node if Key is in Node
            return root;
        }
        if(root.leaf){
            return null;
        }
        else{
            return search(root.getChild(dex), key);
        }
    }
    
    public void split(BNode x, int i, BNode y){ //Done when a Node overflows
        BNode z = new BNode(order, null); //additional Node for split
        z.leaf = y.leaf;//Sets leaf boolean as the same as y
        z.count = order - 1; //Updated size
        for(int j = 0; j < order - 1; j++){
            z.key[j] = y.key[j + order]; //Copies end of Y to front of Z
        }
        if(!y.leaf){ //Reassigns Children Nodes if Y is not a Leaf
            for(int k = 0; k < order; k++){
                z.child[k] = y.child[k + order];// Reassigning Y-children
            }
        }
        y.count = order - 1; //new Size of Y
        for(int j = x.count; j > i; j--){//If keys are pushed onto x, children nodes must be reassigned
            x.child[j + 1] = x.child[j]; //Shifting X-children 
        }
        x.child[i + 1] = z; //Reassigns i+1 child of X
        
        for(int j = x.count; j > i; j--){
            x.key[j + 1] = x.key[j]; //Shifts keys
        }
        x.key[i] = y.key[order - 1]; ///Pushes value up to root
        y.key[order - 1] = 0; //Erases value where it was pushed from
        
        for(int j = 0; j < order - 1; j++){
            y.key[j + order] = 0;// "Deletes" old values
        }
        x.count++; //Increases key count in X
    }
    
    public void insertNF(BNode root, int key){ //Insert method when Node is not Full
        int cnt = root.count; //Counts # of keys in Node X
        System.out.println("ROOT KEY: " + root.key[0]);
        //insertCnt++;
        if(root.leaf){
            while(cnt >= 1 && key < root.key[cnt - 1]){ //Looks for a spot where program can put a key
                root.key[cnt] = root.key[cnt - 1]; //Shifts Values to make room
                cnt--;
            }
            root.key[cnt] = key; //Assigns value to Key
            root.count++; //Increments # of Keys in this Node
        }
        else {
            int j = 0;
            while(j < root.count && key > root.key[j]){ //Searches spot for recursive insert
                j++;   
            }
            insertNF(root.child[j], key);
            if(root.child[j].count == order){
                //System.out.println("NODE IS FULL. MUST SPLIT.");
                split(root, j, root.child[j]); // Splits on X's ith child
            }
            insertNF(root.child[j], key); //Recursive insert
        }
    }
    
    public void insertDF(BTree ents, int key){ //Default Insert method
        BNode root = ents.root; //Finds the Node to be inserted, and starts at the Root Node
        if(root.count == order - 1){ //Checks if Node is full
            BNode axis = new BNode(order, null); //New Node   
            //The ff are to initialize the new Node
            ents.root = axis;
            axis.leaf = false;
            axis.count = 0;
            axis.child[0] = root;
            split(axis, 0, root); //Splits the root
            insertNF(axis, key); //Calls insertNF if root is full
        }
        else {
            insertNF(root, key); //Inserts into Root Node if it is not full   
        }
    }
    
    public void print(BNode node){ //Method to Print Node, or recurses when Root Node is not a leaf
        for(int i = 0; i < node.count; i++){
            System.out.println(node.getKey(i) + " "); //Prints out Root Node  
        }
        if(!node.leaf){
            for(int j = 0; j <= node.count; j++){ //Pre-order Traversal of B-Tree
                if(node.getChild(j) != null){
                    System.out.println(node.getChild(j));   
                }
            }
        }
    }
    
    public void searchNode(BTree ents, int key){ //Prints out Node
        BNode pholder = new BNode(order, null);
        pholder = search(ents.root, key);
        if(pholder == null){
            System.out.println("Specified Key not found.");
        }
        else{
            print(pholder);   
        }
    }
}
