package domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "price")
public class Price implements EntityInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "uuid", length = 36)
    private UUID uuid;

    @Column(name = "price")
    private Float price;

    @Column(name = "store")
    private String store;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Price() {}

    public Price(Float price, Date date) {
        this.price = price;
        this.date = date;
    }

    public Price(Float price, Date date, String store) {
        this.price = price;
        this.date = date;
        this.store = store;
    }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }
    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    @Override
    public UUID getUUID() { return uuid; }

    @Override
    public String toString() {
        String produto = product != null ? product.getName() : "desconhecido";
        return "{ produto: " + produto + ", preco: " + price + ", loja: " + store + ", data: " + date + " }";
    }
}
