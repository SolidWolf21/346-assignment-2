import  java.io.Serializable;

public class FEntry implements Serializable {
    
    public static final int Max_Name_length = 11;
    public String FileName;
    public int size; // this is the file size in bites
    public int FirstBlock; // index of the first FNode

    public FEntry(String FileName, int FirstBlock){
        this.FileName = FileName;
        this.FirstBlock = FirstBlock;
        this.size = 0;
    
    }

    public FEntry {
        this.FileName = "";
        this.FirstBlock = -1;
        this.size = 0;
    
    }
    
    public boolean isUsed() {
        return FileName != null && !FileName.isEmpty();
    }


}
