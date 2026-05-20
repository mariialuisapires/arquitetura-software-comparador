package service;

import adapter.DatabaseStorage;
import domain.Price;

public class PriceService extends BaseService {

    public PriceService() {
        super(new DatabaseStorage<>(Price.class));
    }
}
