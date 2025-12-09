package com.prestify.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for Offer Entity
 */
@DisplayName("Offer Entity Tests")
class OfferTest {

    private Offer offer;

    @BeforeEach
    void setUp() {
        offer = new Offer();
    }

    @Test
    @DisplayName("Should create offer with no args constructor")
    void testOfferDefaultConstructor() {
        assertNotNull(offer);
        assertNull(offer.getId());
        assertNull(offer.getTitle());
        assertNull(offer.getPrice());
    }

    @Test
    @DisplayName("Should create offer with id, title and price")
    void testOfferConstructorWithBasicFields() {
        Offer ofr = new Offer(1L, "Réparation", 100.0);
        
        assertEquals(1L, ofr.getId());
        assertEquals("Réparation", ofr.getTitle());
        assertEquals(100.0, ofr.getPrice());
    }

    @Test
    @DisplayName("Should create offer with all fields")
    void testOfferConstructorWithAllFields() {
        Offer ofr = new Offer(1L, "Réparation", "Réparation urgente", 100.0, "Paris");
        
        assertEquals(1L, ofr.getId());
        assertEquals("Réparation", ofr.getTitle());
        assertEquals("Réparation urgente", ofr.getDescription());
        assertEquals(100.0, ofr.getPrice());
        assertEquals("Paris", ofr.getLocation());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        offer.setId(1L);
        assertEquals(1L, offer.getId());
    }

    @Test
    @DisplayName("Should set and get title")
    void testSetAndGetTitle() {
        offer.setTitle("Installation Électrique");
        assertEquals("Installation Électrique", offer.getTitle());
    }

    @Test
    @DisplayName("Should set and get description")
    void testSetAndGetDescription() {
        String desc = "Installation électrique complète";
        offer.setDescription(desc);
        assertEquals(desc, offer.getDescription());
    }

    @Test
    @DisplayName("Should set and get price")
    void testSetAndGetPrice() {
        offer.setPrice(150.50);
        assertEquals(150.50, offer.getPrice());
    }

    @Test
    @DisplayName("Should set and get location")
    void testSetAndGetLocation() {
        offer.setLocation("Lyon");
        assertEquals("Lyon", offer.getLocation());
    }

    @Test
    @DisplayName("Should set and get status")
    void testSetAndGetStatus() {
        offer.setStatus("ACTIVE");
        assertEquals("ACTIVE", offer.getStatus());
    }

    @Test
    @DisplayName("Should set multiple fields")
    void testSetMultipleFields() {
        offer.setId(2L);
        offer.setTitle("Nettoyage");
        offer.setDescription("Nettoyage professionnel");
        offer.setPrice(75.0);
        offer.setLocation("Marseille");
        offer.setStatus("AVAILABLE");

        assertEquals(2L, offer.getId());
        assertEquals("Nettoyage", offer.getTitle());
        assertEquals("Nettoyage professionnel", offer.getDescription());
        assertEquals(75.0, offer.getPrice());
        assertEquals("Marseille", offer.getLocation());
        assertEquals("AVAILABLE", offer.getStatus());
    }

    @Test
    @DisplayName("Should test equals with same id")
    void testEqualsWithSameId() {
        Offer ofr1 = new Offer(1L, "Réparation", 100.0);
        Offer ofr2 = new Offer(1L, "Installation", 150.0);
        
        assertEquals(ofr1, ofr2);
    }

    @Test
    @DisplayName("Should test equals with different id")
    void testEqualsWithDifferentId() {
        Offer ofr1 = new Offer(1L, "Réparation", 100.0);
        Offer ofr2 = new Offer(2L, "Installation", 150.0);
        
        assertNotEquals(ofr1, ofr2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        Offer ofr1 = new Offer(1L, "Réparation", 100.0);
        
        assertNotEquals(ofr1, null);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        Offer ofr1 = new Offer(1L, "Réparation", 100.0);
        Offer ofr2 = new Offer(1L, "Réparation", 100.0);
        
        assertEquals(ofr1.hashCode(), ofr2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        Offer ofr = new Offer(1L, "Réparation", "Urgent", 100.0, "Paris");
        String str = ofr.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("Réparation"));
        assertTrue(str.contains("100"));
    }

    @Test
    @DisplayName("Should validate positive price")
    void testValidatePositivePrice() {
        offer.setPrice(99.99);
        assertTrue(offer.getPrice() > 0);
    }

    @Test
    @DisplayName("Should handle zero price")
    void testHandleZeroPrice() {
        offer.setPrice(0.0);
        assertEquals(0.0, offer.getPrice());
    }

    @Test
    @DisplayName("Should handle negative price")
    void testHandleNegativePrice() {
        offer.setPrice(-50.0);
        assertEquals(-50.0, offer.getPrice());
    }

    @Test
    @DisplayName("Should handle null fields")
    void testHandleNullFields() {
        offer.setTitle(null);
        offer.setDescription(null);
        offer.setPrice(null);
        offer.setLocation(null);

        assertNull(offer.getTitle());
        assertNull(offer.getDescription());
        assertNull(offer.getPrice());
        assertNull(offer.getLocation());
    }
}
