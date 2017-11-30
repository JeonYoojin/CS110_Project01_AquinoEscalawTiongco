import java.io.*;

public class BTree {
    public static int order; //order of BTree
    public BNode root; //B Tree Root Node
    
    public BTree(int order){ //BTree Constructor
        this.order = order;
        root = new BNode(order,null);
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
    
    public void split(BNode x, int i, BNode y){
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
        int cnt = x.count; //Counts # of keys in Node X
        if(x.leaf){
            while(cnt >= 1 && key < x.key[cnt - 1]){ //Looks for a spot where program can put a key
                x.key[cnt] = x.key[cnt - 1]; //Shifts Values to make room
                cnt--;
            }
            x.key[cnt] = key; //Assigns value to Key
            x.count++; //Increments # of Keys in this Node
        }
        else {
            int j = 0;
            while(j < x.count && key > x.key[j]){ //Searches spot for recursive insert
                j++;   
            }
            if(x.child[j].count == (2*order) - 1){
                split(x, j, x.child[j]); // Splits on X's ith child
                if(key > x.key[j]){
                    j++;   
                }
            }
            insertNF(x.child[j], key); //Recursive insert
        }
    }
    
    public void insertGen(BTree branch, int key){
        
    }
}
