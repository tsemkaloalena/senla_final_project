package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dto.ScheduleItemDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.ScheduleItemMapper;
import com.tsemkalo.senlaExperienceExchangeApp.service.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.READ;

/**
 * Common controller for lessons and courses
 */
@Slf4j
@Controller
@RequestMapping
public class CatalogController {
    @Autowired
    private LessonService lessonService;

    @Autowired
    private ScheduleItemMapper scheduleItemMapper;

    /**
     * @return Directory of knowledge areas by topics and sections
     */
    @GetMapping()
    public String getThemes(Model model) {
        model.addAttribute("themes", lessonService.getThemes());
        return "index";
    }

    /**
     * @return Schedule of future and current lessons with date, theme and subject
     */
    @PreAuthorize(READ)
    @GetMapping("/schedule")
    public String getSchedule(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<ScheduleItemDto> lessons = lessonService.getSchedule(authentication.getName()).stream().map(scheduleItemMapper::toDto).collect(Collectors.toList());
        model.addAttribute("lessons", lessons);
        return "schedule";
    }
}
