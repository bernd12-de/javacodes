package finances.util;

import java.math.BigDecimal;
import java.util.Map;
import java.math.RoundingMode;

public class ChartGenerator {
    public static void printBarChart(Map<?, BigDecimal> data) {
        System.out.print(generateBarChart(data));
    }

    public static String generateBarChart(Map<?, BigDecimal> data) {
        StringBuilder sb = new StringBuilder();
        BigDecimal max = data.values().stream().map(BigDecimal::abs).max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        for (Map.Entry<?, BigDecimal> e : data.entrySet()) {
            int bars = e.getValue().abs().multiply(BigDecimal.valueOf(20)).divide(max, 0, RoundingMode.HALF_UP).intValue();
            String bar = "#".repeat(Math.max(0, bars));
            sb.append(String.format("%-20s | %s %.2f%n", e.getKey(), bar, e.getValue()));
        }
        return sb.toString();
    }
}
