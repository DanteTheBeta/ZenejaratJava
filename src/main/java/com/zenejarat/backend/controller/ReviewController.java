package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Review;
import com.zenejarat.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ez az osztály REST vezérlőként működik, HTTP kérésekre válaszol.
@RequestMapping("/api/reviews") // Minden végpont az /api/reviews alá tartozik.
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService; // Konstruktoron keresztül megkapom az értékeléseket kezelő szolgáltatást.
    }

    @GetMapping // Lekérem az összes értékelést.
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews(); // A szolgáltatásból kérem az összes értékelést.
    }

    @GetMapping("/{id}") // Lekérek egy értékelést azonosító alapján.
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id); // Megpróbálom lekérni a megadott ID-jű értékelést.
        return review.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem találom, 404-et adok vissza.
    }

    @PostMapping // Létrehozok egy új értékelést.
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review savedReview = reviewService.saveReview(review); // Elmentem az új értékelést.
        return ResponseEntity.ok(savedReview); // Visszaküldöm a mentett értékelést válaszként.
    }

    @PutMapping("/{id}") // Frissítem az értékelést ID alapján.
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        Optional<Review> updatedReview = reviewService.getReviewById(id)
                .map(existingReview -> {
                    // Frissítem az értékelés mezőit.
                    existingReview.setRating(reviewDetails.getRating());
                    existingReview.setComment(reviewDetails.getComment());
                    existingReview.setVenue(reviewDetails.getVenue());
                    existingReview.setUser(reviewDetails.getUser());
                    return reviewService.saveReview(existingReview); // Elmentem a frissített értékelést.
                });
        return updatedReview.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem találom, 404-et adok.
    }

    @DeleteMapping("/{id}") // Törlöm az értékelést ID alapján.
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id); // Meghívom a törléshez szükséges szolgáltatást.
        return ResponseEntity.noContent().build(); // Sikeres törlés esetén 204-es (No Content) választ küldök.
    }
}
