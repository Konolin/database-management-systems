package com.example.javabe.service;

import com.example.javabe.model.Artist;
import com.example.javabe.repositories.ArtistRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public List<Artist> getArtists() {
        return artistRepository.findAll();
    }

    public Artist getArtistById(Integer id) {
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if (artistOptional.isEmpty()) {
            throw new EntityNotFoundException("Artist with id " + id + " not found");
        }
        return artistOptional.get();
    }

    public Artist addArtist(Artist artist) {
        if (artistRepository.existsById(artist.getId())) {
            throw new EntityExistsException("Artist with id " + artist.getId() + " already exists");
        }
        return artistRepository.save(artist);
    }

    public Artist editArtist(Artist artist) {
        if (!artistRepository.existsById(artist.getId())) {
            throw new EntityNotFoundException("Artist with id " + artist.getId() + " not found");
        }
        return artistRepository.save(artist);
    }

    public boolean deleteArtist(Artist artist) {
        if (!artistRepository.existsById(artist.getId())) {
            throw new EntityNotFoundException("Artist with id " + artist.getId() + " not found");
        }
        artistRepository.delete(artist);
        return true;
    }
}
