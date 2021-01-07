package blockchain;

import org.apache.commons.codec.digest.DigestUtils;
import transaction.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BlockProvider implements BlockService<Block, Transaction> {

    private int peerId;
    private List<Block> blocks = new ArrayList<>();


    public BlockProvider(int peerId){
        this.peerId = peerId;

    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Block createBlock(List<Transaction> transactions) {
        return null;
    }

    @Override
    public byte[] generateHash(Block block) {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(block.getTransactions());

            return DigestUtils.sha256(byteArrayOutputStream.toByteArray());

        } catch (IOException err) {

            err.printStackTrace();
            return null;

        }
    }

    @Override
    public synchronized boolean storeBlock(Block block) {

        return false;
    }

    @Override
    public boolean verifyBlock(Block block) {
        return false;
    }

    @Override
    public synchronized void updateBlocks(List<Block> blocks)
    {
        if (blocks.size() > this.blocks.size()) {
            this.blocks = blocks;
        }
    }

}
