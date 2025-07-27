package finances.util;

import finances.model.Category;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CategoryMapper {
    private static final Map<Pattern, Category> MAP = new LinkedHashMap<>();

    static {
        MAP.put(Pattern.compile("miete", Pattern.CASE_INSENSITIVE), Category.MIETKOSTEN);
        MAP.put(Pattern.compile("(rewe|aldi|edeka|lidl|kaufland)", Pattern.CASE_INSENSITIVE), Category.LEBENSMITTEL);
        MAP.put(Pattern.compile("strom|energie", Pattern.CASE_INSENSITIVE), Category.STROM);
        MAP.put(Pattern.compile("internet|telefon", Pattern.CASE_INSENSITIVE), Category.INTERNET);
        MAP.put(Pattern.compile("versicherung", Pattern.CASE_INSENSITIVE), Category.VERSICHERUNG);
        MAP.put(Pattern.compile("kredit", Pattern.CASE_INSENSITIVE), Category.KREDITE);
        MAP.put(Pattern.compile("sparen|sparkonto", Pattern.CASE_INSENSITIVE), Category.SPAREN);
        MAP.put(Pattern.compile("(h&m|primark|zalando|shopping|amazon)", Pattern.CASE_INSENSITIVE), Category.SHOPPING);
        MAP.put(Pattern.compile("bahn|verkehr|tank", Pattern.CASE_INSENSITIVE), Category.VERKEHR);
    }

    public static Category fromDescription(String description) {
        if (description == null) {
            return Category.SONSTIGES;
        }
        for (Map.Entry<Pattern, Category> e : MAP.entrySet()) {
            if (e.getKey().matcher(description).find()) {
                return e.getValue();
            }
        }
        return Category.SONSTIGES;
    }
}
