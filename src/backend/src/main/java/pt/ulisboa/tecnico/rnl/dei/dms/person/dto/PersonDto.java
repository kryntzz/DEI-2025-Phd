package pt.ulisboa.tecnico.rnl.dei.dms.person.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

import java.util.List;
import java.time.LocalDate;

public record PersonDto(
		long id,
		String name,
		String istId,
		String email,
		String type,
		String workflowTese,
		String workflowDef,
		String description,
		List<Long> teachers,
		LocalDate defenseDate,
		int grade
) {
	public PersonDto(Person person) {
		this(
				person.getId(),
				person.getName(),
				person.getIstId(),
				person.getEmail(),
				person.getType().toString(),
				person.getWorkflowTese() != null ? person.getWorkflowTese().toString() : null,
				person.getWorkflowDef() != null ? person.getWorkflowDef().toString() : null,
				person.getDescription(),
				person.getTeachers() != null
						? person.getTeachers().stream().map(Person::getId).toList()
						: null,
				person.getDefenseDate(),
				person.getGrade()
		);
	}
}
