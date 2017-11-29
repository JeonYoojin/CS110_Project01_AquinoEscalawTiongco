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
        int i = 0;
        while(i < root.count && key > root.key[i]){ //Increment in Node while Key > Current Value
            i++;
        }
        if(i <= root.count && key == root.key[i]){ //Return Node if Key is in Node
            return root;
        }
        if(root.leaf){
            return null;
        }
        else{
            return search(root.getChild(i), key);
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
    
    
}
