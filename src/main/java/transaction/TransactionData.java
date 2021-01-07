package transaction;

import java.io.Serializable;

public class TransactionData implements Serializable
{
    private String sender;
    private String receiver;
    private int amount;

    public TransactionData(String sender, String receiver, int amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return ("[ Sender: " + sender + ", Receiver: " + receiver + ", Amount: " + amount + " ]");
    }

    public boolean validate() {
        return ( sender != null && receiver != null && amount != 0) ;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
