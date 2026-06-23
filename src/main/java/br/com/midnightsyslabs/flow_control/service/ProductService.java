package br.com.midnightsyslabs.flow_control.service;

import java.util.List;
import java.util.Comparator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.domain.entity.product.ProductCategory;
import br.com.midnightsyslabs.flow_control.domain.entity.product.ProductPrice;
import br.com.midnightsyslabs.flow_control.repository.product.ProductRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Product saveProduct(
            String name,
            String description,
            ProductCategory category,
            String price,
            String quantity,
            MeasurementUnit measurementUnit) {

        var price_ = UtilsService.solveComma(price);
        var quantity_ = UtilsService.solveComma(quantity);

        var product = new Product();
        var productPrice = new ProductPrice();

        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setMeasurementUnit(measurementUnit);
        product.setCreatedAt(OffsetDateTime.now());
        try {
            product.setQuantity(new BigDecimal(quantity_));
            productPrice.setPrice(new BigDecimal(price_));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com formato do preço ou da quantidade!");
        }
        productPrice.setPriceChangeDate(OffsetDateTime.now());
        productPrice.setProduct(product);

        product.setProductPriceHistory(List.of(productPrice));

        return productRepository.save(product);

    }

    @Transactional
    public void editProduct(
            Product product,
            String name,
            String description,
            String price) {

        var price_ = UtilsService.solveComma(price);

        product.setName(name);
        product.setDescription(description);
        try {

            var newPrice = new BigDecimal(price_);

            var currentPrice = product.getProductPriceHistory()
                    .stream()
                    .max(Comparator.comparing(ProductPrice::getPriceChangeDate))
                    .map(ProductPrice::getPrice)
                    .orElse(null);

            if (currentPrice == null || currentPrice.compareTo(newPrice) != 0) {
                ProductPrice newPriceEntry = new ProductPrice();
                newPriceEntry.setProduct(product);
                newPriceEntry.setPrice(newPrice);
                newPriceEntry.setPriceChangeDate(OffsetDateTime.now());

                product.getProductPriceHistory().add(newPriceEntry);
            }

            productRepository.save(product);

        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com formato do preço!");
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void deleteProduct(Product product) {
        product.setDeletedAt(OffsetDateTime.now());
        productRepository.save(product);
    }

    public List<Product> getProducts(){
        return productRepository.findAll();
    }
    
}
