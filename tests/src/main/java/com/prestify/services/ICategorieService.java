package com.prestify.services;

/**
 * Service for managing categories
 */
public interface ICategorieService {
    Categorie addCategorie(Categorie categorie);
    Categorie getCategorieById(Long id);
    java.util.List<Categorie> getAllCategories();
    void deleteCategorie(Long id);
    Categorie updateCategorie(Long id, Categorie categorie);
    java.util.List<Categorie> findAll();
    boolean existsByNom(String nom);
}
