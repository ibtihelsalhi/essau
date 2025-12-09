package com.prestify.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for Categorie Entity
 */
@DisplayName("Categorie Entity Tests")
class CategorieTest {

    private Categorie categorie;

    @BeforeEach
    void setUp() {
        categorie = new Categorie();
    }

    @Test
    @DisplayName("Should create categorie with no args constructor")
    void testCategorieDefaultConstructor() {
        assertNotNull(categorie);
        assertNull(categorie.getId());
        assertNull(categorie.getNom());
        assertNull(categorie.getDescription());
    }

    @Test
    @DisplayName("Should create categorie with id and nom")
    void testCategorieConstructorWithIdAndNom() {
        Categorie cat = new Categorie(1L, "Plomberie");
        
        assertEquals(1L, cat.getId());
        assertEquals("Plomberie", cat.getNom());
    }

    @Test
    @DisplayName("Should create categorie with all fields")
    void testCategorieConstructorWithAllFields() {
        Categorie cat = new Categorie(1L, "Plomberie", "Services de plomberie");
        
        assertEquals(1L, cat.getId());
        assertEquals("Plomberie", cat.getNom());
        assertEquals("Services de plomberie", cat.getDescription());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        categorie.setId(1L);
        assertEquals(1L, categorie.getId());
    }

    @Test
    @DisplayName("Should set and get nom")
    void testSetAndGetNom() {
        categorie.setNom("Électricité");
        assertEquals("Électricité", categorie.getNom());
    }

    @Test
    @DisplayName("Should set and get description")
    void testSetAndGetDescription() {
        String desc = "Services d'électricité professionnel";
        categorie.setDescription(desc);
        assertEquals(desc, categorie.getDescription());
    }

    @Test
    @DisplayName("Should set multiple fields")
    void testSetMultipleFields() {
        categorie.setId(2L);
        categorie.setNom("Menuiserie");
        categorie.setDescription("Services de menuiserie");

        assertEquals(2L, categorie.getId());
        assertEquals("Menuiserie", categorie.getNom());
        assertEquals("Services de menuiserie", categorie.getDescription());
    }

    @Test
    @DisplayName("Should test equals with same id")
    void testEqualsWithSameId() {
        Categorie cat1 = new Categorie(1L, "Plomberie");
        Categorie cat2 = new Categorie(1L, "Plomberie");
        
        assertEquals(cat1, cat2);
    }

    @Test
    @DisplayName("Should test equals with different id")
    void testEqualsWithDifferentId() {
        Categorie cat1 = new Categorie(1L, "Plomberie");
        Categorie cat2 = new Categorie(2L, "Électricité");
        
        assertNotEquals(cat1, cat2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        Categorie cat1 = new Categorie(1L, "Plomberie");
        
        assertNotEquals(cat1, null);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        Categorie cat1 = new Categorie(1L, "Plomberie");
        Categorie cat2 = new Categorie(1L, "Plomberie");
        
        assertEquals(cat1.hashCode(), cat2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        Categorie cat = new Categorie(1L, "Plomberie", "Services");
        String str = cat.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("Plomberie"));
        assertTrue(str.contains("Services"));
    }
}
