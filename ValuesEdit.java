import java.util.*;
import java.io.*;

public class ValuesEdit{
	
	private final int START_POINTER = 0;
	private final int STRING_MAX_LENGTH = 256;
	private final int ALLOCATED_STRLEN = 2;
	private final int TOTAL_BYTE_SIZE = STRING_MAX_LENGTH + ALLOCATED_STRLEN;
	private final int HEADER_SIZE = 8;
	private RandomAccessFile data;
	private long numRecords;

	public ValuesEdit(String fileName) throws IOException{
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

	public void insertEntry(String entry) throws IOException{
		data.seek(HEADER_SIZE + (numRecords)*(TOTAL_BYTE_SIZE));
		byte[] word = entry.getBytes("UTF8");
		data.writeShort(entry.length());
		data.write(word);
		data.seek(START_POINTER);
		data.writeLong(numRecords++);
		//<DEBUG> System.out.println(entry);
	}

	public void updateEntry(long point, String entry) throws IOException{
		byte[] word = entry.getBytes("UTF8");
		data.seek(HEADER_SIZE+point*TOTAL_BYTE_SIZE);
		data.writeShort(entry.length());
		data.write(word);
	}

	public String readEntry(long point) throws IOException{
		data.seek(HEADER_SIZE+point*TOTAL_BYTE_SIZE);
		short size = data.readShort();
		byte[] word = new byte[size];
		
		/*for(int i = 0; i < size; i++){
			word[i] = data.readByte();
		}*/
		data.read(word);
		String output = new String(word,"UTF8");
		return output;
	}
	
	public long amtRecord(){
		return numRecords;
	}
}