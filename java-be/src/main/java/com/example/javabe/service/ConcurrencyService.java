package com.example.javabe.service;

import com.example.javabe.model.Artist;
import com.example.javabe.repositories.ArtistRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
        Integer startingFollowers = artist.getFollowers();
        Integer modifiedFollowersTransaction1 = 99;
        artist.setFollowers(modifiedFollowersTransaction1);
        artistRepository.save(artist);

        // make HTTP request to Python endpoint to perform update
        String pythonUrl = "http://localhost:5000/dirty-read";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"id\": " + id + "}", headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

        // sleep 5 seconds to allow the Python code to modify the data
        Thread.sleep(5000);

        // extract modified difficulty level from the response
        Integer modifiedFollowersTransaction2;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            modifiedFollowersTransaction2 = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject().get("modified_followers").getAsInt();
        } else {
            System.out.println("Error: " + responseEntity.getStatusCodeValue());
            return "";
        }

        artist = artistRepository.findById(id).orElse(null);
        Integer rollbackTransaction1 = startingFollowers;
        artist.setFollowers(rollbackTransaction1);
        artistRepository.save(artist);

        Integer finalFollowers = artistRepository.findById(id).orElse(null).getFollowers();

        Map<String, Integer> map = new HashMap<>();
        map.put("startingFollowers", startingFollowers);
        map.put("modifiedFollowersTransaction1", modifiedFollowersTransaction1);
        map.put("modifiedFollowersTransaction2", modifiedFollowersTransaction2);
        map.put("rollbackTransaction1", rollbackTransaction1);
        map.put("finalFollowers", finalFollowers);

        return objectMapper.writeValueAsString(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String phantomRead() throws JsonProcessingException {
        // initial read
        Integer startingRowsCount = artistRepository.findArtistByFollowersGreaterThan(10).size();

        String pythonUrl = "http://localhost:5000/phantom-read";
        restTemplate.postForObject(pythonUrl, null, String.class);

        // second read
        Integer finalRowsCount = artistRepository.findArtistByFollowersGreaterThan(10).size();

        Map<String, Integer> map = new HashMap<>();
        map.put("startingRowsCount", startingRowsCount);
        map.put("finalRowsCount", finalRowsCount);

        return objectMapper.writeValueAsString(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String lostUpdate(Integer id) throws InterruptedException, JsonProcessingException {
        // initial read
        Artist artist = artistRepository.findById(id).orElse(null);
        Integer startingFollowers = artist.getFollowers();
        Integer modifiedFollowersTransaction1 = startingFollowers - 1;
        artist.setFollowers(modifiedFollowersTransaction1);
        artistRepository.save(artist);

        // make HTTP request to Python endpoint to perform update
        String pythonUrl = "http://localhost:5000/lost-update";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"id\": " + id + "}", headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

        // sleep 5 seconds to allow the Python code to modify the data
        Thread.sleep(5000);

        // extract modified difficulty level from the response
        Integer modifiedFollowersTransaction2;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            modifiedFollowersTransaction2 = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject().get("modified_followers").getAsInt();
        } else {
            System.out.println("Error: " + responseEntity.getStatusCodeValue());
            return "";
        }

        Integer finalFollowers = artistRepository.findById(id).orElse(null).getFollowers();

        Map<String, Integer> map = new HashMap<>();
        map.put("startingFollowers", startingFollowers);
        map.put("modifiedFollowersTransaction1", modifiedFollowersTransaction1);
        map.put("modifiedFollowersTransaction2", modifiedFollowersTransaction2);
        map.put("finalFollowers", finalFollowers);

        return objectMapper.writeValueAsString(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String unrepeatableReads(Integer id) throws InterruptedException, JsonProcessingException {
        // initial read
        Artist artist = artistRepository.findById(id).orElse(null);
        Integer startingFollowers = artist.getFollowers();

        // make HTTP request to Python endpoint to perform update
        String pythonUrl = "http://localhost:5000/unrepeatable-reads";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"id\": " + id + "}", headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

        // sleep 5 seconds to allow the Python code to modify the data
        Thread.sleep(5000);

        // extract modified difficulty level from the response
        Integer modifiedFollowers;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            modifiedFollowers = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject().get("modified_followers").getAsInt();
        } else {
            System.out.println("Error: " + responseEntity.getStatusCodeValue());
            return "";
        }

        Integer finalFollowers = artistRepository.findById(id).orElse(null).getFollowers();

        Map<String, Integer> map = new HashMap<>();
        map.put("startingFollowers", startingFollowers);
        map.put("modifiedFollowers", modifiedFollowers);
        map.put("finalFollowers", finalFollowers);

        return objectMapper.writeValueAsString(map);
    }
}
