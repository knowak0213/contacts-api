package pl.student.sieciowe.contactsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.student.sieciowe.contactsapi.model.Contact;
import pl.student.sieciowe.contactsapi.model.User;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser(User user);
}
