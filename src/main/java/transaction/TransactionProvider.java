package transaction;

import logger.Logger;
import storage.StorageProvider;

import java.util.ArrayList;
import java.util.List;

public class TransactionProvider implements TransactionService<Transaction, TransactionData>
{
    private final int peerID;

    private List<Transaction> thresholdTransactions = new ArrayList<>();
    private List<Transaction> allTransactions = new ArrayList<>();
    private StorageProvider<Transaction> transactionStorage;
    private StorageProvider<Transaction> thresholdStorage;

    public TransactionProvider(int peerID)
    {
        this.peerID = peerID;
        this.transactionStorage = new StorageProvider<>(peerID, "/transactions");
        this.thresholdStorage = new StorageProvider<>(peerID, "/threshold");

        restoreTransactions();

        Logger.getInstance().init(SERVICE_NAME, peerID);
    }

    private void restoreTransactions()
    {
        List<Transaction> transactions = this.transactionStorage.fetchRecords();
        List<Transaction> thresholdTransactions = this.thresholdStorage.fetchRecords();

        if (transactions != null) {
            this.allTransactions = transactions;

            Logger.getInstance().info(SERVICE_NAME, peerID, allTransactions.size() + " transactions restored");
        }

        if (thresholdTransactions != null) {
            this.thresholdTransactions = thresholdTransactions;

            Logger.getInstance().info(SERVICE_NAME, peerID, allTransactions.size() + " threshold transactions restored");
        }
    }

    @Override
    public synchronized void clearTransactions()
    {
        Logger.getInstance().info(SERVICE_NAME, peerID, "Clear threshold transactions");
        thresholdTransactions.clear();
    }

    @Override
    public Transaction getFirstGeneratedTransaction()
    {
        Transaction firstTransaction = thresholdTransactions.get(0);

        for ( Transaction transaction : thresholdTransactions) {
            if (transaction.getCreationDate().getTime() < firstTransaction.getCreationDate().getTime())
                firstTransaction = transaction;
        }

        return firstTransaction;
    }

    @Override
    public synchronized void updateTransactions(List<Transaction> transactions)
    {
        if (transactions.size() > allTransactions.size()) {
            this.allTransactions = transactions;
            this.transactionStorage.saveRecords(allTransactions);

            Logger.getInstance().info(SERVICE_NAME, peerID, "Transactions updated");
        } else {

            Logger.getInstance().info(SERVICE_NAME, peerID, "Transactions already update");
        }
    }

    @Override
    public List<Transaction> getThresholdTransactions() {
        return thresholdTransactions;
    }

    @Override
    public Transaction generateTransaction(int peerID, TransactionData transactionData)
    {
        if ( !transactionData.validate()) {
            Logger.getInstance().error(SERVICE_NAME, "Can't generate transaction. Invalid transaction data");
            return null;
        }

        Transaction transaction = new Transaction<>(peerID, transactionData);
        Logger.getInstance().info(SERVICE_NAME, this.peerID, "Generate new transaction: " + transaction.toString());
        System.out.print(getTransactions().size());
        return transaction;
    }

    @Override
    public synchronized boolean storeTransaction(Transaction transaction)
    {
        if (allTransactions.contains(transaction) || thresholdTransactions.contains(transaction)) {
            Logger.getInstance().info(SERVICE_NAME, peerID, "Already received transaction");
            return false;
        }

        Logger.getInstance().info(SERVICE_NAME, peerID,"Storing received transaction: " + transaction.toString());

        allTransactions.add(transaction);
        thresholdTransactions.add(transaction);

        this.transactionStorage.saveRecords(allTransactions);
        this.thresholdStorage.saveRecords(thresholdTransactions);

        return true;
    }

    @Override
    public List<Transaction> getTransactions() {
        return allTransactions;
    }
}
