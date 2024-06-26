package com.example.javabe.controller;

import com.example.javabe.model.Artist;
import com.example.javabe.service.ConcurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concurrency-issues-java")
@RequiredArgsConstructor
public class ConcurrencyController {
    private final ConcurrencyService concurrencyService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/dirty-write")
    public ResponseEntity<String> dirtyWrite(@RequestParam Integer id) throws InterruptedException, JsonProcessingException {
        String response = concurrencyService.dirtyWrite(id);
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/dirty-read")
    public ResponseEntity<String> dirtyRead(@RequestParam Integer id) throws InterruptedException, JsonProcessingException {
        String response = concurrencyService.dirtyRead(id);
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/phantom-read")
    public ResponseEntity<String> phantomRead() throws JsonProcessingException {
        String response = concurrencyService.phantomRead();
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/lost-update")
    public ResponseEntity<String> lostUpdate(@RequestParam Integer id) throws InterruptedException, JsonProcessingException {
        String response = concurrencyService.lostUpdate(id);
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/unrepeatable-reads")
    public ResponseEntity<String> unrepeatableReads(@RequestParam Integer id) throws InterruptedException, JsonProcessingException {
        String response = concurrencyService.unrepeatableReads(id);
        return ResponseEntity.ok(response);
    }
}
