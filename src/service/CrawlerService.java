package service;

import adapter.PriceFetcherInterface;
import domain.Product;
import domain.ProductLink;

import java.util.ArrayList;

public class CrawlerService {
    private final ProductService productService;
    private final PriceFetcherInterface priceFetcher;

    public CrawlerService(ProductService productService, PriceFetcherInterface priceFetcher) {
        this.productService = productService;
        this.priceFetcher = priceFetcher;
    }

    public void run() {
        ArrayList<Product> products = productService.getAll();

        for (Product product : products) {
            IO.println("Verificando: " + product.getName());
            processProduct(product);
        }
    }

    private void processProduct(Product product) {
        Float menorPreco = null;
        String menorLoja = null;

        for (ProductLink link : product.getLinks()) {
            IO.println("  Buscando em " + link.getStore() + "...");
            Float preco = priceFetcher.fetch(link.getUrl());

            if (preco == null) {
                IO.println("  Nao encontrado.");
                continue;
            }

            IO.println("  " + link.getStore() + ": R$ " + preco);

            if (menorPreco == null || preco < menorPreco) {
                menorPreco = preco;
                menorLoja = link.getStore();
            }
        }

        if (menorPreco != null) {
            IO.println("  Menor preco: R$ " + menorPreco + " na " + menorLoja);
            product.setPrice(menorPreco, menorLoja);
            productService.edit(product);
        }
    }
}
