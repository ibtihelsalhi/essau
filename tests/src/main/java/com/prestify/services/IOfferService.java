package com.prestify.services;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Offer
 */
public interface IOfferService {
    Offer addOffer(Offer offer);
    Offer getOfferById(Long id);
    List<Offer> getAllOffers();
    void deleteOffer(Long id);
    Offer updateOffer(Long id, Offer offer);
    List<Offer> getOffersByTitle(String title);
    List<Offer> getOffersByLocation(String location);
    List<Offer> getOffersByPriceRange(Double minPrice, Double maxPrice);
    boolean offerExists(Long id);
}
