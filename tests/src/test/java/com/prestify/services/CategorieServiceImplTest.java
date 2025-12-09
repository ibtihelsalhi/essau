package com.prestify.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Tests for CategorieService
 * Coverage > 60%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategorieService Unit Tests")
class CategorieServiceImplTest {

    @Mock
    private ICategorieRepository categorieRepository;

    @InjectMocks
    private CategorieServiceImpl categorieService;

    private Categorie categorie1;
    private Categorie categorie2;

    @BeforeEach
    void setUp() {
        categorie1 = new Categorie(1L, "Plomberie", "Services de plomberie");
        categorie2 = new Categorie(2L, "Électricité", "Services d'électricité");
    }

    // ===== ADD TESTS =====
    @Test
    @DisplayName("Should add categorie successfully")
    void testAddCategorie_Success() {
        when(categorieRepository.save(categorie1)).thenReturn(categorie1);

        Categorie result = categorieService.addCategorie(categorie1);

        assertNotNull(result);
        assertEquals("Plomberie", result.getNom());
        verify(categorieRepository, times(1)).save(categorie1);
    }

    @Test
    @DisplayName("Should throw exception when adding null categorie")
    void testAddCategorie_NullCategorie() {
        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.addCategorie(null);
        });
        verify(categorieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when categorie nom is empty")
    void testAddCategorie_EmptyNom() {
        Categorie emptyCat = new Categorie();
        emptyCat.setNom("");

        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.addCategorie(emptyCat);
        });
    }

    @Test
    @DisplayName("Should throw exception when categorie nom is null")
    void testAddCategorie_NullNom() {
        Categorie nullCat = new Categorie();
        nullCat.setNom(null);

        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.addCategorie(nullCat);
        });
    }

    // ===== GET BY ID TESTS =====
    @Test
    @DisplayName("Should get categorie by id successfully")
    void testGetCategorieById_Success() {
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie1));

        Categorie result = categorieService.getCategorieById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Plomberie", result.getNom());
        verify(categorieRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when categorie not found")
    void testGetCategorieById_NotFound() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categorieService.getCategorieById(99L);
        });
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void testGetCategorieById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.getCategorieById(null);
        });
        verify(categorieRepository, never()).findById(any());
    }

    // ===== GET ALL TESTS =====
    @Test
    @DisplayName("Should get all categories")
    void testGetAllCategories() {
        List<Categorie> categories = Arrays.asList(categorie1, categorie2);
        when(categorieRepository.findAll()).thenReturn(categories);

        List<Categorie> result = categorieService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Plomberie", result.get(0).getNom());
        assertEquals("Électricité", result.get(1).getNom());
        verify(categorieRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void testGetAllCategories_Empty() {
        when(categorieRepository.findAll()).thenReturn(Arrays.asList());

        List<Categorie> result = categorieService.getAllCategories();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== DELETE TESTS =====
    @Test
    @DisplayName("Should delete categorie successfully")
    void testDeleteCategorie_Success() {
        when(categorieRepository.existsById(1L)).thenReturn(true);

        categorieService.deleteCategorie(1L);

        verify(categorieRepository, times(1)).existsById(1L);
        verify(categorieRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existent categorie")
    void testDeleteCategorie_NotFound() {
        when(categorieRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            categorieService.deleteCategorie(99L);
        });
        verify(categorieRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw exception when delete id is null")
    void testDeleteCategorie_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.deleteCategorie(null);
        });
        verify(categorieRepository, never()).existsById(any());
    }

    // ===== UPDATE TESTS =====
    @Test
    @DisplayName("Should update categorie successfully")
    void testUpdateCategorie_Success() {
        Categorie updatedCategorie = new Categorie(1L, "Plomberie Pro", "Services avancés");
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie1));
        when(categorieRepository.save(any())).thenReturn(updatedCategorie);

        Categorie result = categorieService.updateCategorie(1L, updatedCategorie);

        assertNotNull(result);
        assertEquals("Plomberie Pro", result.getNom());
        verify(categorieRepository, times(1)).findById(1L);
        verify(categorieRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating with null id")
    void testUpdateCategorie_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.updateCategorie(null, categorie1);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with null categorie")
    void testUpdateCategorie_NullCategorie() {
        assertThrows(IllegalArgumentException.class, () -> {
            categorieService.updateCategorie(1L, null);
        });
    }

    // ===== FIND ALL TESTS =====
    @Test
    @DisplayName("Should find all categories")
    void testFindAll() {
        List<Categorie> categories = Arrays.asList(categorie1, categorie2);
        when(categorieRepository.findAll()).thenReturn(categories);

        List<Categorie> result = categorieService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ===== EXISTS BY NOM TESTS =====
    @Test
    @DisplayName("Should return true when categorie nom exists")
    void testExistsByNom_True() {
        when(categorieRepository.existsByNom("Plomberie")).thenReturn(true);

        boolean result = categorieService.existsByNom("Plomberie");

        assertTrue(result);
        verify(categorieRepository, times(1)).existsByNom("Plomberie");
    }

    @Test
    @DisplayName("Should return false when categorie nom does not exist")
    void testExistsByNom_False() {
        when(categorieRepository.existsByNom("NonExistent")).thenReturn(false);

        boolean result = categorieService.existsByNom("NonExistent");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when nom is null")
    void testExistsByNom_NullNom() {
        boolean result = categorieService.existsByNom(null);

        assertFalse(result);
        verify(categorieRepository, never()).existsByNom(any());
    }

    @Test
    @DisplayName("Should return false when nom is empty")
    void testExistsByNom_EmptyNom() {
        boolean result = categorieService.existsByNom("");

        assertFalse(result);
        verify(categorieRepository, never()).existsByNom(any());
    }

    // ===== INTEGRATION TESTS =====
    @Test
    @DisplayName("Should handle complete workflow: add, get, update, delete")
    void testCompleteWorkflow() {
        when(categorieRepository.save(categorie1)).thenReturn(categorie1);
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie1));
        when(categorieRepository.existsById(1L)).thenReturn(true);
        when(categorieRepository.save(any())).thenReturn(categorie1);

        Categorie added = categorieService.addCategorie(categorie1);
        Categorie retrieved = categorieService.getCategorieById(added.getId());
        
        Categorie updated = new Categorie(1L, "Plomberie Moderne", "Updated");
        categorieService.updateCategorie(1L, updated);
        
        categorieService.deleteCategorie(1L);

        verify(categorieRepository, times(1)).save(categorie1);
        verify(categorieRepository, times(1)).findById(1L);
        verify(categorieRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle multiple categories operations")
    void testMultipleCategoriesOperations() {
        List<Categorie> categories = Arrays.asList(categorie1, categorie2);
        when(categorieRepository.findAll()).thenReturn(categories);
        when(categorieRepository.existsByNom("Plomberie")).thenReturn(true);
        when(categorieRepository.existsByNom("Électricité")).thenReturn(true);

        List<Categorie> all = categorieService.getAllCategories();
        boolean existsPl = categorieService.existsByNom("Plomberie");
        boolean existsEl = categorieService.existsByNom("Électricité");

        assertEquals(2, all.size());
        assertTrue(existsPl);
        assertTrue(existsEl);
    }
}
