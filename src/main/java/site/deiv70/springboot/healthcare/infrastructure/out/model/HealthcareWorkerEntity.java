package site.deiv70.springboot.healthcare.infrastructure.out.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "healthcare_worker")
public class HealthcareWorkerEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private UUID id;
	@Column(name = "tax_identification_number")
	private String taxIdentificationNumber;
	@Column(name = "personal_identification_code")
	private Long personalIdentificationCode;
	@Column(name = "name")
	private String name;
	@Column(name = "surname")
	private String surname;
	@Column(name = "age")
	private Integer age;

}
