import java.io.*; //I put this out of habit pls forgib
import java.util.*;

//readLong returns the next 8 bytes of an input stream, which is often interpreted as a Long
//Apparently offset determines the position of a Node in the Values file 

/**
* <h1> BTree </h1>
*
* BTree class contains all the information of the BTree in the database
* @author Mico Aquino, Elise Gabriel Escalaw, John Eugene Tiongco
* @1.0
* @since November 28, 2017
*/

public class BTree {
    private final int order = 7; //order of BTree
    private long nodeCnt; //counts # of Nodes
    private final long startPntr = 0;
    private final long offsetInit = 16;
    private final int nodeLng = (3*order - 1) * 8;
    private RandomAccessFile data;
    
    /**
    * Constructor for BTree which initializes the actual file and the random access file
    * Checks if the file exists and reads if the start pointer points to it
    * If not it will increase the node count and add a new node
    * @param fileName   name of the file
    * @throws IOException
    * @see IOException
    */    

    public BTree(String fileName) throws IOException{ //BTree Constructor
	File file = new File(fileName);
	if(file.exists()){		
  	    data = new RandomAccessFile(fileName,"rwd");
	    data.seek(startPntr);	
	    nodeCnt = data.readLong();
	}
	else{
	    data = new RandomAccessFile(fileName, "rwd");	
	    data.seek(startPntr);
	    nodeCnt = 1;
	    data.writeLong(nodeCnt);
	    data.writeLong(0);
	    Node source = new Node(0); writeToNode(source);
	    source = null; 
	} 
    }

	/**
	* Method findRoot points toward the root through seeking 8 (8 bytes = 1 long)
	*
	* @throws IOException
	* @see IOException
	*/	

	public long findRoot()throws IOException{
		data.seek(8);
		return data.readLong();
	}

	/**
	* Method insert adds Key values and the offset value to the node's values
	* It calls multiple other methods, as well as recursively calling itself
	* Checks for existing Keys and Writes to Files using other methods
	* Prints that a Key value already exists if it is in already in the Node
	*
	* @param key			long indicating a Key value of a node
	* @param posn			long value indicating a node's position in the B-Tree		
	* @param offset			long value indicating a node's position in the Values file
	* @throws IOException
	* @see IOException
	*/
    
    public void insert(long key, long posn, long offset) throws IOException{
	Node temp = readNode(posn);
	if(!temp.leaf()){
	    temp.keyInsert(key, offset);	
	}
	else{
	    long newPosn = temp.getKey(key);
	    temp = null;
	    insert(key, newPosn, offset);
	}
	
	if(temp != null){
	    if(temp.keyArr[order - 1] != -1){
		split(temp);    
		data.seek(0);
		data.writeLong(nodeCnt);
	    }
	    else{
		writeToNode(temp);    
	    }
	    temp = null;
	}
    }
	
	/**
	* Method split divides the nodes when it overflows
	* It checks all the ancestors until it reaches an ancestor that doesn't split
	* And it will only move the keys then
	*
	* @param node     gets the values of the actual node
	* @throws IOException
	* @see IOException
	*/

    private void split(Node node) throws IOException{
	if(node.parentPntr == -1){
	    Node y = new Node(nodeCnt); nodeCnt++; //BNode y = rightChild
	    Node root = new Node(nodeCnt); nodeCnt++;
	    node.transfer(root, y);
	    node.parentPntr = root.nodeID; y.parentPntr = root.nodeID;
	    root.addChild(node.nodeID);
	    root.addChild(y.nodeID);
	    writeToNode(node); writeToNode(y); writeToNode(root);
	    data.seek(8); data.writeLong(root.nodeID);
	    node = null; y = null; root = null; //Sets original values as null
	}
	else{
	    Node ancestor = readNode(node.parentPntr); //Parent Node
	    Node y = new Node(nodeCnt); nodeCnt++;
	    node.transfer(ancestor, y); ancestor.addChild(y.nodeID); y.parentPntr = ancestor.nodeID;
	    attach(y);
	    writeToNode(y); writeToNode(node);
	    if(ancestor.keyArr[order - 1] != - 1){
		split(ancestor);    
	    }
	    writeToNode(ancestor);
	}
    }
    
	/**
	* Method attach reconnects nodes with their parent nodes after the split method
	*
	* @param ancestor		parent of a node to be reconnected
	* @throws IOException
	* @see IOException
	*/

    private void attach(Node ancestor) throws IOException{ //Reconnects Nodes after splitting
	for(int i = 0; i < (order - 1) / 2; i++){
	    if(ancestor.childArr[i] == - 1){
		break;    
	    }
	    data.seek(offsetInit + ancestor.childArr[i] * nodeLng); data.writeLong(ancestor.nodeID);
	}
    }    

    /**
	* Method searchNodes looks for a given specific Key through the Children Arrays of Nodes
	*
	* @param dex      long value indicating what to search for in the array
	* @return id         returns the corresponding ID
	* @throws IOException
	* @see IOException	
	*/

