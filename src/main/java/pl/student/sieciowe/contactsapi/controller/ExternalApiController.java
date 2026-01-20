package pl.student.sieciowe.contactsapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.service.ContactService;
import pl.student.sieciowe.contactsapi.service.PhoneValidationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final PhoneValidationService phoneValidationService;
    private final ContactService contactService;

    /**
     * Walidacja numeru telefonu przez zewnętrzne API (NumVerify)
     * 
     * @param number numer telefonu do walidacji (np. "48123456789" dla Polski)
     * @return szczegóły walidacji (kraj, operator, typ linii)
     */
    @GetMapping("/validate-phone/{number}")
    public ResponseEntity<Map<String, Object>> validatePhone(@PathVariable String number) {
        Map<String, Object> validation = phoneValidationService.validatePhoneNumber(number);

        Map<String, Object> response = new HashMap<>();
        response.put("requestedNumber", number);
        response.put("validation", validation);
        response.put("formatValid", phoneValidationService.isValidPhoneFormat(number));

        return ResponseEntity.ok(response);
    }

    /**
     * Waliduje WSZYSTKIE numery telefonów zapisane w bazie dla danego użytkownika
     */
    @GetMapping("/validate-stored-contacts")
    public ResponseEntity<List<Map<String, Object>>> validateStoredContacts(@AuthenticationPrincipal User user) {
        List<Contact> contacts = contactService.getContactsByUser(user);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Contact contact : contacts) {
            Map<String, Object> validation = phoneValidationService.validatePhoneNumber(contact.getPhoneNumber());
            Map<String, Object> contactResult = new HashMap<>();
            contactResult.put("contactId", contact.getId());
            contactResult.put("name", contact.getFirstName() + " " + contact.getLastName());
            contactResult.put("phoneNumber", contact.getPhoneNumber());
            contactResult.put("validation", validation);
            results.add(contactResult);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Sprawdza formaty WSZYSTKICH numerów zapisanych w bazie dla danego użytkownika
     * (lokalnie)
     */
    @GetMapping("/check-stored-format")
    public ResponseEntity<List<Map<String, Object>>> checkStoredFormat(@AuthenticationPrincipal User user) {
        List<Contact> contacts = contactService.getContactsByUser(user);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Contact contact : contacts) {
            Map<String, Object> contactResult = new HashMap<>();
            contactResult.put("contactId", contact.getId());
            contactResult.put("name", contact.getFirstName() + " " + contact.getLastName());
            contactResult.put("phoneNumber", contact.getPhoneNumber());
            contactResult.put("validFormat", phoneValidationService.isValidPhoneFormat(contact.getPhoneNumber()));
            results.add(contactResult);
        }

        return ResponseEntity.ok(results);
    }
}
