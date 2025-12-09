package com.prestify.services;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Categorie
 */
public interface ICategorieRepository {
    Categorie save(Categorie categorie);
    Optional<Categorie> findById(Long id);
    List<Categorie> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNom(String nom);
}
