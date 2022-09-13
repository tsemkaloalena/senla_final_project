package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.ScheduleItemDto;
import com.tsemkalo.experienceApp.entities.Lesson;
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
