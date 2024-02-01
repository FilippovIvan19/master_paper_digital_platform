package org.filippov.controller;

import lombok.extern.slf4j.Slf4j;
import org.filippov.service.CsvTopicFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("topic")
public class TopicEndPoint {
    @Autowired
    private CsvTopicFiller csvTopicFiller;

    @ResponseBody
    @GetMapping(value = "fill/csv")
    public Void fillTopicFromScv(@RequestParam String file) {
        String currentDirectory = new File("").getAbsolutePath();
        log.info(currentDirectory);
        log.info(file);
        csvTopicFiller.fillInputTopic(file);
        return null;
    }
}
