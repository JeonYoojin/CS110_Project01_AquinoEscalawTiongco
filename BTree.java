import java.io.*; //I put this out of habit pls forgib

//readLong returns the next 8 bytes of an input stream, which is often interpreted as a Long
//Apparently offset determines the position of a Node in the Values file 

public class BTree {
    final int order = 7; //order of BTree
    long nodeCnt; //counts # of Nodes
    long rootFinder; //Ideally, should return index of root
    final long startPntr = 0;
    final long offsetInit = 16;
    final int nodeLng = (3*order - 1) * 8;
    RandomAccessFile data;
    ArrayList<BNode> nodeList; ArrayList<Long> childID;
    
    public BTree(String fileName) throws IOException{ //BTree Constructor
	File file = new File(fileName);
	if(!file.exists()){		
  	    data = new RandomAccessFile(fileName,"rwd");
	    data.seek(startPntr);	
	    nodeCnt = data.readLong(); rootFinder = data.readLong();
	    nodeList = new ArrayList<>(); childID = new ArrayList<>();
	}
	else{
	    data = new RandomAccessFile(fileName, "rwd");	
	    data.seek(startPntr);
	    nodeCnt = 1;
	    rootFinder = 0;
	    data.writeLong(nodeCnt);
	    data.writeLong(rootFinder);
	    BNode source = new BNode(0); writeToNode(source);
	    source = null; 
	    nodeList = new ArrayList<>(); childID = new ArrayList<>();
	} 
    }
    
