package service;

public class ProductService extends BaseService {
    protected PersistInterface armazenamento = new DatabaseStorage<>(Product.class);>