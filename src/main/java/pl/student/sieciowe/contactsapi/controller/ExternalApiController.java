package pl.student.sieciowe.contactsapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.student.sieciowe.contactsapi.service.PhoneValidationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final PhoneValidationService phoneValidationService;

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
     * Sprawdza tylko format numeru (bez API)
     */
    @GetMapping("/check-format/{number}")
    public ResponseEntity<Map<String, Object>> checkFormat(@PathVariable String number) {
        Map<String, Object> response = new HashMap<>();
        response.put("number", number);
        response.put("validFormat", phoneValidationService.isValidPhoneFormat(number));
        return ResponseEntity.ok(response);
    }
}
