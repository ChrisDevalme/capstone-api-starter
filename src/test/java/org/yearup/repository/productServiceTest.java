package org.yearup.repository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Product;
import org.yearup.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void search_shouldReturnAllProducts_whenNoFiltersAreProvided() {
        //Arrange
        List<Product> testProducts = new ArrayList<>();

        Product featuredProduct = new Product(
                1,
                "Featured Shirt",
                900.00,
                1,
                "Amiri Shirt",
                "Blue",
                20,
                true,
                null);
        Product nonfeaturedProduct = new Product(
                2,
                "T-Shirt",
                40.00,
                1,
                "Walmart Shirt",
                "red",
                100,
                false,
                null);

        testProducts.add(featuredProduct);
        testProducts.add(nonfeaturedProduct);

        when(productRepository.findAll()).thenReturn(testProducts);
        // Act
        List<Product> products = productService.search(null, null, null, null);

        // Assert
        assertEquals(2, products.size());
    }

    @Test
    void update_whenProductExists_shouldUpdateAllFields()
    {
        // Arrange
        Product hoodie = new Product(
                1,
                "Essential Hoodie",
                49.99,
                1,
                "Black",
                "Oversized cotton hoodie",
                25,
                true,
                "hoodie.jpg"
        );

        Product denimJacket = new Product(
                2,
                "Vintage Denim Jacket",
                79.99,
                2,
                "Blue",
                "Classic relaxed-fit denim jacket",
                40,
                false,
                "denim-jacket.jpg"
        );

        when(productRepository.findById(1))
                .thenReturn(Optional.of(hoodie));

        // Act
        productService.update(1, denimJacket);

        // Assert
        verify(productRepository).save(hoodie);
    }

    }