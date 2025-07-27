package finances.app;

import finances.model.Category;
import finances.model.Transaction;
import finances.service.BudgetAnalyzer;
import finances.service.TransactionService;
import finances.util.ChartGenerator;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BudgetUi {
    private final TransactionService service;
    private final BudgetAnalyzer analyzer = new BudgetAnalyzer();
    private final JFrame frame = new JFrame("Budget Overview");
    private final JComboBox<YearMonth> monthBox = new JComboBox<>();
    private final JTextArea output = new JTextArea(20, 50);

    public BudgetUi(Path dataDir, Path storage) {
        this.service = new TransactionService(dataDir, storage);
    }

    private void init() {
        List<Transaction> transactions = service.loadAll();
        Set<YearMonth> months = new TreeSet<>();
        for (Transaction t : transactions) {
            months.add(YearMonth.from(t.getDate()));
        }
        for (YearMonth ym : months) {
            monthBox.addItem(ym);
        }
        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(e -> analyze(transactions));
        JPanel top = new JPanel();
        top.add(new JLabel("Month:"));
        top.add(monthBox);
        top.add(analyzeButton);
        output.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(output), BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void analyze(List<Transaction> all) {
        YearMonth month = (YearMonth) monthBox.getSelectedItem();
        StringBuilder sb = new StringBuilder();
        Map<Category, BigDecimal> byCat = analyzer.sumByCategory(all, month);
        sb.append("Ausgaben nach Kategorien\n");
        sb.append(ChartGenerator.generateBarChart(byCat));
        BigDecimal income = analyzer.totalIncome(all, month);
        BigDecimal expense = analyzer.totalExpense(all, month);
        sb.append(String.format("Einnahmen: %.2f EUR%n", income));
        sb.append(String.format("Ausgaben: %.2f EUR%n", expense));
        sb.append(String.format("Saldo: %.2f EUR%n", income.add(expense)));
        output.setText(sb.toString());
    }

    public void show() {
        init();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Path data = Path.of("Ressourcen/Demodaten");
        Path storeFile = Path.of("transactions.ser");
        new BudgetUi(data, storeFile).show();
    }
}
