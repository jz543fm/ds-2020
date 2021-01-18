package client;

import blockchain.Block;
import peer.Peer;
import peer.PeerF;
import peer.PeerService;
import transaction.Transaction;
import transaction.TransactionData;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

public class Scenario
{
    public static void main(String[] args) throws UnknownHostException, RemoteException, java.net.UnknownHostException {
        PeerService<Block, Transaction, TransactionData> peerNode1 = PeerF.createService(null);
        Peer peer1 = PeerF.getLatestPeer();
        PeerService<Block, Transaction, TransactionData> peerNode2 = PeerF.createService(peer1);
        Peer peer2 = PeerF.getLatestPeer();
        PeerService<Block, Transaction, TransactionData> peerNode3 = PeerF.createService(peer1);
        Peer peer3 = PeerF.getLatestPeer();

        peerNode1.connect(peer2);
        peerNode1.connect(peer3);

        ClientServer.run(peerNode1);
    }

}

