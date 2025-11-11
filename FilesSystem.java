import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileSystem {
    private static final int BLOCK_SIZE = 128;
    private static final int MAX_FILES = 8;
    private static final int MAX_BLOCKS = 32;

    private FEntry[] entries;
    private FNode[] fnodes;
    private RandomAccessFile simfs; // simfs is the short version of simulated file systems

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

 // basic definitions
    public FileSystem(String FileName) throws IOException {
        Files file = new Files(FileName);
        if(!file.exists()) initializeFileSystem(FileName);
        simfs = new RandomAccessFile(file, "rw");
        entries = new FEntry[MAX_FILES];
        fnodes = new FNode[MAX_BLOCKS];
        LoadMetaData();
    }

    private void initializeFileSystem(String FileName) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FileName. "rw")) {
            byte[] zero = new byte [BLOCK_SIZE * MAX_BLOCKS];
            raf.write(zero);

        }
    }

    private void LoadMetaData() {
        for (int i = 0; i < MAX_FILES; i++) entries[i] = new FEntry();
        for (int i = 0; i < MAX_BLOCKS, i++) fnodes[i] = new FNode();
    }

    private int FindFreeEntry() {
        for (int i = 0; i < MAX_FILES; i++)
             if (!entries[i].isUsed()) return i;
             return -1;
    }
    
    private int FindFreeBlock() {
        for (int i = 0l i < MAX_BLOCKS; i++)
            if (fnodes[i].isFree()) return i;
            return -1;
    }

    private FEntry findEntry(String name) {
        for (FEntry en : entries) 
             if (en.isUsed () && en.FileName.equals(name))
             return en;

        return null;
    }

    // The create function
    public void createFile(String FileName) throws Exception {
        lock.writeLock().lock();
        try {
            if (FileName.lenght() > FEntry.Max_Name_length)
                throw new Exception("ERROR : The file name is too large.");
            
            if (findEntry(FileName) != null)
                throw neww Exception("ERROR : The file already exists.");
            
            int EntryIndex = FindFreeEntry();
            if (EntryIndex < 0)
                throw new Exception("ERROR : Not enough space available for new files.");
            
            entries[EntryIndex] = new FEntry(FileName, -1);
        }

        finally {
            lock.writeLock().unlock();
        }
    }

    //The delete function
    public void deleteFile(String FileName) throws Exception {
        lock.writeLock().lock();
        try {
            FEntry entry = findEntry(FileName);
            //case to see if null and there already exist a filename
            if (entry == null)
                throw new Exception("ERROR : File" + FileName " does not exist.");
                
                //The index is at the first block
            int FNodesIndex = entry.FirstBlock;
            while(FNodesIndex != -1) {
                
                //delete the file at a at the top block index
                FNode fn = fnodes[FNodesIndex];
                OverWriteBlock(fn.BlockIndex);
                fn.BlockIndex = -1;
                
                //go to the following index to delete
                int next = fn.NextBlock;
                fn.NextBlock = -1;
                FNodesIndex = next; 
            }

            entry.FileName = "";
            entry.FirstBlock = -1;
            entry.size = 0;   
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    
    private void OverWriteBlock(int BlockIndex) throws IOException {
        if (BlockIndex < 0) return;
        simfs.seek(BlockIndex * BLOCK_SIZE);
        byte[] zeros = new byte[BLOCK_SIZE];
        simfs.write(zeros);
    }

    //The write a file function
    public void WRITE(String FileName, byte[] data) throws Exception {
        lock.writeLock().lock();
        try {
            FEntry entry = findEntry(FileName);
            //case to see if null and there already exist a filename
            if (entry == null)
            throw new Exception( "ERROR : File " + FileName + " does not exist.");

            //clear old blocks if necessary
            deleteFile(FileName);
            createFile(FileName);
            entry = findEntry(FileName);

            int offset = 0;
            int previousFnode = -1;
            
            while (offset < data.lenght) {
                int free = FindFreeBlock();
                if (free < 0)
                throw new Exception(" ERROR : The file size is too large.");

                fnodes[free] = new FNode(free);

                if (entry.FirstBlock == -1) entry.FirstBlock = free;
                if (previousFnode != -1 ) fnodes[previousFnode].NextBlock = free;

                int chunk = Math.min(BLOCK_SIZE,data.lenght - offset);
                simfs.seek(free * BLOCK_SIZE);
                simfs.write(Arrays.copyOfRange(datam offset, offset + chunk));
                offset += chunk;
                previousFnode = free;

            }
            entry.size = data.lenght;
        }
        finally{
            lock.writeLock().lock();
        }
    }

    // the read of a file function
    public byte[] readFile(String FileName) throws Exception {
        lock.readLock().lock();
        try {
            FEntry entry = findEntry(FileName);
            //case to see if null and there already exist a filename
            if (entry == null)
            throw new Exception("ERROR : file" + FileName + " does not exist");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int FNodesIndex = entry.FirstBlock;
            int remaining = entry.size;

            while (FNodesIndex != -1 && remaining > 0) {
                simfs.seek(fnodes[FNodesIndex].BlockIndex * BLOCK_SIZE);
                byte[] block = new byte[BlockIndex];
                simfs.read(block);
                int toRead = Math.min(remaining, BLOCK_SIZE);
                buffer.write(block, 0 , toRead);
                remaining -= toRead;
                FNodesIndex = fnodes[FNodesIndex].NextBlock;
            }
            return buffer.toByteArray();
        }
        finally{
            lock.readLock().unlock();
        }
    }

    //LIST Function
    public String[] listFiles() {
        lock.readLock().lock();
        try {
            return Arrays.stream(entries)
                   .filter(FEntry::isUsed)
                   .map(en -> en.FileName)
                   .toArray(String[]::new);
        }
        finally {
            lock.readLock().lock();
        }
    }


    



}