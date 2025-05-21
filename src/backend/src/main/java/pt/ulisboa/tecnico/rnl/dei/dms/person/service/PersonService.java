package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	private Person fetchPersonOrThrow(long id) {
		return personRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
	}

	private Person buildPerson(Long id, PersonDto personDto) {
		validatePerson(personDto);
		Person person = new Person(personDto);
		setTeachers(person, personDto.teachers());
		person.setId(id);
		return person;
	}

	@Transactional
	private void setTeachers(Person person, List<Long> teacherIds) {
		if (teacherIds != null && !teacherIds.isEmpty()) {
			List<Person> teachers = personRepository.findAllById(teacherIds).stream()
					.filter(p -> p.getType() == Person.PersonType.TEACHER)
					.toList();

			person.setTeachers(teachers);
		}
	}

	@Transactional
	public List<PersonDto> getPeople() {
		return personRepository.findAll().stream()
				.map(PersonDto::new)
				.toList();
	}

	@Transactional
	public List<PersonDto> getStudents() {
		return personRepository.findByType(PersonType.STUDENT).stream()
				.map(PersonDto::new)
				.toList();
	}

	@Transactional
	public List<PersonDto> getTeacher() {
		return personRepository.findByType(PersonType.TEACHER).stream()
				.map(PersonDto::new)
				.toList();
	}

	@Transactional
	public PersonDto createPerson(PersonDto personDto) {
		return new PersonDto(personRepository.save(buildPerson(null, personDto)));
	}

	private void validatePerson(PersonDto personDto) {
		// Validate name (no digits)
		String name = personDto.name();
		if (name == null || name.trim().isEmpty() || name.matches(".*\\d.*")) {
			throw new IllegalArgumentException("Nome não pode conter números");
		}
	
		// Validate IST ID
		String istId = personDto.istId();
		if (istId == null || !istId.matches("(?i)^ist1\\d{1,6}$") || istId.length() > 10) {
			throw new IllegalArgumentException("IstID invalid");
		}
	
		// Validate type (not null or empty)
		String type = personDto.type();
		if (type == null || type.trim().isEmpty()) {
			throw new IllegalArgumentException("Tipo é obrigatório");
		}
	
		// Validate email
		String email = personDto.email();
		if (email == null || !email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
			throw new IllegalArgumentException("E-mail Invalido");
		}
	}
	

	@Transactional
	public PersonDto getPerson(long id) {
		return new PersonDto(fetchPersonOrThrow(id));
	}

	@Transactional
	public PersonDto updatePerson(long id, PersonDto personDto) {
		fetchPersonOrThrow(id); // ensure exists

		return new PersonDto(personRepository.save(buildPerson(id, personDto)));
	}

	@Transactional
	
	public boolean checkPersonExists(PersonDto personDto) {
		// Check if a person exists by IST ID, email, or name
		boolean exists = personRepository.existsByIstId(personDto.istId()) ||
				personRepository.existsByEmail(personDto.email()) ||
				personRepository.existsByName(personDto.name());
		return exists;
	}

	@Transactional
	public void deletePerson(long id) {
		fetchPersonOrThrow(id); // ensure exists

		personRepository.deleteById(id);
	}

}
