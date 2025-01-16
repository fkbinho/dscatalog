package com.fkbinho.dscatalog.services;

import com.fkbinho.dscatalog.dto.CategoryDTO;
import com.fkbinho.dscatalog.entities.Category;
import com.fkbinho.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = repository.findAll();
        return list.stream().map(CategoryDTO::new).toList();
    }

    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity =obj.get();
        return new CategoryDTO(entity);
    }
}
