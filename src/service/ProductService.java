package service;

import adapter.DatabaseStorage;
import domain.EntityInterface;
import domain.Product;

import java.util.ArrayList;

public class ProductService extends BaseService {

    public ProductService() {
        super(new DatabaseStorage<>(Product.class));
    }

    public ArrayList<Product> getAll() {
        ArrayList<EntityInterface> entities = armazenamento.listAll();
        ArrayList<Product> products = new ArrayList<>();
        for (EntityInterface e : entities) {
            products.add((Product) e);
        }
        return products;
    }
}
