package pl.student.sieciowe.contactsapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.student.sieciowe.contactsapi.dto.RegisterRequest;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public User authenticate(pl.student.sieciowe.contactsapi.dto.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("DEBUG: Authenticating user: " + request.getUsername());
        System.out.println("DEBUG: Stored Hash: " + user.getPassword());
        System.out.println("DEBUG: Input Password: " + request.getPassword());
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("DEBUG: Password Matches: " + matches);

        if (!matches) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}
