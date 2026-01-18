package pl.student.sieciowe.contactsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;
import pl.student.sieciowe.contactsapi.repository.ContactRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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

    public Contact updateContact(Long id, Contact updatedContact) {
        return contactRepository.findById(id)
                .map(contact -> {
                    if (updatedContact.getFirstName() != null)
                        contact.setFirstName(updatedContact.getFirstName());
                    if (updatedContact.getLastName() != null)
                        contact.setLastName(updatedContact.getLastName());
                    if (updatedContact.getEmail() != null)
                        contact.setEmail(updatedContact.getEmail());
                    if (updatedContact.getPhoneNumber() != null)
                        contact.setPhoneNumber(updatedContact.getPhoneNumber());
                    return contactRepository.save(contact);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
    }
}
