package blockchain;

import java.rmi.Remote;
import java.util.List;


public interface BlockService<T,D> extends Remote
{
    String SERVICE_NAME = "BlockService";

    List<T> getBlocks();
    T createBlock(List<D> transactions);
    byte[] generateHash(T block);
    boolean storeBlock(T block);
    boolean verifyBlock(T block);
    void updateBlocks(List<T> blocks);
}

