package de.thi.informatik.edi.flux;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public Flux<Long> getAllEmployees() {
        return Flux.interval(Duration.ofSeconds(1));
    }
}
