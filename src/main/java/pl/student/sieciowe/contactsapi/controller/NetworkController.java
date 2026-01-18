package pl.student.sieciowe.contactsapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.student.sieciowe.contactsapi.dto.EmailRequest;
import pl.student.sieciowe.contactsapi.dto.FtpRequest;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.service.ContactService;
import pl.student.sieciowe.contactsapi.service.EmailService;
import pl.student.sieciowe.contactsapi.service.NetworkService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkService networkService;
    private final EmailService emailService;
    private final ContactService contactService;

    /**
     * TCP - Test połączenia TCP z zewnętrznym serwerem
     * Domyślnie testuje połączenie z google.com:80 (HTTP)
     */
    @GetMapping("/tcp-test")
    public ResponseEntity<Map<String, Object>> testTcp(
            @RequestParam(defaultValue = "google.com") String host,
            @RequestParam(defaultValue = "80") int port) {

        String result = networkService.testTcpConnection(host, port);

        Map<String, Object> response = new HashMap<>();
        response.put("protocol", "TCP");
        response.put("host", host);
        response.put("port", port);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * UDP - Test wysyłania pakietu UDP
     * Domyślnie wysyła pakiet do serwera DNS Google (8.8.8.8:53)
     */
    @GetMapping("/udp-test")
    public ResponseEntity<Map<String, Object>> testUdp(
            @RequestParam(defaultValue = "8.8.8.8") String host,
            @RequestParam(defaultValue = "53") int port,
            @RequestParam(defaultValue = "Hello UDP") String message) {

        String result = networkService.testUdpConnection(host, port, message);

        Map<String, Object> response = new HashMap<>();
        response.put("protocol", "UDP");
        response.put("host", host);
        response.put("port", port);
        response.put("message", message);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * SMTP - Wysyłanie emaila przez SMTP
     */
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody EmailRequest request) {
        String result = emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());

        Map<String, Object> response = new HashMap<>();
        response.put("protocol", "SMTP");
        response.put("to", request.getTo());
        response.put("subject", request.getSubject());
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * FTP - Eksport kontaktów użytkownika na serwer FTP
     */
    @PostMapping("/ftp-upload")
    public ResponseEntity<Map<String, Object>> uploadToFtp(
            @RequestBody FtpRequest request,
            @AuthenticationPrincipal User user) {

        // Pobierz kontakty użytkownika
        List<Contact> contacts = contactService.getContactsByUser(user);

        // Konwertuj do CSV
        StringBuilder csv = new StringBuilder();
        csv.append("id,firstName,lastName,email,phoneNumber\n");
        for (Contact c : contacts) {
            csv.append(c.getId()).append(",")
                    .append(c.getFirstName()).append(",")
                    .append(c.getLastName()).append(",")
                    .append(c.getEmail()).append(",")
                    .append(c.getPhoneNumber()).append("\n");
        }

        String filename = "contacts_" + user.getUsername() + ".csv";
        String result = networkService.uploadToFtp(
                request.getHost(),
                request.getPort(),
                request.getUsername(),
                request.getPassword(),
                filename,
                csv.toString());

        Map<String, Object> response = new HashMap<>();
        response.put("protocol", "FTP");
        response.put("host", request.getHost());
        response.put("filename", filename);
        response.put("contactsCount", contacts.size());
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Podsumowanie dostępnych protokołów
     */
    @GetMapping("/protocols")
    public ResponseEntity<Map<String, String>> getProtocols() {
        Map<String, String> protocols = new HashMap<>();
        protocols.put("TCP", "GET /api/network/tcp-test - testuje połączenie TCP");
        protocols.put("UDP", "GET /api/network/udp-test - wysyła pakiet UDP");
        protocols.put("SMTP", "POST /api/network/send-email - wysyła email");
        protocols.put("FTP", "POST /api/network/ftp-upload - eksportuje kontakty przez FTP");
        return ResponseEntity.ok(protocols);
    }
}
