package com.baddog.databasefixture.controller;

import com.baddog.databasefixture.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Log4j2
public class DatabaseFixtureController {
    private final VideoRepository videoRepository;

    @GetMapping("/drop-collection")
    public void dropCollection(@RequestParam("collection") String collection) {
        videoRepository.dropCollection(collection);
    }

    @GetMapping("/load-fixture")
    public void loadFixture(@RequestParam("fixture") String fixture, @RequestParam("collection") String collection) {
        try {
            videoRepository.loadFixture(fixture, collection);
        } catch (IOException e) {
            log.error("Error loading fixture: " + e.getMessage());
        }
    }

    @GetMapping("/unload-fixture")
    public void unloadFixture(@RequestParam("fixture") String fixture, @RequestParam("collection") String collection) {
        try {
            videoRepository.unloadFixture(fixture, collection);
        } catch (IOException e) {
            log.error("Error unloading fixture: " + e.getMessage());
        }
    }
}
