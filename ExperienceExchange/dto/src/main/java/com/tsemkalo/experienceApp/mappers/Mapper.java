package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.AbstractDto;
import com.tsemkalo.experienceApp.entities.AbstractEntity;

public interface Mapper<E extends AbstractEntity, D extends AbstractDto> {
	D toDto(E entity);

	E toEntity(D dto);
}
