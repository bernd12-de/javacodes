package finances.storage;

import finances.model.Transaction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private final Path storageFile;

    public DataStore(Path storageFile) {
        this.storageFile = storageFile;
    }

    public List<Transaction> load() {
        if (!Files.exists(storageFile)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(storageFile))) {
            Object obj = in.readObject();
            if (obj instanceof List<?> list) {
                List<Transaction> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof Transaction t) {
                        result.add(t);
                    }
                }
                return result;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void save(List<Transaction> transactions) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(storageFile))) {
            out.writeObject(new ArrayList<>(transactions));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
