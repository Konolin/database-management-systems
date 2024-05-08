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

    @PostMapping("/dirty-write")
    public ResponseEntity<String> dirtyWrite(@RequestParam Integer id) throws InterruptedException, JsonProcessingException {
        String response = concurrencyService.dirtyWrite(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/dirty-read")
    public ResponseEntity<String> dirtyRead(@RequestParam Integer id) throws InterruptedException {
        String response = concurrencyService.dirtyRead(id);
        return ResponseEntity.ok(response);
    }
}