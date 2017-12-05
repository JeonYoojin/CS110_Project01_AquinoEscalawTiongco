import java.io.*; //Again, habit

public class Values{
    int numRecords = -1;
    RandomAccessFile access;
    
    public Values(String s) throws IOException{ //Initializes Values class & RAF that contains all Inserted Strings   
        access = new RandomAccessFile(s, "rwd");
        access.seek(0);
        access.writeShort(numRecords+1);
    }   
    
    public void insert(String s) throws IOException{ //Insert method for Strings into corresponding Record #s
        numRecords++;
        access.seek(2 + numRecords*256);
        byte[] byteArray = s.getBytes("UTF8");
        access.writeShort(byteArray.length);
        access.write(byteArray);
        access.seek(0);
        access.writeShort(numRecords+1);
        System.out.println(s);
    }
    
    public void update(String s, int no) throws IOException{ //Updates String inp & its length
        access.seek(2 + no*256);
        byte[] byteArray = s.getBytes("UTF8");
        access.writeShort(byteArray.length);
        access.write(byteArray);
    }
    
    public String select(int n) throws IOException{ //Returns the String of a corresponding Record #
        access.seek(2 + n*256);
        int length = raf.readShort();
        byte[] byteArray = new byte[length];
        access.read(byteArray);
        return new String(byteArray, "UTF8");
    }
}
