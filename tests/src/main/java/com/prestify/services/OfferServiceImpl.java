package com.prestify.services;

import java.util.List;

/**
 * Implementation of OfferService
 */
public class OfferServiceImpl implements IOfferService {
    
    private IOfferRepository offerRepository;

    public OfferServiceImpl(IOfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public Offer addOffer(Offer offer) {
        if (offer == null) {
            throw new IllegalArgumentException("Offer cannot be null");
        }
        if (offer.getTitle() == null || offer.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Offer title cannot be empty");
        }
        if (offer.getPrice() == null || offer.getPrice() <= 0) {
            throw new IllegalArgumentException("Offer price must be greater than 0");
        }
        return offerRepository.save(offer);
    }

    @Override
    public Offer getOfferById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
    }

    @Override
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @Override
    public void deleteOffer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!offerRepository.existsById(id)) {
            throw new RuntimeException("Offer not found with id: " + id);
        }
        offerRepository.deleteById(id);
    }

    @Override
    public Offer updateOffer(Long id, Offer offer) {
        if (id == null || offer == null) {
            throw new IllegalArgumentException("ID and Offer cannot be null");
        }
        
        Offer existing = getOfferById(id);
        existing.setTitle(offer.getTitle());
        existing.setDescription(offer.getDescription());
        existing.setPrice(offer.getPrice());
        existing.setLocation(offer.getLocation());
        existing.setStatus(offer.getStatus());
        
        return offerRepository.save(existing);
    }

    @Override
    public List<Offer> getOffersByTitle(String title) {
        if (title == null || title.isEmpty()) {
            return List.of();
        }
        return offerRepository.findByTitle(title);
    }

    @Override
    public List<Offer> getOffersByLocation(String location) {
        if (location == null || location.isEmpty()) {
            return List.of();
        }
        return offerRepository.findByLocation(location);
    }

    @Override
    public List<Offer> getOffersByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null || minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return List.of();
        }
        return offerRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public boolean offerExists(Long id) {
        if (id == null) {
            return false;
        }
        return offerRepository.existsById(id);
    }
}
