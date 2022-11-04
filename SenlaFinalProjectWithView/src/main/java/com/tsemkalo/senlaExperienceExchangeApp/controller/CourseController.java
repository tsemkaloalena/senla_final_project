package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dto.CourseDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.LessonDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.SearchFilterDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.CourseMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.LessonMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.PersonalAccountMapper;
import com.tsemkalo.senlaExperienceExchangeApp.enums.PermissionType;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import com.tsemkalo.senlaExperienceExchangeApp.service.CourseService;
import com.tsemkalo.senlaExperienceExchangeApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.ADD;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.DELETE;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.EDIT;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.GET_SALARY;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.READ;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.SUBSCRIBE;

@Slf4j
@Controller
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PersonalAccountMapper personalAccountMapper;

    @Autowired
    private LessonMapper lessonMapper;

    //TODO link all href=""
    @GetMapping
    public String getCourses(Model model) {
        SearchFilterDto searchFilterDto = new SearchFilterDto();
        model.addAttribute("searchFilter", searchFilterDto);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(courseService.getStatusPredicate(searchFilterDto.getStatus()));
        model.addAttribute("courses", courseService.getCourses(predicates).stream().map(courseMapper::toDto).collect(Collectors.toList()));
        return "courses";
    }

    /**
     * Finding and filtering courses
     *
     * @param searchFilterDto filter parameters:
     *                        status - filter by status (is the course started, finished and etc),
     *                        teacherName - filter by teacher name,
     *                        teacherSurname - filter by teacher surname,
     *                        theme - filter by course theme,
     *                        minRating - find courses with rating not less than given,
     *                        online - find online / offline courses,
     *                        address - filter by address,
     *                        free - find only free courses,
     *                        minCost - find courses with cost not less than given,
     *                        maxCost - find courses with cost not greater than given,
     *                        numberOfPlaces - find courses with number of places left not less than given,
     *                        individual - find individual / group courses,
     *                        from - find courses starting after given date,
     *                        to - find courses finishing before given date
     * @return filtered list of courses
     */
    @PostMapping("/filter")
    public String getCourses(@ModelAttribute SearchFilterDto searchFilterDto, Model model) {
        model.addAttribute("searchFilter", searchFilterDto);
        List<Predicate> predicates = new ArrayList<>();
        if (stringIsNotEmpty(searchFilterDto.getTeacherName()) && stringIsNotEmpty(searchFilterDto.getTeacherSurname())) {
            predicates.add(courseService.getTeacherPredicate(searchFilterDto.getTeacherName(), searchFilterDto.getTeacherSurname()));
        }
        if (stringIsNotEmpty(searchFilterDto.getTheme())) {
            predicates.add(courseService.getThemePredicate(searchFilterDto.getTheme()));
        }
        if (searchFilterDto.getMinRating() != null) {
            predicates.add(courseService.getRatingPredicate(searchFilterDto.getMinRating()));
        }
        if (stringIsNotEmpty(searchFilterDto.getAddress()) || searchFilterDto.getOnline() != null) {
            predicates.add(courseService.getOnlineOfflinePredicate(searchFilterDto.getOnline(), searchFilterDto.getAddress()));
        }
        if (searchFilterDto.getFree() != null || searchFilterDto.getMinCost() != null || searchFilterDto.getMaxCost() != null) {
            predicates.add(courseService.getCostPredicate(searchFilterDto.getFree(), searchFilterDto.getMinCost(), searchFilterDto.getMaxCost()));
        }
        if (searchFilterDto.getIndividual() != null) {
            predicates.add(courseService.getIndividualPredicate(searchFilterDto.getIndividual()));
        }
        predicates.add(courseService.getStatusPredicate(searchFilterDto.getStatus()));
        if (searchFilterDto.getFrom() != null || searchFilterDto.getTo() != null) {
            predicates.add(courseService.getDatePredicate(searchFilterDto.getFrom(), searchFilterDto.getTo()));
        }
        if (searchFilterDto.getNumberOfPlaces() != null) {
            predicates.add(courseService.getFreePlacesLeftPredicate(searchFilterDto.getNumberOfPlaces()));
        }
        model.addAttribute("courses", courseService.getCourses(predicates).stream().map(courseMapper::toDto).collect(Collectors.toList()));
        return "courses";
    }

    @PreAuthorize(READ)
    @GetMapping("/{id}")
    public String getCourse(@PathVariable Long id, @RequestParam(required = false) String message, Model model) {
        Course course = courseService.getById(id);
        model.addAttribute("course", courseMapper.toDto(course));
        model.addAttribute("teacher", personalAccountMapper.toDto(course.getTeacher()));
        if (message != null) {
            model.addAttribute("message", message);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getAuthorities().contains(new SimpleGrantedAuthority(PermissionType.SUBSCRIBE.name()))) {
            if (courseService.isSubscribed(authentication.getName(), id)) {
                model.addAttribute("subscribed", true);
            }
            else {
                model.addAttribute("subscribed", false);
            }
        }
        return "course";
    }

    /**
     * @param id id of the course
     * @return lessons which belong to current course
     */
    @PreAuthorize(READ)
    @GetMapping("/{id}/lessons")
    public String getCourseLessons(@PathVariable Long id, Model model) {
        model.addAttribute("courseId", id);
        model.addAttribute("lessons", courseService.getCourseLessons(id).stream().map(lessonMapper::toDto).collect(Collectors.toList()));
        return "course_lessons";
    }

    @PreAuthorize(ADD)
    @GetMapping("/create")
    public String addCourse(Model model) {
        CourseDto courseDto = new CourseDto();
        model.addAttribute("course", courseDto);
        return "create_course";
    }

    /**
     * @param courseDto course data
     * @return added to database course data
     */
    @PreAuthorize(ADD)
    @PostMapping
    public String addCourse(@ModelAttribute CourseDto courseDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Course course = courseService.addCourse(authentication.getName(), courseMapper.toEntity(courseDto));
        return "redirect:/courses/" + course.getId();
    }

    @PreAuthorize(EDIT)
    @GetMapping("/{id}/edit")
    public String editCourse(@PathVariable Long id, Model model) {
        CourseDto courseDto = courseMapper.toDto(courseService.getById(id));
        model.addAttribute("course", courseDto);
        return "edit_course";
    }

    /**
     * @param id        id of the edited course
     * @param courseDto data that should be edited
     * @return edited course data
     */
    @PreAuthorize(EDIT)
    @PostMapping("/{id}")
    public String editCourse(@PathVariable Long id, @ModelAttribute CourseDto courseDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        courseDto.setId(id);
        Course course = courseService.editCourse(authentication.getName(), courseMapper.toEntity(courseDto));
        return "redirect:/courses/" + course.getId();
    }

    /**
     * @param id id of the course that should be denied
     * @return message about denied lessons that belonged to given course
     */
    @PreAuthorize(EDIT)
    @PostMapping("/{id}/deny")
    public String denyCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Long> courseStatus = courseService.denyCourse(authentication.getName(), id);
        return "redirect:/courses/" + id;
    }

    /**
     * @param id id of the course that should be deleted
     * @return message about deleted lessons that belonged to given course
     */
    @PreAuthorize(DELETE)
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Long> courseStatus = courseService.deleteCourse(authentication.getName(), id);
        return "redirect:/courses";
    }

    /**
     * @return list of courses to which the student is subscribed
     */
    @PreAuthorize(SUBSCRIBE)
    @GetMapping("/subscriptions")
    public List<CourseDto> getSubscriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return courseService.getSubscriptions(authentication.getName()).stream().map(courseMapper::toDto).collect(Collectors.toList());
    }

    /**
     * @param id id of the course to which the student should be subscribed
     * @return message about subscription success
     */
    @PreAuthorize(SUBSCRIBE)
    @PostMapping("/{id}/subscribe")
    public String subscribeCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (courseService.subscribe(authentication.getName(), id)) {
            return "redirect:/courses/" + id + "?message=You are subscribed to course with id " + id;
        }
        return "redirect:/courses/" + id + "?message=Subscription was failed. You were already subscribed to course with id " + id;
    }

    /**
     * @param id id of the course to which the student should be unsubscribed
     * @return message about success of canceling subscription
     */
    @PreAuthorize(SUBSCRIBE)
    @PostMapping("/{id}/unsubscribe")
    public String unsubscribeCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (courseService.unsubscribe(authentication.getName(), id)) {
            return "redirect:/courses/" + id + "?message=You are unsubscribed to course with id " + id;
        }
        return "redirect:/courses/" + id + "?message=Canceling subscription was failed. You were not subscribed to the course with id " + id;
    }

    /**
     * @param id id of the course for which the teacher's salary should be counted
     * @return message about counted salary
     */
    @PreAuthorize(GET_SALARY)
    @GetMapping("{id}/salary")
    public ResponseEntity<Object> countSalary(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Double salary = courseService.countSalary(authentication.getName(), id);
        Map<String, Object> body = new LinkedHashMap<>();
        if (salary == null) {
            body.put("message", "You don't have a salary, too few students");
        } else {
            body.put("message", "Your salary for the course with id " + id + " is " + salary);
        }
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    /**
     * @param id          id of the course whose status and lesson statuses should be updated
     * @param currentTime time according to which the statuses should be updated
     * @return message about success of status updates
     */
    @PreAuthorize(EDIT)
    @PutMapping("/{id}/update_statuses")
    public ResponseEntity<Object> updateLessonStatuses(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
        // automatic update by matching with current date is supported
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        courseService.updateLessonStatusesInCourse(authentication.getName(), currentTime, id);
        courseService.updateCourseStatuses(authentication.getName(), id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Your lesson statuses are updated according to the current time");
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    private Boolean stringIsNotEmpty(String string) {
        return string != null && !"".equals(string);
    }
}
