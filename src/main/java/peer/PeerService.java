package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PeerService<B, T, D> extends Remote {

    String SERVICE_NAME = "PeerService";

    boolean connect(Peer peer) throws RemoteException;
    List<B> getBlocks() throws RemoteException;
    List<T> getTransactions() throws RemoteException;
    T generateTransaction(D data) throws RemoteException;
    List<Peer> allPeers() throws RemoteException;
    void disconnect() throws RemoteException;
    void addTransaction(T transaction) throws RemoteException;
    void addBlock(B block) throws RemoteException;


}
