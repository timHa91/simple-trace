package de.tim.tracea;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/start")
public class TraceAController {

    private final RestTemplate restTemplate;

    public TraceAController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String start() {
        return restTemplate.getForObject("http://localhost:8082/api/process", String.class);
    }
}
