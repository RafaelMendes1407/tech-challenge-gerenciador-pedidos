package com.techchallenge.ordermanager.infrastructure.gateways;

import com.techchallenge.ordermanager.application.gateways.IProductGateway;
import com.techchallenge.ordermanager.domain.entities.Product;
import com.techchallenge.ordermanager.domain.entities.valueobjects.Money;
import com.techchallenge.ordermanager.domain.entities.valueobjects.ProductCategory;
import com.techchallenge.ordermanager.domain.entities.valueobjects.ProductId;
import com.techchallenge.ordermanager.domain.entities.valueobjects.Image;
import com.techchallenge.ordermanager.infrastructure.persistence.data.ProductData;
import com.techchallenge.ordermanager.infrastructure.persistence.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JpaProductGateway implements IProductGateway {
    private final ProductRepository productRepository;

    public JpaProductGateway(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getAll() {
        return productRepository.findAll().stream().map(this::mapToProduct).toList();
    }

    private Product mapToProduct(ProductData productData) {
        return  Product.ProductBuilder.aProduct()
                .withId(new ProductId(productData.getId()))
                .withName(productData.getName())
                .withCategory(productData.getCategory())
                .withPrice(new Money(productData.getPrice()))
                .withDescription(productData.getDescription())
                .withImages(productData.getImages().stream().map(Image::new).toList())
                .build();
    }

    public ProductId add(Product product) {
        UUID id = UUID.randomUUID();
        ProductData productData = ProductData
                .builder()
                .id(id)
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice().getAmount())
                .images(product.getImages().stream().map(Image::toString).toList())
                .build();
        productRepository.save(productData);

        return new ProductId(id);
    }

    @Override
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .map(this::mapToProduct)
                .orElse(null);
    }

    @Override
    public List<Product> getByCategory(ProductCategory category) {
        List<Product> listProducts = new ArrayList<>();
        this.productRepository.findByCategory(category)
                .forEach(e -> listProducts.add(this.mapToProduct(e)));
        return listProducts;
    }

    @Override
    public void update(Product product) {
        ProductData productData = ProductData
                .builder()
                .id(product.getId().getValue())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice().getAmount())
                .images(product.getImages().stream().map(Image::toString).toList())
                .build();
        productRepository.save(productData);    }

    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

}