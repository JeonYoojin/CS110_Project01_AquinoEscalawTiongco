
public class BNode {
    static int order; //Determines order of Tree
    int count; //Determines # of Keys in Node
    int key[]; //Array of Key Values
    BNode child[]; //Array of references;
    boolean leaf; //Boolean to check whether Node is leaf or not
    BNode parent; //Parent of current Node
    
    public BNode(){} //default constructors for new Nodes
    
    public BNode(int order, BNode parent){
        this.order = order; //Assigns order/size
        this.parent = parent; //Assigns parent
        key = new int[2*order - 1]; //size of Key array
        child = new BNode[2*order]; //size of References array
        leaf = true; //Assumes every Node is a leaf at first
        count = 0; //Remains 0 until Keys are added        
    }
    
    public int getValue(int dex){ //Returns Key value at specified index
        return key[dex];
    }
    
    public BNode getChild(int dex){ //returns child of Node at specified index
        return child[dex];
    }
    
    
}
