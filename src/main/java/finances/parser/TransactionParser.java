package finances.parser;

import finances.model.Transaction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TransactionParser {
    List<Transaction> parse(Path file) throws IOException;
}