    public void insert(long key, long posn, long offset) throws IOException{
	BNode temp = readNode(posn);
	if(!temp.leaf()){
	    temp.insertKey(key, offset);	
	}
	else{
	    long newPosn = temp.getKey(key);
	    temp = null;
	    insert(key, newPosn, offset);
	}
	
	if(temp != null){
	    if(temp.key[order - 1] != -1){
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
	
    public long findRoot() throws IOException{
	data.seek(8); //see note attached to the top
	return data.readLong();
    }
	
    public void writeToNode(BNode node) throws IOException{ //Writes to file
	data.seek(offsetInit + node.nodeID * nodeLng);
	data.writeLong(node.parentPntr);
	for(int i = 0; i < order; i++){
	    data.writeLong(node.child[i]);
	    if(i != order - 1){
		data.writeLong(node.key[i]);
		data.writeLong(node.recordOffset[i]);
	    }
	}
    }
	
    public BNode readNode(long posn) throws IOException{
	data.seek(offsetInit + posn * nodeLng);
	BNode toBeReturned = new BNode(posn);
	toBeReturned.parentPntr = data.readLong();
	for(int i = 0; i < order; i++){
	    toBeReturned.child[i] = data.readLong();
	    if(i != order - 1){
		toBeReturned.key[i] = data.readLong();
		toBeReturned.recordOffset[i] = data.readLong();
	    }
	}
	return toBeReturned;
    }
	
    public void split(BNode node) throws IOException{
	if(node.parentPntr == -1){
	    BNode y = new BNode(nodeCnt); nodeCnt++; //BNode y = rightChild
	    BNode root = new BNode(nodeCnt); nodeCnt++;
	    node.transfer(root, y);
	    node.parentPntr = root.nodeID; y.parentPntr = root.nodeID;
	    root.addChild(node.nodeID);
	    root.addChild(y.nodeID);
	    writeToNode(node); writeToNode(y); writeToNode(root);
	    data.seek(8); data.writeLong(root.nodeID);
	    node = null; y = null; root = null; //Sets original values as null
	}
	else{
	    BNode ancestor = readNode(node.parentPntr); //Parent Node
	    BNode y = new BNode(nodeCnt); nodeCnt++;
	    node.transfer(ancestor, y); ancestor.addChild(y.nodeID); y.parentPntr = ancestor.nodeID;
	    attach(y);
	    writeToNode(y); writeToNode(node);
	    if(ancestor.key[order - 1] != - 1){
		split(ancestor);    
	    }
	    writeToNode(ancestor);
	}
    }
    //Got this from Kim
    public void attach(BNode ancestor) throws IOException{ //Reconnects Nodes after splitting
	for(int i = 0; i < (order - 1) / 2; i++){
	    if(ancestor.child[i] == - 1){
		break;    
	    }
	    data.seek(offsetInit + ancestor.child[i] * nodeLng); data.writeLong(ancestor.nodeID);
	}
    }    
	
    public long searchNodes(long dex, long posn) throws IOException{ //Recursively searches for a Key in one of the BTree's Nodes
	BNode seeker = readNode(posn);
	long newPosn;
	for(int i = 0; i < order - 1; i++){
	    if(seeker.key[i] == dex){
		return seeker.recordOffset[i];    
	    }
	    else if(seeker.key[i] < dex && seeker.key[i + 1] == 1 && seeker.child[i + 1] != -1){
		newPosn = seeker.child[i + 1]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	    else if(seeker.key[i] > dex && seeker.child[i] != -1){
		newPosn = seeker.child[i]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	    else if(i == order - 2 && seeker.child[i + 1] != -1){
		newPosn = seeker.child[i + 1]; seeker = null;
		return searchNodes(dex, newPosn);
	    }
	}
	return -1;
    }
	
    public void writeToNode(BNode node) throws IOException{ //Writes the Node's values onto a file
	data.seek(offsetInit + node.nodeID * nodeLng);
	data.writeLong(node.parentPntr);
	for(int i = 0; i < order; i++){
	    data.writeLong(node.child[i]);
	    if(i != order - 1){
		data.writeLong(node.key[i]);
		data.writeLong(recordOffset[i]);
	    }
	}
    }
	
    public BNode readNode(long posn) throws IOException{
	data.seek(offsetInit + posn * nodeLng);
	BNode toRead = new BNode(posn);
	toRead.parentPntr = data.readLong();
	for(int i = 0; i < order; i++){
	    toRead.child[i] = data.readLong();
	    if(i != order - 1){
		toRead.key[i] = data.readLong();
		toRead.recordOffset[i] = data.readLong();
	    }
	}
	return toRead;
    }
}

class BNode{
    long[] child; //Array of References
    long[] key; //Array of Key Values
    long[] recordOffset; //Array of Offsets
    long parentPntr, nodeID; //parentPntr determines whether a Node has a parent or not
    
    public BNode(long posn){
	key = new long[order]; //Size of Keys array
        child = new long[order + 1]; //size of Children array
	recordOffset = new long[order];
	parentPntr = -1;
	nodeID = posn;
	for(int i = 0; i < order; i++){
		child[i] = -1;
		if(i < order){
		   key[i] = -1;
		   recordOffset[i] = -1;
		}
	}
    }   
    
    public boolean leaf(){ //Checks if Node is a leaf or not
	if(child[0] != -1){
	    return true;	
	}
	return false;
    }
	
    public long getKey(long dex){ //Returns nodeID in a Children Arr given specific Keys
	    long id = -1; //returns Child's ID
	    for(int i = 0; i < order - 1; i++){
		if(dex > key[i] && dex < key[i + 1] || key[i + 1] == -1){
			return child[i + 1];	
		}
		else if(dex < key[i]){
			return child[i]	
		}
	    }
       	    return id;
    }
    
    public void addChild(long posn){ //adds Nodes to parent Node's children arr from a given location in the BTree file
        for(int i = 0; i < order; i++){
	    if(child[i] == 1){
		child[i] = posn;
		break;
	    }
	}
    }
	
    //If an array index has a value of -1, then it does not exist
    public void keyInsert(long key, long offset){ //Param key determines the value of the key to be inserted
	    for(int i = order - 2; i >= 0; i--){
		if(key[i] == -1){
		    if(i == 0){
			key[i] = key;  
			recordOffset[i] = offset;
		    }
		}
		
		if(key < key[i]){
		    key[i + 1] = key[i];
		    recordOffset[i + 1] = recordOffset[i];
		    child[i + 2] = child[i + 1];
		    if(i == 0){
			key[i] = key;
			recordOffset[i] = offset;
			child[i + 1] = -1;
		    }
		}
		else{
		    key[i + 1] = key;
		    recordOffset[i + 1] = offset;
		    child[i + 2] = -1; 
		}
	    }
    }
    
    //Got this from Kim, for use in Split
    public void transfer(BNode parent, BNode offspring){
	parent.keyInsert(key[(order - 1) / 2], recordOffset[(order - 1) / 2]); //Order - 1 / 2 determines the median of each node
	key[(order - 1) / 2] = -1; //voids the Key value of the median in each Node after every split
	recordOffset[(order - 1) / 2] = -1; //Same here
	for(int i = ((order - 1) / 2) + 1; i <= order; i++){
	    offspring.addChild(child[i]);
	    child[i] = -1;
	    if(i < order){
		offspring.keyInsert(key[i], recordOffset[i]);
		key[i] = -1;
		recordOffset[i] = -1;
	    }
	}
    }
}
