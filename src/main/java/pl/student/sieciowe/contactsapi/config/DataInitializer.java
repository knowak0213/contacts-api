package pl.student.sieciowe.contactsapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.repository.ContactRepository;
import pl.student.sieciowe.contactsapi.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Zawsze upewnij się, że admin istnieje i ma poprawne hasło
        // (Naprawia problem, gdy w bazie zostało stare konto z błędnym hasłem)
        User admin = userRepository.findByUsername("admin")
                .orElse(User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .build());

        admin.setPassword(passwordEncoder.encode("password"));
        userRepository.save(admin);

        if (contactRepository.count() == 0) {
            Contact c1 = Contact.builder()
                    .firstName("Jan")
                    .lastName("Kowalski")
                    .email("jan@test.pl")
                    .phoneNumber("500-100-100")
                    .user(admin)
                    .build();

            Contact c2 = Contact.builder()
                    .firstName("Anna")
                    .lastName("Zimna")
                    .email("anna@test.pl")
                    .phoneNumber("600-200-200")
                    .user(admin)
                    .build();

            contactRepository.save(c1);
            contactRepository.save(c2);
        }

        System.out.println("--- DATA INITIALIZED: Admin password reset to 'password' ---");
    }
}
