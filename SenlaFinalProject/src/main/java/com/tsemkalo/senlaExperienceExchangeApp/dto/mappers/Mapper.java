package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.dto.AbstractDto;

public interface Mapper<E extends AbstractEntity, D extends AbstractDto> {
	D toDto(E entity);

	E toEntity(D dto);
}
