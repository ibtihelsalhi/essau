package com.prestify.services;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CategorieService
 */
public class CategorieServiceImpl implements ICategorieService {
    
    private ICategorieRepository categorieRepository;

    public CategorieServiceImpl(ICategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public Categorie addCategorie(Categorie categorie) {
        if (categorie == null) {
            throw new IllegalArgumentException("Categorie cannot be null");
        }
        if (categorie.getNom() == null || categorie.getNom().isEmpty()) {
            throw new IllegalArgumentException("Categorie nom cannot be empty");
        }
        return categorieRepository.save(categorie);
    }

    @Override
    public Categorie getCategorieById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorie not found with id: " + id));
    }

    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public void deleteCategorie(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!categorieRepository.existsById(id)) {
            throw new RuntimeException("Categorie not found with id: " + id);
        }
        categorieRepository.deleteById(id);
    }

    @Override
    public Categorie updateCategorie(Long id, Categorie categorie) {
        if (id == null || categorie == null) {
            throw new IllegalArgumentException("ID and Categorie cannot be null");
        }
        
        Categorie existing = getCategorieById(id);
        existing.setNom(categorie.getNom());
        existing.setDescription(categorie.getDescription());
        
        return categorieRepository.save(existing);
    }

    @Override
    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    @Override
    public boolean existsByNom(String nom) {
        if (nom == null || nom.isEmpty()) {
            return false;
        }
        return categorieRepository.existsByNom(nom);
    }
}
