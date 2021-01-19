package client;

import blockchain.Block;
import blockchain.BlockProvider;
import logger.Logger;
import peer.Peer;
import peer.PeerF;
import peer.PeerServer;
import peer.PeerService;
import transaction.Transaction;
import transaction.TransactionData;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

public class Scenario
{
    public static void main(String[] args) throws UnknownHostException, RemoteException, java.net.UnknownHostException {
        System.out.print("BBBBBB       LL           OOOOOOOO     CCCCCCCCCC   KK        KK  \n");
        System.out.print("BB    BB     LL         OO        OO   CC           KK      KK    \n");
        System.out.print("BB    BB     LL         OO        OO   CC           KK    KK        CCCC  H   H   AAAAA  I  N      N     \n");
        System.out.print("BB   BB      LL         OO        OO   CC           KK  KK         C      H   H   A   A  I  N N    N     \n");
        System.out.print("BB BB        LL         OO        OO   CC           KKKK           C      HHHHH   AAAAA  I  N  N   N     \n");
        System.out.print("BB    BB     LL         OO        OO   CC           KK  KK         C      H   H   A   A  I  N    N N     \n");
        System.out.print("BB    BB     LL         OO        OO   CC           KK    KK        CCCC  H   H   A   A  I  N      N     \n");
        System.out.print("BB   BB      LL         OO        OO   CC           KK      KK    \n");
        System.out.print("BBBBBB       LLLLLLLLLL   OOOOOOOO     CCCCCCCCCCC  KK        KK  \n");



        System.out.print("\n");
        Logger.getInstance().info("Main method","Starting Blockchain\n");
        PeerService<Block, Transaction, TransactionData> peerNode1 = PeerF.createService(null);
        Logger.getInstance().info("PeerService","Creating first node.\n");
        Peer peer1 = PeerF.getLatestPeer();
        PeerService<Block, Transaction, TransactionData> peerNode2 = PeerF.createService(peer1);
        Peer peer2 = PeerF.getLatestPeer();
        PeerService<Block, Transaction, TransactionData> peerNode3 = PeerF.createService(peer1);
        Peer peer3 = PeerF.getLatestPeer();
        //PeerService<Block, Transaction, TransactionData> peerNode4 = PeerF.createService(peer1);
        //Peer peer4 = PeerF.getLatestPeer();
        //PeerService<Block, Transaction, TransactionData> peerNode5 = PeerF.createService(peer1);
        ///Peer peer5 = PeerF.getLatestPeer();


        peerNode1.connect(peer2);
        peerNode1.connect(peer3);
        //peerNode1.connect(peer4);
        //peerNode1.connect(peer5);
        ClientServer.run(peerNode1);

    }

}

