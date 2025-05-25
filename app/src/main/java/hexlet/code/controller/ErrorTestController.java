package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ErrorTestController {

    @GetMapping("/test/error")
    public String throwError() {
        throw new RuntimeException("Error");
    }
}
