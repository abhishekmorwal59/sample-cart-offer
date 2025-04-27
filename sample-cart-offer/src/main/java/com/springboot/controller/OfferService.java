package com.springboot.controller;

import java.util.List;

public class OfferService {

    // Mock methods to simulate data checks
    private boolean isValidSegmentForCustomer(int restaurantId, List<String> customerSegment) {
        // Logic to validate if the restaurant has valid customer segments
        return customerSegment.contains("p1"); // Assuming 'p1' is valid for this example
    }

    private boolean checkIfOfferExists(int restaurantId, String offerType) {
        // Logic to check if the offer already exists for the restaurant
        return false; // Simulating that no offer exists for testing purposes
    }

    private boolean checkIfRestaurantExists(int restaurantId) {
        // Logic to check if the restaurant exists
        return restaurantId != 999; // Simulate non-existing restaurant ID as 999
    }

    private boolean checkIfRestaurantHasActiveOffer(int restaurantId) {
        // Logic to check if restaurant has any active offer
        return restaurantId != 1; // Simulate active offer for restaurant 1 only
    }

    public boolean applyOffer(OfferRequest offerRequest) {
        // 1. Validate negative offer value
        if (offerRequest.getOffer_value() < 0) {
            return false; // Do not apply offer for negative cart value
        }

        // 2. Validate non-empty customer segments
        if (offerRequest.getCustomer_segment().isEmpty()) {
            return false; // Do not apply offer if no segment is provided
        }

        // 3. Validate the segment
        if (!isValidSegmentForCustomer(offerRequest.getRestaurant_id(), offerRequest.getCustomer_segment())) {
            return false; // Customer not in any valid segment
        }

        // 4. Check for duplicate offers
        if (checkIfOfferExists(offerRequest.getRestaurant_id(), offerRequest.getOffer_type())) {
            return false; // Offer already exists for the same restaurant
        }

        // 5. Check if percentage offer is greater than 100%
        if ("FLATX%".equals(offerRequest.getOffer_type()) && offerRequest.getOffer_value() > 100) {
            return false; // Do not apply offer if percentage is greater than 100%
        }

        // 6. Check if restaurant has an active offer
        if (!checkIfRestaurantHasActiveOffer(offerRequest.getRestaurant_id())) {
            return false; // Restaurant does not have any active offer
        }

        // 7. Check if restaurant exists
        if (!checkIfRestaurantExists(offerRequest.getRestaurant_id())) {
            return false; // Restaurant does not exist
        }

        // Apply the offer if all conditions are met
        return true;
    }
}
