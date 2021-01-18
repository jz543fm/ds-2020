package storage;

import logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageProvider<T>
{
    private final String SERVICE_NAME = "PersistenceService";
    private final String storageDirectory;
    private final int peerID;
    private final String storageFile;

    public StorageProvider(int peerID, String storageFile)
    {
        this.peerID = peerID;
        this.storageFile = storageFile;

        String generalStorage = System.getenv("HOME") + "/.PoS/";
        createDirectory(generalStorage);
        storageDirectory = generalStorage + this.peerID;
        createDirectory(storageDirectory);

        Logger.getInstance().init(SERVICE_NAME, peerID);
    }

    private void createDirectory(String dir)
    {
        if ( !new File(dir).exists())
            if (! new File(dir).mkdir())
                Logger.getInstance().error(SERVICE_NAME, "Can't create directory " + dir + " for persistent storage");
            else
                Logger.getInstance().info(SERVICE_NAME, "Created directory " + dir + " for persistent storage");
    }


    public void saveRecords(List<T> records)
    {
        Logger.getInstance().info(SERVICE_NAME, peerID,"Saving records to the persistent storage");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(storageDirectory + storageFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(records);
            Logger.getInstance().info(SERVICE_NAME, peerID,"Successfully saved " + records.size() + " records from " + storageFile);

            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException err) {

            err.printStackTrace();
        }
    }


    public List<T> fetchRecords()
    {
        Logger.getInstance().info(SERVICE_NAME, peerID,"Restoring blocks from the backup file" + storageDirectory + storageFile);

        try {
            FileInputStream fileInputStream = new FileInputStream(storageDirectory + storageFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<T> records = (ArrayList<T>) objectInputStream.readObject();
            Logger.getInstance().info(SERVICE_NAME, peerID, "Successfully restored " + records.size() + " records from " + storageFile);

            objectInputStream.close();
            fileInputStream.close();

            return records;

        } catch (IOException | ClassNotFoundException err) {

            Logger.getInstance().error(SERVICE_NAME, peerID,"Unable to restore records");
            return null;
        }
    }
}
