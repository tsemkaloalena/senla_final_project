package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dto.ScheduleItemDto;
import org.springframework.stereotype.Component;

@Component
public class ScheduleItemMapper implements Mapper<Lesson, ScheduleItemDto> {

	@Override
	public ScheduleItemDto toDto(Lesson entity) {
		return new ScheduleItemDto(entity.getId(), entity.getTheme(), entity.getSubject(), entity.getLessonDate());
	}

	@Override
	public Lesson toEntity(ScheduleItemDto dto) {
		return null;
	}
}
