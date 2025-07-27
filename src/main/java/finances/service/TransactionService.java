package finances.service;

import finances.model.Transaction;
import finances.parser.CsvTransactionParser;
import finances.parser.PdfTransactionParser;
import finances.parser.TransactionParser;
import finances.storage.DataStore;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private final Path dataDir;
    private final DataStore store;

    public TransactionService(Path dataDir, Path storeFile) {
        this.dataDir = dataDir;
        this.store = new DataStore(storeFile);
    }

    public List<Transaction> loadAll() {
        List<Transaction> list = store.load();
        if (!list.isEmpty()) {
            return list;
        }
        List<Transaction> result = new ArrayList<>();
        TransactionParser csvParser = new CsvTransactionParser();
        TransactionParser pdfParser = new PdfTransactionParser();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir)) {
            for (Path p : stream) {
                String name = p.getFileName().toString().toLowerCase();
                try {
                    if (name.endsWith(".csv")) {
                        result.addAll(csvParser.parse(p));
                    } else if (name.endsWith(".pdf")) {
                        result.addAll(pdfParser.parse(p));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        store.save(result);
        return result;
    }
}
