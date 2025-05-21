package pt.ulisboa.tecnico.rnl.dei.dms.person.domain;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

@Data
@Entity
@Table(name = "people")
public class Person {

    public enum PersonType {
        COORDINATOR, STAFF, STUDENT, TEACHER, SC
    }

    public enum WorkflowState {
        SUBMITED, APPROVED, ATRIBUTED, SIGNED, STAFF
    }

    public enum WorkflowStateDef{
        SCHEDULED, REVISION, SUBMITED
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ist_id", nullable = false, unique = true)
    private String istId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "grade")
    private int grade;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonType type;

    @Column(name = "workflowTese")
    @Enumerated(EnumType.STRING)
    private WorkflowState workflowTese;

    @Column(name = "workflowDef")
    @Enumerated(EnumType.STRING)
    private WorkflowStateDef workflowDef;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ✅ Add this: a student can have many teachers
    @ManyToMany
    @JoinTable(
        name = "student_teachers",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Person> teachers;

    @Column(name = "defenseDate")
    private LocalDate defenseDate;

    protected Person() {
    }

    public Person(String name, String istId, String email, PersonType type, WorkflowState workflowTese,
                  WorkflowStateDef workflowDef, String description, LocalDate defenseDate, int grade) {
        this.name = name;
        this.istId = istId;
        this.email = email;
        this.type = type;
        this.workflowTese = workflowTese;
        this.workflowDef = workflowDef;
        this.description = description;
        this.defenseDate = defenseDate;
        this.grade = grade;
    }

    // ⚠ Teachers are not set here directly from the DTO — do this in service layer
    public Person(PersonDto personDto) {
        this(
            personDto.name(),
            personDto.istId(),
            personDto.email(),
            PersonType.valueOf(personDto.type().toUpperCase()),
            parseWorkflowState(personDto.workflowTese()),
            parseWorkflowStateDef(personDto.workflowDef()),
            personDto.description(),
            personDto.defenseDate(),
            personDto.grade()
        );
        System.out.println("PersonDto: " + personDto);
        System.out.println("PersonType: " + personDto.type());
    }

    private static WorkflowState parseWorkflowState(String workflowState) {
        if (workflowState != null) {
            try {
                return WorkflowState.valueOf(workflowState.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }


    private static WorkflowStateDef parseWorkflowStateDef(String workflowStateDef) {
        if (workflowStateDef != null) {
            try {
                return WorkflowStateDef.valueOf(workflowStateDef.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
