import java.util.*;
import java.io.*;
/**
* <h1> Values </h1>
*
* Java class that contains all the value strings of the database
* @author Mico Aquino, Elise Gabriel Escalaw, John Eugene Tiongco
* @1.0
* @since November 28, 2017
*/
public class Values{
	
	private final int START_POINTER = 0;
	private final int STRING_MAX_LENGTH = 256;
	private final int ALLOCATED_STRLEN = 2;
	private final int TOTAL_BYTE_SIZE = STRING_MAX_LENGTH + ALLOCATED_STRLEN;
	private final int HEADER_SIZE = 8;
	private RandomAccessFile data;
	private long numRecords;

	/**
    	* Constructor for Values which initializes the actual file 
   	 * Checks the initial file's byte and writes the number of existing records
    	* 
    	* @param fileName   name of the file
    	* @throws IOException
    	* @see IOException
    	*/   
	public Values(String fileName) throws IOException{
		File file = new File(fileName);
		if(!file.exists()){
			numRecords = 0;
			data = new RandomAccessFile(fileName,"rwd");	
			data.seek(START_POINTER);
			data.writeLong(numRecords);
		} 
		else{
			data = new RandomAccessFile(fileName, "rwd");
			data.seek(START_POINTER);
			numRecords = data.readLong();
		} 
	}
	
	/**
    	* insertEntry seeks the initial pointer and increments the number of records 
    	* it also writes the length of the entry into the file by accessing getBytes
    	* 
    	* @param entry  byte that will be read
    	*/  
	public void insertEntry(String entry) throws IOException{
		data.seek(HEADER_SIZE + (numRecords)*(TOTAL_BYTE_SIZE));
		byte[] word = entry.getBytes("UTF8");
		data.writeShort(entry.length());
		data.write(word);
		data.seek(START_POINTER);
		data.writeLong(numRecords++);
	}
	
	/**
    	* updateEntry writes the updated length and word for the output file
    	* 
    	* @param point  pointer for locating the the characters that were encoded into 8 bytes (UTF8)
    	* @param entry  byte that will be read
    	*/
	public void updateEntry(String entry, long point) throws IOException{
		byte[] word = entry.getBytes("UTF8");
		data.seek(HEADER_SIZE+point*TOTAL_BYTE_SIZE);
		data.writeShort(entry.length());
		data.write(word);
	}
	
	/**
    	* readEntry reads the word that is being pointed to 
    	* 
    	* @param point  pointer for locating the the characters that were encoded into 8 bytes (UTF8)
    	*/
	public String readEntry(long point) throws IOException{
		data.seek(HEADER_SIZE+point*TOTAL_BYTE_SIZE);
		short size = data.readShort();
		byte[] word = new byte[size];
		data.read(word);
		String output = new String(word,"UTF8");
		return output;
	}

	/**
    	* amtRecord returns the number of records in the Values file
    	* 
    	*/	
	public long amtRecord(){
		return numRecords;
	}
}
