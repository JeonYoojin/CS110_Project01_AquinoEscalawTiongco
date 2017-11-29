import java.util.*;
import java.io.*;


public class btdb {
    private long cntRecords; 
    private RandomAccessFile File;
    public static void main(String args[]) throws FileNotFoundException{
        Scanner read = new Scanner(System.in);
        String strFile = read.next();
        
        
        btdb(String strFile) throws IOException {
            File file = new File(strFile);
            if(!file.exists()){
                this.cntRecords = 0;
                this.file = new RandomAccessFile(strFile, "rwd");
                this.file.seek(RECORD_COUNT_OFFSET);
                this.file.writeLong(this.cntRecords);
            }
            
            else{
                this.file = new RandomAccessFile(strFile, "rwd");
                this.file.seek(RECORD_COUNT_OFFSET);
                this.cntRecords = this.file.readLong();
            }
        }
        
        
    }
}
