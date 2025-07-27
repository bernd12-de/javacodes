package finances.parser;

import finances.model.Category;
import finances.model.Transaction;
import finances.util.CategoryMapper;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Placeholder for a PDF parser implementation.
 * Parsing PDF statements would require an additional library (e.g. Apache PDFBox).
 */
public class PdfTransactionParser implements TransactionParser {
    private static final Pattern LINE_PATTERN =
            Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2,4}).*?([+-]?\\d+[,.]\\d{2})");

    @Override
    public List<Transaction> parse(Path file) throws IOException {
        // Without external libraries we can only do a naive text extraction.
        String text = Files.readString(file, StandardCharsets.ISO_8859_1);
        List<Transaction> result = new ArrayList<>();
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd.MM.yy"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        };
        for (String line : text.split("\\R")) {
            Matcher m = LINE_PATTERN.matcher(line);
            if (!m.find()) continue;
            LocalDate date = parseDate(m.group(1), formatters);
            java.math.BigDecimal amount = parseAmount(m.group(2));
            String desc = line.trim();
            Category cat = CategoryMapper.fromDescription(desc);
            result.add(new Transaction(date, desc, amount, cat, file.getFileName().toString()));
        }
        return result;
    }

    private static LocalDate parseDate(String val, DateTimeFormatter[] fmt) {
        for (DateTimeFormatter f : fmt) {
            try {
                return LocalDate.parse(val, f);
            } catch (DateTimeParseException ignored) {
            }
        }
        return LocalDate.now();
    }

    private static java.math.BigDecimal parseAmount(String value) {
        String v = value.replace(".", "").replace(',', '.');
        return new java.math.BigDecimal(v);
    }
}
