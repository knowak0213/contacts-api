package pl.student.sieciowe.contactsapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PhoneValidationService {

    private final RestTemplate restTemplate;

    @Value("${numverify.api.key:}")
    private String apiKey;

    public PhoneValidationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Waliduje numer telefonu używając zewnętrznego API NumVerify
     * API URL: http://apilayer.net/api/validate
     * 
     * @param phoneNumber numer telefonu do walidacji (np. "48123456789")
     * @return mapa z wynikami walidacji
     */
    public Map<String, Object> validatePhoneNumber(String phoneNumber) {
        Map<String, Object> result = new HashMap<>();

        // Jeśli brak klucza API, zwróć demo odpowiedź
        if (apiKey == null || apiKey.isEmpty()) {
            result.put("valid", true);
            result.put("number", phoneNumber);
            result.put("country_code", "PL");
            result.put("country_name", "Poland");
            result.put("carrier", "Demo Carrier");
            result.put("line_type", "mobile");
            result.put("note", "Demo mode - no API key configured. Set numverify.api.key in application.properties");
            return result;
        }

        try {
            String url = String.format(
                    "http://apilayer.net/api/validate?access_key=%s&number=%s",
                    apiKey, phoneNumber);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                return response;
            } else {
                result.put("error", "Empty response from API");
            }
        } catch (Exception e) {
            result.put("error", "API call failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Sprawdza czy numer jest prawidłowy (uproszczona walidacja lokalna)
     */
    public boolean isValidPhoneFormat(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        // Usuń spacje i myślniki
        String cleaned = phoneNumber.replaceAll("[\\s-]", "");
        // Sprawdź czy składa się z cyfr i opcjonalnego + na początku
        return cleaned.matches("^\\+?[0-9]{7,15}$");
    }
}
