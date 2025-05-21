package pt.ulisboa.tecnico.rnl.dei.dms.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;

import java.util.List;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByType(PersonType type);

    // Check if a person with a specific IST ID exists
    boolean existsByIstId(String istId);

    // Check if a person with a specific email exists
    boolean existsByEmail(String email);

    // Check if a person with a specific name exists
    boolean existsByName(String name);
}
