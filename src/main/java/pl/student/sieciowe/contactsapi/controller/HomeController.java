package pl.student.sieciowe.contactsapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Contacts API is running! Use endpoints starting with /api/auth or /api/contacts.";
    }
}
