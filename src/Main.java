import adapter.JsoupPriceFetcher;
import domain.Product;
import domain.ProductLink;
import service.CrawlerService;
import service.ProductService;

void main() {
    ProductService productService = new ProductService();

    if (productService.getAll().isEmpty()) {
        Product ps5 = new Product("PS5-001", "PlayStation 5", null);
        ps5.addLink(new ProductLink("Kabum",      "https://www.kabum.com.br/produto/989702"));
        ps5.addLink(new ProductLink("Amazon",     "https://a.co/d/0bqiXiV9"));
        ps5.addLink(new ProductLink("Americanas", "https://www.americanas.com.br/playstation-5-edicao-digital-825gb-1-controle-branco-sony-com-2-jogos-8299907/p"));
        productService.create(ps5);

        Product rtx = new Product("GPU-5090", "NVIDIA RTX 5090", null);
        rtx.addLink(new ProductLink("Kabum",  "https://www.kabum.com.br/produto/704641"));
        rtx.addLink(new ProductLink("Amazon", "https://a.co/d/0egSEqWN"));
        productService.create(rtx);
    }

    CrawlerService crawler = new CrawlerService(productService, new JsoupPriceFetcher());
    crawler.run();

    IO.println("\n=== Produtos apos crawler ===");
    productService.listAll();
}
