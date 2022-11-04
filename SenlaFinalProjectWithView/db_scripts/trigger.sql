USE experience_exchange_db;

DELIMITER $$
CREATE DEFINER=root@localhost TRIGGER lesson_INSERT
AFTER INSERT
ON lessons FOR EACH ROW
BEGIN
	UPDATE courses SET courses.finish_date = date_add(new.lesson_date, interval new.duration minute)
		WHERE courses.id = new.course_id AND
			courses.finish_date < date_add(new.lesson_date, interval new.duration minute)
            OR courses.finish_date is null;
	UPDATE courses SET courses.start_date = new.lesson_date
		WHERE courses.id = new.course_id AND
			courses.start_date > new.lesson_date
            OR courses.start_date is null;
END
$$
DELIMITER ;