    public long searchNodes(long dex, long posn) throws IOException{ //Recursively searches for a Key in one of the BTree's Nodes
	Node seeker = readNode(posn);
	long newPosn;
	for(int i = 0; i < order - 1; i++){
	    if(seeker.keyArr[i] == dex){
		return seeker.recordOffset[i];    
	    }
	    else if(seeker.keyArr[i] < dex && seeker.keyArr[i + 1] == 1 && seeker.childArr[i + 1] != -1){
		newPosn = seeker.childArr[i + 1]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	    else if(seeker.keyArr[i] > dex && seeker.childArr[i] != -1){
		newPosn = seeker.childArr[i]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	    else if(i == order - 2 && seeker.childArr[i + 1] != -1){
		newPosn = seeker.childArr[i + 1]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	}
	return -1;
    }
	
    /**
	* Method writeToNode writes on the nodes along the order of the BTree
	*
	* @param node     gets the values of the actual node
	* @throws IOException
	* @see IOException
	*/

    private void writeToNode(Node node) throws IOException{ //Writes the Node's values onto a file
	data.seek(offsetInit + node.nodeID * nodeLng);
	data.writeLong(node.parentPntr);
	for(int i = 0; i < order; i++){
	    data.writeLong(node.childArr[i]);
	    if(i != order - 1){
			data.writeLong(node.keyArr[i]);
			data.writeLong(node.recordOffset[i]);
	    }
	}
    }

    /**
	* Method readNode reads the nodes along the order of the BTree
	*
	* @param posn     stores the value of the nodeID based on the position of the Node
	* @return toRead         returns what is to be read
	* @throws IOException
	* @see IOException	
	*/

    private Node readNode(long posn) throws IOException{
	data.seek(offsetInit + posn * nodeLng);
	Node toRead = new Node(posn);
	toRead.parentPntr = data.readLong();
	for(int i = 0; i < order; i++){
	    toRead.childArr[i] = data.readLong();
	    if(i != order - 1){
		toRead.keyArr[i] = data.readLong();
		toRead.recordOffset[i] = data.readLong();
	    }
	}
	return toRead;
    }
}

/**
* Constructor for Sub-Class Node
*Sub-class Node is used to initialize the Nodes to be Inserted into the B-Tree
*B-Trees have multiple branches, and as such arrays of Children must also be specified
*The sub-class also handles the storage of multiple Key values in Arrays
*
* @param posn	stores the value of the nodeID based on the position of the Node
*/

class Node{
	private final int order = 7;
    long[] childArr; //Array of References
    long[] keyArr; //Array of Key Values
    long[] recordOffset; //Array of Offsets
    long parentPntr, nodeID; //parentPntr determines whether a Node has a parent or not
    
    public Node(long posn){
	keyArr = new long[order]; //Size of Keys array
    childArr = new long[order + 1]; //size of Children array
	recordOffset = new long[order];
	parentPntr = -1;
	nodeID = posn;
	for(int i = 0; i < order; i++){
		childArr[i] = -1;
		if(i < order){
		   keyArr[i] = -1;
		   recordOffset[i] = -1;
		}
	}
    }   
    
    /**
	* Method leaf checks if the node has children
	* If the first element of childArr returns -1, that denotes that there are no children
	*
	* @return boolean	returns a true or false statement depending on if the Node has a Child
	*/

    public boolean leaf(){ 
	if(childArr[0] != -1){
	    return true;	
	}
	return false;
    }

    /**
	* Method getKey looks for Node IDs in a Children Array given a specific Key
	*
	*   @param dex      long value indicating what to search for in the array
	*   @return id         returns the corresponding ID
	*/
	
    public long getKey(long dex){ 
	    long id = -1; //returns Child's ID
	    for(int i = 0; i < order - 1; i++){
			if(dex > keyArr[i] && dex < keyArr[i + 1] || keyArr[i + 1] == -1){
				return childArr[i + 1];	
			}
			else if(dex < keyArr[i]){
				return childArr[i];
			}
	    }
       	    return id;
    }
    
	/**
	* Method addChild adds a node to a parent node's array of children from a given location in the BTree file
	*
	* @param posn     position/location of a value in the Btree file
	*/

    public void addChild(long posn){ 
        for(int i = 0; i < order; i++){
		    if(childArr[i] == 1){
				childArr[i] = posn;
				break;
		    }
		}
    }
	
	/**
	* Method keyInsert adds keys to a node given a key value and a node's position in the values file
	*
	* @param key		long value indicating the value of the key to be inserted
	* @param offset	long value indicating a node's position in the values file
	*/

    public void keyInsert(long key, long offset){ 
	    for(int i = order - 2; i >= 0; i--){
		if(keyArr[i] == -1){
		    if(i == 0){
				keyArr[i] = key;  
				recordOffset[i] = offset;
		    }
		}
		
		if(key < keyArr[i]){
		    keyArr[i + 1] = keyArr[i];
		    recordOffset[i + 1] = recordOffset[i];
		    childArr[i + 2] = childArr[i + 1];
		    if(i == 0){
				keyArr[i] = key;
				recordOffset[i] = offset;
				childArr[i + 1] = -1;
		    }
		}
		else{
		    keyArr[i + 1] = key;
		    recordOffset[i + 1] = offset;
		    childArr[i + 2] = -1; 
		}
	    }
    }

	/**
	* Method transfers relevant Node data between a Node parent and child
	* For use in split and relinkCP
	*
	* @param parent		the parent of the node
	* @param offspring        a node in the parent node's children array
	*/

    public void transfer(Node parent, Node offspring){
	parent.keyInsert(keyArr[(order - 1) / 2], recordOffset[(order - 1) / 2]); //Order - 1 / 2 determines the median of each node
	keyArr[(order - 1) / 2] = -1; //voids the Key value of the median in each Node after every split
	recordOffset[(order - 1) / 2] = -1; //Same here
	for(int i = ((order - 1) / 2) + 1; i <= order; i++){
	    offspring.addChild(childArr[i]);
	    childArr[i] = -1;
	    if(i < order){
			offspring.keyInsert(keyArr[i], recordOffset[i]);
			keyArr[i] = -1;
			recordOffset[i] = -1;
	    }
	}
    }
}

