package org.filippov.controller;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.service.FileTopicFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
