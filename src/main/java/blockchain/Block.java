package blockchain;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Block<T> implements Serializable
{
    private final Date creationDate;

    private int ID;
    private List<T> transactions;
    private byte[] previousHash;
    private byte[] currentHash;

    public Block(Date creationDate, int ID, List<T> transactions, byte[] previousHash) {
        this.creationDate = creationDate;
        this.ID = ID;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.currentHash = new byte[0];
    }

    public Block(int ID, List<T> transactions, byte[] previousHash)
    {
        this.ID = ID;
        this.transactions = transactions;
        this.creationDate = new Date(System.currentTimeMillis());
        this.previousHash = previousHash;
    }

    @Override
    public String toString() {
        return ("[ ID: " + ID + ", previousHash: " + DigestUtils.sha256Hex(previousHash) + ", currentHash: " + DigestUtils.sha256Hex(currentHash) + " ]");
    }

    public byte[] getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(byte[] previousHash) {
        this.previousHash = previousHash;
    }

    public byte[] getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(byte[] currentHash) {
        this.currentHash = currentHash;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<T> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<T> transactions) {
        this.transactions = transactions;
    }
}
