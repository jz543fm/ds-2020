package client;

import blockchain.Block;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import peer.Peer;
import peer.PeerService;
import transaction.Transaction;
import transaction.TransactionData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientServer
{

    private static final String SENDER = "sender";
    private static final String RECEIVER = "receiver";
    private static final String AMOUNT = "amount";
    private static final String PORT = "port";


    public static void run(PeerService peerService)
    {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/connect", new ConnectNodeHandler(peerService));
            httpServer.createContext("/blocks", new getBlockchain(peerService));
            httpServer.createContext("/transactions", new getTransactions(peerService));
            httpServer.createContext("/peers", new getPeers(peerService));
            httpServer.createContext("/newTransaction", new generateTransaction(peerService));
            httpServer.createContext("/disconnect", new disconnect(peerService));

            httpServer.setExecutor(null);
            httpServer.start();

        } catch (IOException err )  {
            err.printStackTrace();
        }
    }

    public static class ConnectNodeHandler implements HttpHandler
    {
        private PeerService peerService;

        public ConnectNodeHandler(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

            if (! params.containsKey(PORT))
            {
                response = "Specify nodeID parameter";
                exchange.sendResponseHeaders(422, response.length());
                OutputStream os = exchange.getResponseBody();
                os.close();
            }


            Peer peer = new Peer(Inet4Address.getLocalHost().getHostAddress(), Integer.parseInt(params.get(PORT)));

            if (peerService.connect(peer)) {
                response = "Successfully connected to peer " + peer.toString();
                exchange.sendResponseHeaders(200, response.length());
            } else  {
                response = "Unable to establish connections with peer: " + peer.toString();
                exchange.sendResponseHeaders(405, response.length());
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


    static class getBlockchain implements HttpHandler
    {
        private PeerService peerService;

        public getBlockchain(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";

            List<Block> blocks = peerService.getBlocks();

            for( Block block : blocks) {
                response += block.toString() + '\n';
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


    static class getTransactions implements HttpHandler
    {
        private PeerService peerService;

        public getTransactions(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            List<Transaction> transactions = peerService.getTransactions();

            if (transactions.isEmpty()) {
                response = "No transactions have been executed";
            } else {
                for ( Transaction transaction : transactions)
                    response += transaction.toString() + '\n';
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    public static class generateTransaction implements HttpHandler
    {

        private PeerService<Block, Transaction, TransactionData> peerService;

        public generateTransaction(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

            if (!params.containsKey(ClientServer.SENDER) ||
                    !params.containsKey(ClientServer.RECEIVER) ||
                    !params.containsKey(ClientServer.AMOUNT) )
            {
                response = "Specify all parameters: sender, receiver and amount";
                exchange.sendResponseHeaders(422, response.length());
                OutputStream os = exchange.getResponseBody();
                os.close();
            }

            TransactionData transactionData = new TransactionData(
                    params.get(ClientServer.SENDER),
                    params.get(ClientServer.RECEIVER),
                    Integer.parseInt(params.get(ClientServer.AMOUNT)));

            Transaction transaction = peerService.generateTransaction(transactionData);

            response = "Generate new transaction: " + transaction.toString();

            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class getPeers implements HttpHandler
    {

        private PeerService peerService;

        public getPeers(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            List<Peer> peers = peerService.allPeers();

            if (peers.isEmpty()) {
                response = "No connections to any peers";
            } else {
                for ( Peer peer : peers)
                    response += peer.toString();
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class disconnect implements HttpHandler
    {
        private PeerService peerService;

        public disconnect(PeerService peerService) {
            this.peerService = peerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Disconnect";
            peerService.disconnect();

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}

