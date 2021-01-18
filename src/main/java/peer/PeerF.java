package peer;



import blockchain.Block;
import transaction.Transaction;
import transaction.TransactionData;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class PeerF {

    private static int ID = 0;
    private static int port = 5001;


    public static Peer getLatestPeer() throws UnknownHostException {
        return new Peer(Inet4Address.getLocalHost().getHostAddress(), port-1);
    }

    public static PeerService<Block, Transaction, TransactionData> createService(Peer peer) {
        return new PeerServer(ID++, port++, peer);
    }


}
