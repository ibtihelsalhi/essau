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
 * Unit Tests for OfferService
 * Coverage > 60%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OfferService Unit Tests")
class OfferServiceImplTest {

    @Mock
    private IOfferRepository offerRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer offer1;
    private Offer offer2;
    private Offer offer3;

    @BeforeEach
    void setUp() {
        offer1 = new Offer(1L, "Réparation Plomberie", "Réparation urgente", 100.0, "Paris");
        offer2 = new Offer(2L, "Installation Électrique", "Installation complète", 150.0, "Lyon");
        offer3 = new Offer(3L, "Nettoyage", "Nettoyage maison", 50.0, "Marseille");
    }

    // ===== ADD OFFER TESTS =====
    @Test
    @DisplayName("Should add offer successfully")
    void testAddOffer_Success() {
        when(offerRepository.save(offer1)).thenReturn(offer1);

        Offer result = offerService.addOffer(offer1);

        assertNotNull(result);
        assertEquals("Réparation Plomberie", result.getTitle());
        assertEquals(100.0, result.getPrice());
        verify(offerRepository, times(1)).save(offer1);
    }

    @Test
    @DisplayName("Should throw exception when adding null offer")
    void testAddOffer_NullOffer() {
        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(null);
        });
        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when offer title is empty")
    void testAddOffer_EmptyTitle() {
        Offer emptyTitleOffer = new Offer();
        emptyTitleOffer.setTitle("");
        emptyTitleOffer.setPrice(100.0);

        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(emptyTitleOffer);
        });
    }

    @Test
    @DisplayName("Should throw exception when offer title is null")
    void testAddOffer_NullTitle() {
        Offer nullTitleOffer = new Offer();
        nullTitleOffer.setPrice(100.0);

        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(nullTitleOffer);
        });
    }

    @Test
    @DisplayName("Should throw exception when offer price is null")
    void testAddOffer_NullPrice() {
        Offer nullPriceOffer = new Offer();
        nullPriceOffer.setTitle("Test Offer");
        nullPriceOffer.setPrice(null);

        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(nullPriceOffer);
        });
    }

    @Test
    @DisplayName("Should throw exception when offer price is zero")
    void testAddOffer_ZeroPrice() {
        Offer zeroPriceOffer = new Offer();
        zeroPriceOffer.setTitle("Test Offer");
        zeroPriceOffer.setPrice(0.0);

        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(zeroPriceOffer);
        });
    }

    @Test
    @DisplayName("Should throw exception when offer price is negative")
    void testAddOffer_NegativePrice() {
        Offer negativePriceOffer = new Offer();
        negativePriceOffer.setTitle("Test Offer");
        negativePriceOffer.setPrice(-50.0);

        assertThrows(IllegalArgumentException.class, () -> {
            offerService.addOffer(negativePriceOffer);
        });
    }

    // ===== GET OFFER BY ID TESTS =====
    @Test
    @DisplayName("Should get offer by id successfully")
    void testGetOfferById_Success() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer1));

        Offer result = offerService.getOfferById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Réparation Plomberie", result.getTitle());
        verify(offerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when offer not found")
    void testGetOfferById_NotFound() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            offerService.getOfferById(99L);
        });
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void testGetOfferById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            offerService.getOfferById(null);
        });
    }

    // ===== GET ALL OFFERS TESTS =====
    @Test
    @DisplayName("Should get all offers")
    void testGetAllOffers() {
        List<Offer> offers = Arrays.asList(offer1, offer2, offer3);
        when(offerRepository.findAll()).thenReturn(offers);

        List<Offer> result = offerService.getAllOffers();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(offerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no offers exist")
    void testGetAllOffers_Empty() {
        when(offerRepository.findAll()).thenReturn(Arrays.asList());

        List<Offer> result = offerService.getAllOffers();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== DELETE OFFER TESTS =====
    @Test
    @DisplayName("Should delete offer successfully")
    void testDeleteOffer_Success() {
        when(offerRepository.existsById(1L)).thenReturn(true);

        offerService.deleteOffer(1L);

        verify(offerRepository, times(1)).existsById(1L);
        verify(offerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent offer")
    void testDeleteOffer_NotFound() {
        when(offerRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            offerService.deleteOffer(99L);
        });
    }

    @Test
    @DisplayName("Should throw exception when delete id is null")
    void testDeleteOffer_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            offerService.deleteOffer(null);
        });
    }

    // ===== UPDATE OFFER TESTS =====
    @Test
    @DisplayName("Should update offer successfully")
    void testUpdateOffer_Success() {
        Offer updatedOffer = new Offer(1L, "Réparation Plomberie Pro", "Réparation urgente avancée", 120.0, "Paris");
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer1));
        when(offerRepository.save(any())).thenReturn(updatedOffer);

        Offer result = offerService.updateOffer(1L, updatedOffer);

        assertNotNull(result);
        assertEquals("Réparation Plomberie Pro", result.getTitle());
        assertEquals(120.0, result.getPrice());
        verify(offerRepository, times(1)).findById(1L);
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating with null id")
    void testUpdateOffer_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            offerService.updateOffer(null, offer1);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with null offer")
    void testUpdateOffer_NullOffer() {
        assertThrows(IllegalArgumentException.class, () -> {
            offerService.updateOffer(1L, null);
        });
    }

    // ===== GET BY TITLE TESTS =====
    @Test
    @DisplayName("Should get offers by title")
    void testGetOffersByTitle() {
        List<Offer> offers = Arrays.asList(offer1);
        when(offerRepository.findByTitle("Réparation Plomberie")).thenReturn(offers);

        List<Offer> result = offerService.getOffersByTitle("Réparation Plomberie");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(offerRepository, times(1)).findByTitle("Réparation Plomberie");
    }

    @Test
    @DisplayName("Should return empty list when title is null")
    void testGetOffersByTitle_NullTitle() {
        List<Offer> result = offerService.getOffersByTitle(null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(offerRepository, never()).findByTitle(any());
    }

    @Test
    @DisplayName("Should return empty list when title is empty")
    void testGetOffersByTitle_EmptyTitle() {
        List<Offer> result = offerService.getOffersByTitle("");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== GET BY LOCATION TESTS =====
    @Test
    @DisplayName("Should get offers by location")
    void testGetOffersByLocation() {
        List<Offer> offers = Arrays.asList(offer1);
        when(offerRepository.findByLocation("Paris")).thenReturn(offers);

        List<Offer> result = offerService.getOffersByLocation("Paris");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(offerRepository, times(1)).findByLocation("Paris");
    }

    @Test
    @DisplayName("Should return empty list when location is null")
    void testGetOffersByLocation_NullLocation() {
        List<Offer> result = offerService.getOffersByLocation(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return empty list when location is empty")
    void testGetOffersByLocation_EmptyLocation() {
        List<Offer> result = offerService.getOffersByLocation("");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== GET BY PRICE RANGE TESTS =====
    @Test
    @DisplayName("Should get offers by price range")
    void testGetOffersByPriceRange() {
        List<Offer> offers = Arrays.asList(offer1, offer2);
        when(offerRepository.findByPriceBetween(50.0, 200.0)).thenReturn(offers);

        List<Offer> result = offerService.getOffersByPriceRange(50.0, 200.0);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(offerRepository, times(1)).findByPriceBetween(50.0, 200.0);
    }

    @Test
    @DisplayName("Should return empty list when minPrice is null")
    void testGetOffersByPriceRange_NullMinPrice() {
        List<Offer> result = offerService.getOffersByPriceRange(null, 100.0);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return empty list when maxPrice is null")
    void testGetOffersByPriceRange_NullMaxPrice() {
        List<Offer> result = offerService.getOffersByPriceRange(50.0, null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return empty list when minPrice is negative")
    void testGetOffersByPriceRange_NegativeMinPrice() {
        List<Offer> result = offerService.getOffersByPriceRange(-10.0, 100.0);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return empty list when minPrice > maxPrice")
    void testGetOffersByPriceRange_InvalidRange() {
        List<Offer> result = offerService.getOffersByPriceRange(200.0, 50.0);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== OFFER EXISTS TESTS =====
    @Test
    @DisplayName("Should return true when offer exists")
    void testOfferExists_True() {
        when(offerRepository.existsById(1L)).thenReturn(true);

        boolean result = offerService.offerExists(1L);

        assertTrue(result);
        verify(offerRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Should return false when offer does not exist")
    void testOfferExists_False() {
        when(offerRepository.existsById(99L)).thenReturn(false);

        boolean result = offerService.offerExists(99L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when id is null")
    void testOfferExists_NullId() {
        boolean result = offerService.offerExists(null);

        assertFalse(result);
        verify(offerRepository, never()).existsById(any());
    }

    // ===== INTEGRATION TESTS =====
    @Test
    @DisplayName("Should handle complete workflow: add, get, update, delete")
    void testCompleteWorkflow() {
        when(offerRepository.save(offer1)).thenReturn(offer1);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer1));
        when(offerRepository.existsById(1L)).thenReturn(true);
        when(offerRepository.save(any())).thenReturn(offer1);

        Offer added = offerService.addOffer(offer1);
        Offer retrieved = offerService.getOfferById(added.getId());
        boolean exists = offerService.offerExists(1L);
        
        Offer updated = new Offer(1L, "Updated", 200.0);
        offerService.updateOffer(1L, updated);
        
        offerService.deleteOffer(1L);

        assertTrue(exists);
        verify(offerRepository, times(1)).save(offer1);
        verify(offerRepository, times(1)).findById(1L);
        verify(offerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle multiple offers search operations")
    void testMultipleSearchOperations() {
        List<Offer> allOffers = Arrays.asList(offer1, offer2, offer3);
        List<Offer> parisOffers = Arrays.asList(offer1);
        List<Offer> priceRangeOffers = Arrays.asList(offer2);

        when(offerRepository.findAll()).thenReturn(allOffers);
        when(offerRepository.findByLocation("Paris")).thenReturn(parisOffers);
        when(offerRepository.findByPriceBetween(100.0, 200.0)).thenReturn(priceRangeOffers);

        List<Offer> all = offerService.getAllOffers();
        List<Offer> byLocation = offerService.getOffersByLocation("Paris");
        List<Offer> byPrice = offerService.getOffersByPriceRange(100.0, 200.0);

        assertEquals(3, all.size());
        assertEquals(1, byLocation.size());
        assertEquals(1, byPrice.size());
    }
}
