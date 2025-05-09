package com.fkbinho.dscatalog.services;

import com.fkbinho.dscatalog.dto.ProductDTO;
import com.fkbinho.dscatalog.entities.Category;
import com.fkbinho.dscatalog.entities.Product;
import com.fkbinho.dscatalog.repositories.CategoryRepository;
import com.fkbinho.dscatalog.repositories.ProductRepository;
import com.fkbinho.dscatalog.services.exceptions.DatabaseException;
import com.fkbinho.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fkbinho.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith({SpringExtension.class})
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));

        // Mock the behavior of the repository to return a product
        // when getReferenceById is called with an existing ID
        Mockito.when(repository.getReferenceById((existingId))).thenReturn(product);

        // Mock the behavior of the repository to throw EntityNotFoundException
        // when getReferenceById is called with a non-existing ID
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        // Mock the behavior of the category repository to return a category
        // when getReferenceById is called with an existing ID
        Mockito.when(categoryRepository.getReferenceById((existingId))).thenReturn(Factory.createCategory());

        // Mock the behavior of the category repository to throw EntityNotFoundException
        // when getReferenceById is called with a non-existing ID
        Mockito.when(categoryRepository.getReferenceById((nonExistingId))).thenThrow(EntityNotFoundException.class);

        // Mock the behavior of the repository to return a list of products
        // when findAll is called with any Pageable
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        // Mock the behavior of the repository to return a product
        // when save is called
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        // Mock the behavior of the repository to return a product
        // when findById is called with an existing ID
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        // Mock the behavior of the repository to return an empty Optional
        // when findById is called with a non-existing ID
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Mock the behavior of the repository to do nothing
        // when deleteById is called
        Mockito.doNothing().when(repository).deleteById(existingId);

        // Mock the behavior of the repository to throw DataIntegrityViolationException
        // when deleteById is called with a dependent ID
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        // Mock the behavior of the repository to return true
        // when checking for an existing ID
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        // Mock the behavior of the repository to return false
        // when checking for a non-existing ID
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        // Mock the behavior of the repository to return true
        // when checking for a dependent ID
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(nonExistingId, productDTO)
        );
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(nonExistingId)
        );
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(
                DatabaseException.class,
                () -> service.delete(dependentId)
        );
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(nonExistingId)
        );
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(
                () -> service.delete(existingId)
        );

        // Verify that the repository's deleteById method was called with the correct ID
        Mockito.verify(repository).deleteById(existingId);
    }
}
