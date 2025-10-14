package de.tim.tracec;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work")
public class TraceCController {

    @GetMapping
    public String done() {
        return "Done";
    }
}
