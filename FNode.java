import java.io.Serializable;

public class FNode implemts Serializable {
    public int BlockIndex; // index of the data block
    public int NextBlock; // next FNode index if there is none then it goes to -1

    public FNode (int BlockIndex) {
        this.BlockIndex = BlockIndex;
        this.NextBlock = -1;
    }

    public FNode() {
        this.BlockIndex = -1;
        this.NextBlock = -1;
    }

    public boolean isFree() {
        return BlockIndex < 0;
    }
    
}