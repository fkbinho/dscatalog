package com.fkbinho.dscatalog.services;

import com.fkbinho.dscatalog.repositories.ProductRepository;
import com.fkbinho.dscatalog.services.exceptions.DatabaseException;
import com.fkbinho.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        // Mock the behavior of the repository to do nothing when deleteById is called
        Mockito.doNothing().when(repository).deleteById(existingId);

        // Mock the behavior of the repository to throw DataIntegrityViolationException
        // when deleteById is called with a dependent ID
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        // Mock the behavior of the repository to return true when checking for an existing ID
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        // Mock the behavior of the repository to return false when checking for a non-existing ID
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        // Mock the behavior of the repository to return true when checking for a dependent ID
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

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
