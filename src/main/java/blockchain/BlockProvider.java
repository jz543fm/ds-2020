package blockchain;

import org.apache.commons.codec.digest.DigestUtils;

import logger.Logger;
import storage.StorageProvider;
import transaction.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BlockProvider implements BlockService<Block, Transaction>
{
    private List<Block> blocks = new ArrayList<>();
    private StorageProvider<Block> blockStorage;
    private int peerID;

    public BlockProvider(int peerID)
    {
        this.peerID = peerID;
        this.blockStorage = new StorageProvider<>(peerID, "/blocks");
        restoreBlocks();

        Logger.getInstance().init(SERVICE_NAME, peerID);
    }

    private void restoreBlocks()
    {
        List<Block> backupBlocks = blockStorage.fetchRecords();

        if (backupBlocks != null) {
            this.blocks = backupBlocks;
        } else {
            this.blocks.add(new Block<Transaction>(new Date(0), 0x00, new ArrayList<>(), new byte[0]));
            Logger.getInstance().info(SERVICE_NAME, peerID, "Creating new blocks");
        }

    }

    @Override
    public synchronized void updateBlocks(List<Block> blocks)
    {
        if (blocks.size() > this.blocks.size()) {
            this.blocks = blocks;
            blockStorage.saveRecords(blocks);
            Logger.getInstance().info(SERVICE_NAME, peerID, "Updating blocks");
        }
    }


    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public synchronized Block createBlock(List<Transaction> transactions)
    {
        Block lastBlock = this.blocks.get(blocks.size() - 1);
        byte[] previousBlockHash = generateHash(lastBlock);
        int lastBlockID = lastBlock.getID();

        Block newBlock = new Block<>(lastBlockID + 1, transactions, previousBlockHash);

        byte[] currentBlockHash = generateHash(newBlock);
        newBlock.setCurrentHash(currentBlockHash);

        Logger.getInstance().info(SERVICE_NAME, peerID, "Created new block: " + newBlock.toString());
        storeBlock(newBlock);

        return newBlock;
    }


    @Override
    public byte[] generateHash(Block block)
    {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(block.getTransactions());

            return DigestUtils.sha256(byteArrayOutputStream.toByteArray());

        } catch (IOException err) {

            Logger.getInstance().error(SERVICE_NAME, peerID, "Can't generate hash for block: " + block.toString());
            err.printStackTrace();
            return null;
        }
    }


    @Override
    public synchronized boolean storeBlock(Block block)
    {
        if (blocks.contains(block) || !verifyBlock(block)) {
            return false;
        }

        Logger.getInstance().info(SERVICE_NAME, peerID, "Storing new block");
        blocks.add(block);
        blockStorage.saveRecords(blocks);

        return true;
    }

    @Override
    public boolean verifyBlock(Block newBlock)
    {
        Logger.getInstance().info(SERVICE_NAME, peerID,"Verify new block");

        int totalBlocks = blocks.size();
        Block currentBlock, previousBlock;

        if (newBlock.getID() != blocks.size()) {
            Logger.getInstance().error(SERVICE_NAME, peerID,"ID verification failed");
            return false;
        }


        for (int blockIndex = 1; blockIndex < totalBlocks; blockIndex++)
        {
            currentBlock = blocks.get(blockIndex);
            previousBlock = blocks.get(blockIndex - 1);

            if (! Arrays.equals(generateHash(previousBlock), currentBlock.getPreviousHash() )) {
                Logger.getInstance().error(SERVICE_NAME, peerID,
                        "Verification of the previous hash block: " + previousBlock.toString() + " failed");
                return false;
            }
        }

        return true;
    }

}
