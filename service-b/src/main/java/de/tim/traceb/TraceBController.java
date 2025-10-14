package de.tim.traceb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/process")
public class TraceBController {

    private final RestTemplate restTemplate;

    public TraceBController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String process() {
        return restTemplate.getForObject("http://localhost:8083/api/work", String.class);
    }
}
