package com.prestify.services;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Offer
 */
public interface IOfferRepository {
    Offer save(Offer offer);
    Optional<Offer> findById(Long id);
    List<Offer> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Offer> findByTitle(String title);
    List<Offer> findByLocation(String location);
    List<Offer> findByPriceBetween(Double minPrice, Double maxPrice);
}
