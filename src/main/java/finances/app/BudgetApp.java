package finances.app;

import finances.model.Transaction;
import finances.service.BudgetAnalyzer;
import finances.service.TransactionService;
import finances.util.ChartGenerator;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BudgetApp {

    public static void main(String[] args) {
        Path data = Path.of("Ressourcen/Demodaten");
        Path storeFile = Path.of("transactions.ser");
        TransactionService service = new TransactionService(data, storeFile);
        List<Transaction> transactions = service.loadAll();
        BudgetAnalyzer analyzer = new BudgetAnalyzer();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Jahr-Monat eingeben (z.B. 2023-12): ");
        String input = scanner.nextLine();
        YearMonth month = YearMonth.parse(input);

        Map<YearMonth, BigDecimal> saldoMap = analyzer.monthlySaldo(transactions);
        System.out.println("Saldo pro Monat:");
        ChartGenerator.printBarChart(saldoMap);

        Map<finances.model.Category, BigDecimal> financesByCategory = analyzer.sumByCategory(transactions, month);
        System.out.println("Ausgaben nach Kategorien f\u00fcr " + month + ":");
        ChartGenerator.printBarChart(financesByCategory);

        BigDecimal income = analyzer.totalIncome(transactions, month);
        BigDecimal expense = analyzer.totalExpense(transactions, month);
        System.out.printf("Einnahmen: %.2f EUR%n", income);
        System.out.printf("Ausgaben: %.2f EUR%n", expense);
        System.out.printf("Saldo: %.2f EUR%n", income.add(expense));
    }
}
