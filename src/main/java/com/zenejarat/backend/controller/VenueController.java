package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Venue;
import com.zenejarat.backend.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // Összes helyszín lekérdezése
    @GetMapping
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    // Egy helyszín lekérdezése ID alapján
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        Optional<Venue> venue = venueService.getVenueById(id);
        return venue.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Új helyszín létrehozása
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue savedVenue = venueService.saveVenue(venue);
        return ResponseEntity.ok(savedVenue);
    }

    // Helyszín frissítése ID alapján
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        Optional<Venue> updatedVenue = venueService.getVenueById(id).map(existingVenue -> {
            existingVenue.setName(venueDetails.getName());
            existingVenue.setAddress(venueDetails.getAddress());
            existingVenue.setDescription(venueDetails.getDescription());
            return venueService.saveVenue(existingVenue);
        });
        return updatedVenue.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Helyszín törlése ID alapján
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
