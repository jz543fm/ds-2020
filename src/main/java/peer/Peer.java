package peer;

import java.io.Serializable;

public class Peer implements Serializable {

    private String INET_ADDRESS;
    private int port;

    public Peer(String INET_ADDRESS, int port) {
        this.INET_ADDRESS = INET_ADDRESS;
        this.port = port;
    }

    public String getINET_ADDRESS() {
        return INET_ADDRESS;
    }

    public void setINET_ADDRESS(String INET_ADDRESS) {
        this.INET_ADDRESS = INET_ADDRESS;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return ("[ " + INET_ADDRESS + ":" + port  +" ]");
    }

}
