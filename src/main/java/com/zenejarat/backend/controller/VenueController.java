package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Venue;
import com.zenejarat.backend.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ez a vezérlő REST API végpontokat kezel helyszínekhez.
@RequestMapping("/api/venues") // Minden végpont az /api/venues útvonal alatt érhető el.
public class VenueController {

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService; // A helyszíneket kezelő szolgáltatást konstruktoron keresztül kapom meg.
    }

    @GetMapping // Lekérem az összes helyszínt.
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues(); // Visszaadom az összes helyszínt a szolgáltatáson keresztül.
    }

    @GetMapping("/{id}") // Egy adott helyszínt kérek le ID alapján.
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        Optional<Venue> venue = venueService.getVenueById(id); // Megpróbálom lekérni a helyszínt.
        return venue.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem találom, 404-es választ adok.
    }

    @PostMapping // Létrehozok egy új helyszínt.
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue savedVenue = venueService.saveVenue(venue); // Elmentem az új helyszínt.
        return ResponseEntity.ok(savedVenue); // Visszaküldöm a mentett példányt válaszként.
    }

    @PutMapping("/{id}") // Frissítem a meglévő helyszínt ID alapján.
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        Optional<Venue> updatedVenue = venueService.getVenueById(id).map(existingVenue -> {
            // Frissítem az adatokat a meglévő példányban.
            existingVenue.setName(venueDetails.getName());
            existingVenue.setAddress(venueDetails.getAddress());
            existingVenue.setDescription(venueDetails.getDescription());
            return venueService.saveVenue(existingVenue); // Elmentem a frissített adatokat.
        });
        return updatedVenue.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem található, 404-et küldök vissza.
    }

    @DeleteMapping("/{id}") // Törlöm a helyszínt az ID alapján.
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id); // Meghívom a törlő műveletet a szolgáltatásban.
        return ResponseEntity.noContent().build(); // 204 No Content válaszként jelzem a sikeres törlést.
    }
}
