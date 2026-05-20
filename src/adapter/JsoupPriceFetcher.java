package adapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupPriceFetcher implements PriceFetcherInterface {

    private static final Pattern PRICE_PATTERN =
            Pattern.compile("R\\$\\s*([\\d]{1,3}(?:[.]\\d{3})*,\\d{2})");

    @Override
    public Float fetch(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .referrer("https://www.google.com.br")
                    .followRedirects(true)
                    .timeout(15_000)
                    .get();

            Float price = trySchemaOrg(doc);
            if (price != null) return price;

            price = tryOpenGraph(doc);
            if (price != null) return price;

            return tryRegex(doc);

        } catch (Exception e) {
            System.out.println("Erro ao buscar preco em: " + url + " -> " + e.getMessage());
            return null;
        }
    }

    private Float trySchemaOrg(Document doc) {
        for (Element el : doc.select("[itemprop='price']")) {
            String content = el.attr("content");
            if (!content.isBlank()) {
                Float price = parseDecimal(content);
                if (price != null && price > 1) return price;
            }
            Float price = parseBrl(el.text());
            if (price != null && price > 1) return price;
        }
        return null;
    }

    private Float tryOpenGraph(Document doc) {
        for (String selector : new String[]{
                "meta[property='product:price:amount']",
                "meta[name='price']",
                "meta[property='og:price:amount']"
        }) {
            Element el = doc.selectFirst(selector);
            if (el != null) {
                Float price = parseDecimal(el.attr("content"));
                if (price != null && price > 1) return price;
            }
        }
        return null;
    }

    private Float tryRegex(Document doc) {
        Matcher matcher = PRICE_PATTERN.matcher(doc.text());
        while (matcher.find()) {
            Float price = parseBrl(matcher.group(1));
            if (price != null && price > 10) return price;
        }
        return null;
    }

    private Float parseDecimal(String raw) {
        try {
            return Float.parseFloat(raw.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseBrl(String raw) {
        try {
            String cleaned = raw.trim().replace(".", "").replace(",", ".");
            return Float.parseFloat(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
