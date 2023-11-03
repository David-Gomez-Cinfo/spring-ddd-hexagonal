package site.deiv70.springboot.healthcare.infrastructure.in.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import site.deiv70.springboot.healthcare.infrastructure.in.HealthcareWorkerDtoPatchObject;
import site.deiv70.springboot.healthcare.infrastructure.in.model.HealthcareWorkerDtoModel;
import site.deiv70.springboot.healthcare.utils.Utils;

@Mapper(componentModel = "spring")
public abstract class HealthcareWorkerPatchMapper {

	@BeforeMapping
	protected void processNulls(@MappingTarget HealthcareWorkerDtoPatchObject patchObject, Map<String, String> hashMap) {
		Map<String, String> nulls = new HashMap<>();
		// Rename map's keys to match patchObject's DTO keys
		Utils.getJsonProperyHashMap(HealthcareWorkerDtoModel.class).forEach((jsonKey, field) -> {
				if (hashMap.containsKey(jsonKey)) {
					hashMap.put(field.getName(), hashMap.get(jsonKey));
					if (!jsonKey.equals(field.getName())) {
						hashMap.remove(jsonKey);
					}
				}
			}
		);
		// Assign map's null values to patchObject's nullKeys
		hashMap.forEach((key, value) -> {
			if (null == value) {
				nulls.put(key, null);
			}
		});
		// Remove null values from map
		nulls.forEach((key, value) -> hashMap.remove(key));
		patchObject.setNullKeys(nulls);
	}

	@Mapping(target = "dto", source = ".")
	@Mapping(target = "nullKeys", ignore = true)
	public abstract HealthcareWorkerDtoPatchObject toDtoPatchObject(Map<String, String> map);

}
