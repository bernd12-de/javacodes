package finances.parser;

import finances.model.Category;
import finances.model.Transaction;
import finances.util.CategoryMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.math.BigDecimal;

/**
 * Simple CSV parser that expects a semicolon separated file with German date format.
 */
public class CsvTransactionParser implements TransactionParser {
    @Override
    public List<Transaction> parse(Path file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return transactions;
            }
            headerLine = headerLine.replace("\uFEFF", ""); // remove BOM
            String[] headers = headerLine.split(";");
            Map<String, Integer> index = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                index.put(headers[i].trim(), i);
            }
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                int dateIdx = index.getOrDefault("Buchungstag", -1);
                LocalDate date = parseDate(parts, dateIdx, formatter);
                String text = get(parts, index.getOrDefault("Buchungstext", -1));
                if (text == null) {
                    text = get(parts, index.getOrDefault("Verwendungszweck", -1));
                }
                int amountIdx = index.getOrDefault("Betrag", -1);
                if (amountIdx < 0 || amountIdx >= parts.length) continue;
                BigDecimal amount = parseAmount(parts[amountIdx]);
                String account = get(parts, index.getOrDefault("IBAN Kontoinhaber", index.getOrDefault("IBAN Auftragskonto", -1)));
                Category category = parseCategory(get(parts, index.getOrDefault("Kategorie", -1)));
                if (category == Category.SONSTIGES) {
                    category = CategoryMapper.fromDescription(text);
                }
                transactions.add(new Transaction(date, text, amount, category, account));
            }
        }
        return transactions;
    }

    private static Category parseCategory(String name) {
        if (name == null || name.isBlank()) {
            return Category.SONSTIGES;
        }
        try {
            return Category.valueOf(name.replace(' ', '_').replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Category.SONSTIGES;
        }
    }

    private static String get(String[] parts, int idx) {
        if (idx >= 0 && idx < parts.length) {
            return parts[idx];
        }
        return null;
    }

    private static LocalDate parseDate(String[] parts, int idx, DateTimeFormatter formatter) {
        if (idx < 0 || idx >= parts.length) {
            return LocalDate.now();
        }
        String val = parts[idx];
        try {
            return LocalDate.parse(val, formatter);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private static BigDecimal parseAmount(String value) {
        String v = value.replace(".", "").replace(',', '.');
        return new BigDecimal(v);
    }
}
