package com.example.javabe.service;

import com.example.javabe.model.Artist;
import com.example.javabe.repositories.ArtistRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ConcurrencyService {
    private final ArtistRepository artistRepository;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public String dirtyWrite(Artist artist) throws InterruptedException, JsonProcessingException {
        // update the artist name
        artist.setName("ZZZZZ");
        artistRepository.save(artist);

        // delay to let other transaction happen
        Thread.sleep(5000);

        // call the Python transaction that updates the starting name of the same user
        String pythonUrl = "http://localhost:5000/dirty-write";
        ObjectMapper objectMapper = new ObjectMapper();
        String artistJson = objectMapper.writeValueAsString(artist);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(artistJson, headers);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(pythonUrl, entity, String.class);

        return response;
    }
}
