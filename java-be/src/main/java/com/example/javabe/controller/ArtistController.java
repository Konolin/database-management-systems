package com.example.javabe.controller;

import com.example.javabe.model.Artist;
import com.example.javabe.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("")
    public ResponseEntity<List<Artist>> getArtists() {
        try {
            return new ResponseEntity<>(artistService.getArtists(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Integer id) {
        try {
            return ResponseEntity
                    .ok()
                    .body(artistService.getArtistById(id));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("")
    public ResponseEntity<Artist> addArtist(@RequestBody Artist artist) {
        try {
            return new ResponseEntity<>(artistService.addArtist(artist), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping("")
    public ResponseEntity<Artist> editArtist(@RequestBody Artist artist) {
        try {
            return new ResponseEntity<>(artistService.editArtist(artist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteArtist(@RequestBody Artist artist) {
        try {
            artistService.deleteArtist(artist);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
