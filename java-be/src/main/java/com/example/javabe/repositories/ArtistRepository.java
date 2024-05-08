package com.example.javabe.repositories;

import com.example.javabe.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    List<Artist> findArtistByFollowersGreaterThan(Integer followers);
}
