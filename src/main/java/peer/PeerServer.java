package peer;

import blockchain.Block;
import transaction.Transaction;
import transaction.TransactionData;
import transaction.TransactionService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class PeerServer implements PeerService<Block, Transaction, TransactionData> {

    private List<Peer> connectedPeers = new ArrayList<>();
    private List<PeerService<Block, Transaction, TransactionData>> peerInstances = new ArrayList<>();
    private Registry registry = null;
    private TransactionService<Transaction, TransactionData> transactionService;
    private int port;
    private int peerId;

    public PeerServer(int peerId, int port, Peer peer) throws RemoteException {
        this.peerId = peerId;
        this.port = port;

        if(peer != null){
            connect(peer);
        }

    }

    @Override
    public boolean connect(Peer peer) throws RemoteException {
        try{
            PeerService peerService = ((PeerService) LocateRegistry.getRegistry(peer.getINET_ADDRESS(), peer.getPort()).lookup(PeerService.SERVICE_NAME));
            update(peer);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void update(Peer peer) {
        connectedPeers.add(peer);
    }


    @Override
    public List<Block> getBlocks() throws RemoteException {
        return null;
    }

    @Override
    public List<Transaction> getTransactions() throws RemoteException {
        return null;
    }

    @Override
    public Transaction generateTransaction(TransactionData data) throws RemoteException {

        Transaction transaction = transactionService.generateTransaction(peerId, transactionData);
        return null;


    }

    @Override
    public List<Peer> allPeers() throws RemoteException {
        return connectedPeers;
    }

    @Override
    public void disconnect() throws RemoteException {
        try{
            this.registry.unbind(SERVICE_NAME);
            UnicastRemoteObject.unexportObject(this,true);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTransaction(Transaction transaction) throws RemoteException {

    }

    @Override
    public void addBlock(Block block) throws RemoteException {

    }
}
