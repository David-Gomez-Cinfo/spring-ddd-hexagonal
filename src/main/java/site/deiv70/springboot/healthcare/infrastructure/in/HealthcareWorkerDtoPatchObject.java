package site.deiv70.springboot.healthcare.infrastructure.in;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.deiv70.springboot.healthcare.infrastructure.in.model.HealthcareWorkerDtoModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthcareWorkerDtoPatchObject {

	private HealthcareWorkerDtoModel dto;
	private Map<String, String> nullKeys;

}
