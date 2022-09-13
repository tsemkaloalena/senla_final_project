DROP DATABASE IF EXISTS experience_exchange_db;
CREATE DATABASE experience_exchange_db;
USE experience_exchange_db;

CREATE TABLE roles (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
name varchar(64) NOT NULL
);

CREATE TABLE permissions (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
name varchar(64) NOT NULL,
role_id bigint REFERENCES roles(id)
);

CREATE TABLE users (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
username varchar(64) NOT NULL,
password varchar(64) NOT NULL,
name varchar(64) NOT NULL,
surname varchar(64) NOT NULL,
role_id bigint REFERENCES roles(id),
email varchar(64) NOT NULL
);

CREATE TABLE courses (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
theme varchar(64) NOT NULL,
subject varchar(64) NOT NULL,
status varchar(64) NOT NULL,
online varchar(64) NOT NULL,
address varchar(64),
teacher_id bigint REFERENCES users(id),
individual boolean NOT NULL,
cost int,
start_date datetime,
finish_date datetime,
description varchar(1024) NOT NULL,
average_rating float
);

CREATE TABLE lessons (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
theme varchar(64) NOT NULL,
subject varchar(64) NOT NULL,
teacher_id bigint REFERENCES users(id),
course_id bigint REFERENCES courses(id),
status varchar(64) NOT NULL,
address varchar(64),
individual boolean NOT NULL,
max_students_number int,
description varchar(1024) NOT NULL,
cost int,
durability int,
lesson_date datetime NOT NULL,
fixed_salary int,
unfixed_salary int,
average_rating float
);

CREATE TABLE subscriptions (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
student_id bigint REFERENCES users(id),
lesson_id bigint REFERENCES lessons(id)
);

CREATE TABLE reviews (
id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
rating int NOT NULL,
text varchar(1024),
user_id bigint REFERENCES users(id),
lesson_id bigint REFERENCES lessons(id),
course_id bigint REFERENCES courses(id)
);
