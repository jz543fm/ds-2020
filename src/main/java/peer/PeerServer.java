package peer;


import blockchain.Block;
import blockchain.BlockProvider;
import blockchain.BlockService;
import logger.Logger;
import transaction.Transaction;
import transaction.TransactionData;
import transaction.TransactionProvider;
import transaction.TransactionService;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class PeerServer implements PeerService<Block, Transaction, TransactionData>
{
    private final List<Peer> connectedPeers = new ArrayList<>();
    private final List<PeerService<Block, Transaction, TransactionData>> peersInstances = new ArrayList<>();

    private TransactionService<Transaction, TransactionData> transactionService;
    private BlockService<Block, Transaction> blockService;
    private Registry registry;
    private int port;
    private int peerID;


    public PeerServer(int peerID, int port, Peer peer)
    {
        this.peerID = peerID;
        this.transactionService = new TransactionProvider(peerID);
        this.blockService = new BlockProvider(peerID);
        this.port = port;

        exportService();

        if (peer != null) connect(peer);
    }

    private void exportService()
    {
        try {
            this.registry = LocateRegistry.createRegistry(port);
            PeerService remote = (PeerService) UnicastRemoteObject.exportObject(this, port);
            this.registry.bind(SERVICE_NAME, remote);

            Logger.getInstance().init(SERVICE_NAME, peerID, port);

        } catch (RemoteException | AlreadyBoundException err ) {

            Logger.getInstance().error(SERVICE_NAME, peerID, "Unable to start service");
            err.printStackTrace();
        }
    }


    @Override
    public boolean connect(Peer peer)
    {
        Logger.getInstance().info(SERVICE_NAME, peerID, "Connecting to peer --> " + peer.toString());

        try {
            PeerService peerService = ((PeerService) LocateRegistry.getRegistry(peer.getINET_ADDRESS(), peer.getPort())
                    .lookup(PeerService.SERVICE_NAME));

            Logger.getInstance().info(SERVICE_NAME, peerID, "Connected ...");
            update(peer, peerService);

            return true;

        } catch (RemoteException | NotBoundException err) {

            Logger.getInstance().error(SERVICE_NAME, peerID, "Unable to connect " + peer.toString());
            err.printStackTrace();
            return false;
        }
    }

    private void update(Peer peer, PeerService<Block, Transaction, TransactionData> peerService) throws RemoteException
    {
        connectedPeers.add(peer);
        peersInstances.add(peerService);
        blockService.updateBlocks(peerService.getBlocks());
        transactionService.updateTransactions(peerService.getTransactions());
    }

    @Override
    public void addTransaction(Transaction transaction)
    {
        Logger.getInstance().info(SERVICE_NAME, peerID, "Receiving new transaction: " + transaction.toString());

        if (! transactionService.storeTransaction(transaction)) return;

        if (transactionService.getThresholdTransactions().size() == TransactionService.THRESHOLD_LIMIT)
        {
            Logger.getInstance().info(SERVICE_NAME, peerID,"Reached maximal transactions");

            if (transactionService.getFirstGeneratedTransaction().getPeerID() == peerID)
            {
                Logger.getInstance().info(SERVICE_NAME, peerID, "Generating new block");

                Block createdBlock = blockService.createBlock(getTransactions());

                peersInstances.forEach( peer -> new Thread( () -> {
                    try {
                        peer.addBlock(createdBlock);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }).start());

            }

            transactionService.clearTransactions();
        }
    }

    @Override
    public void addBlock(Block block)
    {
        Logger.getInstance().info(SERVICE_NAME, peerID, "Receiving new block: " + block.toString());
        blockService.storeBlock(block);
    }

    @Override
    public List<Block> getBlocks() {
        return blockService.getBlocks();
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionService.getTransactions();
    }

    @Override
    public Transaction generateTransaction(TransactionData transactionData)
    {
        Transaction transaction = transactionService.generateTransaction(peerID, transactionData);
        Logger.getInstance().info(SERVICE_NAME, peerID, "Sending broadcast with a transaction to connected peers");

        addTransaction(transaction);

        peersInstances.forEach( Peer -> new Thread(() ->
        {
            try {
                Peer.addTransaction(transaction);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start());

        return transaction;
    }

    @Override
    public List<Peer> allPeers() {
        return connectedPeers;
    }

    @Override
    public void disconnect()
    {
        try {
            this.registry.unbind(SERVICE_NAME);
            UnicastRemoteObject.unexportObject(this, true);

            Logger.getInstance().finish(SERVICE_NAME, peerID, this.port);

        } catch (RemoteException | NotBoundException err) {

            Logger.getInstance().error(SERVICE_NAME, peerID, "Unable to terminate process");
            err.printStackTrace();
        }
    }

}
