package service;

public class PriceService extends BaseService {
    protected PersistInterface armazenamento = new DatabaseStorage<>(Price.class);
}
