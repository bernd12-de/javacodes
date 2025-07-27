package finances.service;

import finances.model.Category;
import finances.model.Transaction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public class BudgetAnalyzer {

    public Map<Category, BigDecimal> sumByCategory(List<Transaction> transactions, YearMonth month) {
        Map<Category, BigDecimal> map = new EnumMap<>(Category.class);
        for (Transaction t : transactions) {
            if (!YearMonth.from(t.getDate()).equals(month)) continue;
            map.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }
        return map;
    }

    public BigDecimal totalIncome(List<Transaction> transactions, YearMonth month) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalExpense(List<Transaction> transactions, YearMonth month) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<YearMonth, BigDecimal> monthlySaldo(List<Transaction> transactions) {
        Map<YearMonth, BigDecimal> map = new TreeMap<>();
        for (Transaction t : transactions) {
            YearMonth ym = YearMonth.from(t.getDate());
            map.merge(ym, t.getAmount(), BigDecimal::add);
        }
        return map;
    }
}
