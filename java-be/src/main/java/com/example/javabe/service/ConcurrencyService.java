package com.example.javabe.service;

import com.example.javabe.model.Artist;
import com.example.javabe.repositories.ArtistRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConcurrencyService {
    private final ArtistRepository artistRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public String dirtyWrite(Integer id) throws InterruptedException, JsonProcessingException {
        // update the artist name
        Artist artist = artistRepository.findById(id).orElse(null);
        String startingName = artist.getName();
        artist.setName("ZZZZZ");
        String modifiedNameTransaction1 = artist.getName();
        artistRepository.save(artist);

        // delay to let other transaction happen
        Thread.sleep(5000);

        // call the Python transaction that updates the starting name of the same user
        String pythonUrl = "http://localhost:5000/dirty-write";
        String artistJson = objectMapper.writeValueAsString(artist);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(artistJson, headers);

        String modifiedNameTransaction2 = restTemplate.postForObject(pythonUrl, entity, String.class);
        String finalName = artistRepository.findById(id).orElse(null).getName();

        Map<String, String> map = new HashMap<>();
        map.put("startingName", startingName);
        map.put("modifiedNameTransaction1", modifiedNameTransaction1);
        map.put("modifiedNameTransaction2", modifiedNameTransaction2);
        map.put("finalName", finalName);

        return objectMapper.writeValueAsString(map);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public String dirtyRead(Integer id) throws InterruptedException, JsonProcessingException {
        // initial read
        Artist artist = artistRepository.findById(id).orElse(null);
        String startingName = artist.getName();

        // make HTTP request to Python endpoint to perform update
        String pythonUrl = "http://localhost:5000/dirty-read";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"id\": " + id + "}", headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

        // sleep 5 seconds to allow the Python code to modify the data
        Thread.sleep(5000);

        // extract modified difficulty level from the response
        String modifiedNameTransaction2;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            modifiedNameTransaction2 = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject().get("modified_name").getAsString();
        } else {
            System.out.println("Error: " + responseEntity.getStatusCodeValue());
            return "";
        }

        String finalName = artistRepository.findById(id).orElse(null).getName();

        Map<String, String> map = new HashMap<>();
        map.put("startingName", startingName);
        map.put("modifiedNameTransaction2", modifiedNameTransaction2);
        map.put("finalName", finalName);

        return objectMapper.writeValueAsString(map);
    }
}
