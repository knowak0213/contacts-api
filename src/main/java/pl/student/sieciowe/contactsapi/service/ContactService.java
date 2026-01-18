package pl.student.sieciowe.contactsapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.repository.ContactRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public List<Contact> getContactsByUser(User user) {
        return contactRepository.findByUser(user);
    }

    public Contact addContact(Contact contact, User user) {
        contact.setUser(user);
        return contactRepository.save(contact);
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
