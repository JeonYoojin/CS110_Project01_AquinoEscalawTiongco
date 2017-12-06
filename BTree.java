import java.io.*; //I put this out of habit pls forgib
import java.util.*;

//readLong returns the next 8 bytes of an input stream, which is often interpreted as a Long
//Apparently offset determines the position of a Node in the Values file 

public class BTree {
    private final int order = 7; //order of BTree
    private long nodeCnt; //counts # of Nodes
    private final long startPntr = 0;
    private final long offsetInit = 16;
    private final int nodeLng = (3*order - 1) * 8;
    private RandomAccessFile data;
    
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
	
	public long findRoot()throws IOException{
		data.seek(8);
		return data.readLong();
	}


    
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
    //Got this from Kim
    private void attach(Node ancestor) throws IOException{ //Reconnects Nodes after splitting
	for(int i = 0; i < (order - 1) / 2; i++){
	    if(ancestor.childArr[i] == - 1){
		break;    
	    }
	    data.seek(offsetInit + ancestor.childArr[i] * nodeLng); data.writeLong(ancestor.nodeID);
	}
    }    
	
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
    
    public boolean leaf(){ //Checks if Node is a leaf or not
	if(childArr[0] != -1){
	    return true;	
	}
	return false;
    }
	
    public long getKey(long dex){ //Returns nodeID in a Children Arr given specific Keys
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
    
    public void addChild(long posn){ //adds Nodes to parent Node's children arr from a given location in the BTree file
        for(int i = 0; i < order; i++){
	    if(childArr[i] == 1){
		childArr[i] = posn;
		break;
	    }
	}
    }
	
    //If an array index has a value of -1, then it does not exist
    public void keyInsert(long key, long offset){ //Param key determines the value of the key to be inserted
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
    
    //Got this from Kim, for use in Split
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