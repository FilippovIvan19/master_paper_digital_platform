package org.filippov.controller;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.service.FileTopicFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired @Qualifier("csvTopicFiller")
    private FileTopicFiller csvTopicFiller;

    @ResponseBody
    @GetMapping(value = "fill/csv")
    public Void fillTopicFromScv(@RequestParam(required = false) String file) {
        csvTopicFiller.fillInputTopic(file);
        return null;
    }
}
