package com.fkbinho.dscatalog.services;


import com.fkbinho.dscatalog.dto.ProductDTO;
import com.fkbinho.dscatalog.repositories.ProductRepository;
import com.fkbinho.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Test to verify that the service returns a page of products
        // when the page is 0 and size is 10
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        // Verify that the result is not empty
        Assertions.assertFalse(result.isEmpty());

        // Verify that the number of page is 0
        Assertions.assertEquals(0, result.getNumber());

        // Verify that the size of the page is 10
        Assertions.assertEquals(10, result.getSize());

        // Verify that the total number of products is as expected
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() throws Exception {
        PageRequest pageRequest = PageRequest.of(50, 10);

        // Test to verify that the service returns an empty page
        // when the page does not exist
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        // Verify that the result is empty
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        // Test to verify that the service returns a sorted page of products
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        // Verify that the result is not empty
        Assertions.assertFalse(result.isEmpty());

        // Verify that the first/second/third product in the page is the expected one
        Assertions.assertEquals("Macbook Pro", result.getContent().getFirst().getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() throws Exception {
        service.delete(existingId);

        // Verify that the product was deleted
        assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        // Attempt to delete a product with a non-existing ID
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(nonExistingId)
        );
    }
}
