package transaction;

import java.rmi.Remote;
import java.util.List;

public interface  TransactionService<T, D> extends Remote {

    String SERVICE_NAME = "TransactionService";
    int THRESHOLD_LIMIT = 5;

    T generateTransaction(int peerID, D transactionData);
    boolean storeTransaction(T transaction);
    List<T> getTransactions();
    void clearTransactions();
    void updateTransactions(List<T> transactions);
    T getFirstGeneratedTransaction();
    List<T> getThresholdTransactions();

}
