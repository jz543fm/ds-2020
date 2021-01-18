package transaction;

import java.io.Serializable;
import java.util.Date;

public class Transaction<T> implements Serializable {
    private final Date creationDate;
    private final T transactionData;
    private final int peerID;

    public Transaction(int peerID, T transactionData) {
        this.transactionData = transactionData;
        this.creationDate = new Date(System.currentTimeMillis());
        this.peerID = peerID;
    }

    @Override
    public String toString() {
        return ("[ -> Creator of transaction: " + peerID + ", Timestamp: " + creationDate.toString() + ", Data: " + transactionData.toString() + " ]" );
    }

    public T getTransactionData() {
        return transactionData;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getPeerID() {
        return peerID;
    }
}