package site.deiv70.springboot.healthcare.infrastructure.out;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import site.deiv70.springboot.healthcare.domain.model.HealthcareWorker;
import site.deiv70.springboot.healthcare.domain.port.HealthcareWorkerRepositoryPort;
import site.deiv70.springboot.healthcare.infrastructure.in.ApiErrorException;
import site.deiv70.springboot.healthcare.infrastructure.out.mapper.HealthcareWorkerOutMapper;
import site.deiv70.springboot.healthcare.infrastructure.out.model.HealthcareWorkerEntity;

@Repository
@RequiredArgsConstructor
public class HealthcareWorkerRepositoryAdapterJPA implements HealthcareWorkerRepositoryPort {

	final HealthcareWorkerOutMapper healthcareWorkerOutMapper;
	final HealthcareWorkerJpaRepository healthcareWorkerJpaRepository;

	@Override
	public Page<HealthcareWorker> findAll(Pageable pageable) {
		Page<HealthcareWorkerEntity> entityPage = healthcareWorkerJpaRepository.findAll(pageable);
		return healthcareWorkerOutMapper.toDomain(entityPage);
	}

	@Override
	public Optional<HealthcareWorker> findById(UUID id) {
		Optional<HealthcareWorkerEntity> entity = healthcareWorkerJpaRepository.findById(id);
		return entity.map(healthcareWorkerOutMapper::toDomain);
	}

	@Override
	public HealthcareWorker save(HealthcareWorker healthcareWorker){
		HealthcareWorkerEntity entity = healthcareWorkerOutMapper.toInfrastructure(healthcareWorker);
		entity = healthcareWorkerJpaRepository.save(entity);
		return healthcareWorkerOutMapper.toDomain(entity);
	}

	@Override
	public List<HealthcareWorker> save(List<HealthcareWorker> healthcareWorker) {
		List<HealthcareWorkerEntity> entityList = healthcareWorkerOutMapper.toInfrastructure(healthcareWorker);
		entityList = healthcareWorkerJpaRepository.saveAll(entityList);
		return healthcareWorkerOutMapper.toDomain(entityList);
	}

	@Override
	public HealthcareWorker save(Map<String, Object> healthcareWorkerHashMap) {
		HealthcareWorkerEntity entity = healthcareWorkerJpaRepository.findById((UUID) healthcareWorkerHashMap.get("id"))
			.orElseThrow(() -> new ApiErrorException(
				"HealthcareWorker not found with id: " + healthcareWorkerHashMap.get("id")));
		healthcareWorkerHashMap.remove("id");

		// Get Fields from Entity
		List<Field> fields = List.of(HealthcareWorkerEntity.class.getDeclaredFields());
		// Filter Out Fields without @Column annotation
		Map<String, Field> fieldColumnHashMap = fields.stream()
			.filter(field -> field.isAnnotationPresent(Column.class))
			.collect(java.util.stream.Collectors.toMap(
				columnName -> columnName.getAnnotation(Column.class).name(),
				field -> field
			));

		healthcareWorkerHashMap.forEach((jsonKey, jsonValue) -> {
			if (null != fieldColumnHashMap.get(jsonKey)) {
				Field field = fieldColumnHashMap.get(jsonKey);
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, entity, jsonValue);
			} else {
				throw new ApiErrorException("HealthcareWorker field not found: " + jsonKey);
			}
		});

		// To save NULLs. Another alternative method is to use saveAndFlush() ?
		final HealthcareWorkerEntity newEntity = healthcareWorkerOutMapper.toNewInfrastructure(entity);

		HealthcareWorkerEntity savedEntity = healthcareWorkerJpaRepository.save(newEntity);
		//HealthcareWorkerEntity savedEntity = healthcareWorkerJpaRepository.saveAndFlush(entity);
		return healthcareWorkerOutMapper.toDomain(savedEntity);
	}

	/*
	public HealthcareWorker patch(HealthcareWorker healthcareWorker, List<String> nullKeys) {
		HealthcareWorkerEntity entity = healthcareWorkerOutMapper.toInfrastructure(healthcareWorker);

		HealthcareWorkerEntity DBEntity = healthcareWorkerJpaRepository.findById(entity.getId())
			.orElseThrow(() -> new ApiErrorException(
				"HealthcareWorker not found with id: " + entity.getId()));

		// Get Fields from Entity
		List<Field> fields = List.of(HealthcareWorkerEntity.class.getDeclaredFields());
		// Filter Out Fields without @Column annotation
		Map<String, Field> fieldColumnHashMap = fields.stream()
			.filter(field -> field.isAnnotationPresent(Column.class))
			.collect(java.util.stream.Collectors.toMap(
				columnName -> columnName.getAnnotation(Column.class).name(),
				field -> field
			));

		// Update only fields with NULL values

		// To save NULLs. Another alternative method is to use saveAndFlush() ?
		final HealthcareWorkerEntity newEntity = healthcareWorkerOutMapper.toNewInfrastructure(entity);

		HealthcareWorkerEntity savedEntity = healthcareWorkerJpaRepository.save(newEntity);
		//HealthcareWorkerEntity savedEntity = healthcareWorkerJpaRepository.saveAndFlush(entity);
		return healthcareWorkerOutMapper.toDomain(savedEntity);
	}
	*/

	@Override
	public void delete(UUID id) {
		healthcareWorkerJpaRepository.deleteById(id);
	}

}
