USE experience_exchange_db;

INSERT INTO permissions (id, name, role_id) VALUES
(1, "READ", 1),
(2, "READ", 2),
(3, "READ", 3),
(4, "ADD", 1),
(5, "ADD", 2),
(6, "EDIT", 1),
(7, "EDIT", 2),
(8, "DELETE", 1),
(9, "DELETE", 2),
(10, "SUBSCRIBE", 3),
(11, "GET_SALARY", 2),
(12, "WRITE_REVIEW", 3),
(13, "DELETE_REVIEW", 3),
(14, "DELETE_REVIEW", 1);

INSERT INTO roles(id, name) VALUES
(1, "ADMIN"),
(2, "TEACHER"),
(3, "STUDENT");

INSERT INTO users(id, username, password, name, surname, role_id, email) VALUES
(1, "svetlana", "$2a$10$eHrKX3q0Gi.K3IXxDVSnmevyIf.LLq/DEAoHCMSCkIPDsOQsrix7W", "Svetlana", "Oleina", 1, "tsemkaloalena@gmail.com"),
(2, "amy", "$2a$10$uURzRp0Dr/iC5GEP3POsPeEvpF8GBfy1WNx0l2eNbM/WyvyrZHV4S", "Amy", "Norton", 2, "tsemkaloalena@gmail.com"),
(3, "rav", "$2a$10$K9VIDic.2ZRNlDUn5Ex8p.WztqJ9VnFz935itAylM3/zMCV9TGTjW", "Rav", "Ahuja", 2, "tsemkaloalena@gmail.com"),
(4, "robby", "$2a$10$YIePp00IQCDH5cSjZdcBAeBmKJUJ8QjaJh.bKjPA51kW30PBpic.K", "Robert", "Shiller", 2, "tsemkaloalena@gmail.com"),
(5, "nadya", "$2a$10$6pY.lRuSXtWLupFhyHzZD.uyoRojk6ecvKGBqlJ5WllPdfFdOE3Vi", "Nader", "Tavassoli", 2, "tsemkaloalena@gmail.com"),
(6, "rohit", "$2a$10$d4rss2UUzw1jY0m6m8eZxOIJSAwocb0oelOhpPTjHw1GW/1XlBw42", "Rohit", "Rahi", 2, "tsemkaloalena@gmail.com"),
(7, "pasha", "$2a$10$YAy0lNUFouUPvxJZoFaYR.uwV2uKNJz8hdbmJ9dA4M.LCJbgD/jXi", "Pavel", "Pevzner", 2, "tsemkaloalena@gmail.com"),
(8, "philya", "$2a$10$gLr2A2aU1w8C6JV2x0CZKuAtaUvbtHO.whMinTKaOnvx.ggTwPyme", "Phillip", "Compeau", 2, "tsemkaloalena@gmail.com"),
(9, "lisa", "$2a$10$bbP1/bhs/ByqDYsQtg.FFO3M7Qwoiug5p8cPhQWX2xqBtn7anYoOq", "Lisa", "Mazzola", 2, "tsemkaloalena@gmail.com"),
(10, "marik", "$2a$10$h2O/buH8cHiKx6eSgFuhs..c5fB15lu2Bi1SRSMpq3d8nTJBhJROW", "Marik", "Thewho", 3, "tsemkaloalena@gmail.com"),
(11, "edik", "$2a$10$5ufPvlwI5MBXQDa/7wIdsuHneB2/ppWSAn5cz5tmhA2NuMjwrg2DS", "Edik", "NotYarik", 3, "tsemkaloalena@gmail.com"),
(12, "derevo", "$2a$10$qOIhlqKOgDb0Ftrv5cn/zOGnPIa1Rv5p83TxhiHZOnOMORkXwBVuC", "Drevo", "Obrabotka", 3, "tsemkaloalena@gmail.com");

INSERT INTO courses(id, theme, subject, status, online, address, teacher_id, individual, cost, start_date, finish_date, description, average_rating) VALUES
(1, "IT", "Google IT Support", "NOT_STARTED", "ONLINE", NULL, 2, false, 20, STR_TO_DATE("03.10.2022 15:40", "%d.%m.%Y %H:%i"), STR_TO_DATE("31.10.2022 15:40", "%d.%m.%Y %H:%i"), "This is your path to a career in IT. In this program, you’ll learn in-demand skills that will have you job-ready in less than 6 months. No degree or experience required.", NULL),
(2, "IT", "Oracle Cloud Infrastructure Foundations", "NOT_STARTED", "ONLINE", NULL, 3, false, 72, STR_TO_DATE("10.09.2022 12:00", "%d.%m.%Y %H:%i"), STR_TO_DATE("01.10.2022 12:00", "%d.%m.%Y %H:%i"), "Welcome to the course OCI Foundations Course. This course is a starting point to prepare you for the Oracle Cloud Infrastructure Foundations Associate Certification. Begin with an introduction of the OCI platform, and then dive into the core primitives, compute, storage, networking, identity, databases, security, and more.", NULL),
(3, "IT", "Technical Support Specialist (IBM)", "NOT_STARTED", "ONLINE", NULL, 3, false, 77, STR_TO_DATE("05.11.2022 12:00", "%d.%m.%Y %H:%i"), STR_TO_DATE("17.12.2022 12:00", "%d.%m.%Y %H:%i"), "Launch your rewarding new career in tech. This program will prepare you with job-ready skills valued by employers in as little as 3 months. No degree or prior experience needed to get started.", 4.5),
(4, "Business", "Financial markets", "NOT_STARTED", "ONLINE", NULL, 4, false, 105, STR_TO_DATE("10.11.2022 13:00", "%d.%m.%Y %H:%i"), STR_TO_DATE("22.11.2022 13:00", "%d.%m.%Y %H:%i"), "An overview of the ideas, methods, and institutions that permit human society to manage risks and foster enterprise.  Emphasis on financially-savvy leadership skills. Description of practices today and analysis of prospects for the future. Introduction to risk management and behavioral finance principles to understand the real-world functioning of securities, insurance, and banking industries.  The ultimate goal of this course is using such industries effectively and towards a better society.", NULL);

INSERT INTO lessons(id, theme, subject, teacher_id, course_id, status, address, individual, max_students_number, description, cost, durability, lesson_date, fixed_salary, unfixed_salary, average_rating) VALUES
(1, "IT", "Technical Support Fundamentals", 2, 1, "NOT_STARTED", NULL, false, NULL, "This course is the first of a series that aims to prepare you for a role as an entry-level IT Support Specialist. In this course, you’ll be introduced to the world of Information Technology, or IT. You’ll learn about the different facets of Information Technology, like computer hardware, the Internet, computer software, troubleshooting, and customer service. This course covers a wide variety of topics in IT that are designed to give you an overview of what’s to come in this certificate program.", 4, 60, STR_TO_DATE("03.10.2022 15:40", "%d.%m.%Y %H:%i"), NULL, 70, NULL),
(2, "IT", "The Bits and Bytes of Computer Networking", 2, 1, "NOT_STARTED", NULL, false, NULL, "This course is designed to provide a full overview of computer networking. We’ll cover everything from the fundamentals of modern networking technologies and protocols to an overview of the cloud to practical applications and network troubleshooting.", 4, 60, STR_TO_DATE("10.10.2022 15:40", "%d.%m.%Y %H:%i"), NULL, 70, NULL),
(3, "IT", "Operating Systems and You: Becoming a Power User", 2, 1, "NOT_STARTED", NULL, false, NULL, "In this course -- through a combination of video lectures, demonstrations, and hands-on practice -- you’ll learn about the main components of an operating system and how to perform critical tasks like managing software and users, and configuring hardware.", 4, 60, STR_TO_DATE("17.10.2022 15:40", "%d.%m.%Y %H:%i"), NULL, 70, NULL),
(4, "IT", "System Administration and IT Infrastructure Services", 2, 1, "NOT_STARTED", NULL, false, NULL, "This course will transition you from working on a single computer to an entire fleet. Systems administration is the field of IT that’s responsible for maintaining reliable computers systems in a multi-user environment. In this course, you’ll learn about the infrastructure services that keep all organizations, big and small, up and running. We’ll deep dive on cloud so that you’ll understand everything from typical cloud infrastructure setups to how to manage cloud resources. You'll also learn how to manage and configure servers and how to use industry tools to manage computers, user information, and user productivity. Finally, you’ll learn how to recover your organization’s IT infrastructure in the event of a disaster.", 4, 60, STR_TO_DATE("24.10.2022 15:40", "%d.%m.%Y %H:%i"), NULL, 70, NULL),
(5, "IT", "IT Security: Defense against the digital dark arts", 2, 1, "NOT_STARTED", NULL, false, NULL, "This course covers a wide variety of IT security concepts, tools, and best practices. It introduces threats and attacks and the many ways they can show up. We’ll give you some background of encryption algorithms and how they’re used to safeguard data. Then, we’ll dive into the three As of information security: authentication, authorization, and accounting. We’ll also cover network security solutions, ranging from firewalls to Wifi encryption options. The course is rounded out by putting all these elements together into a multi-layered, in-depth security architecture, followed by recommendations on how to integrate a culture of security into your organization or team.", 4, 60, STR_TO_DATE("31.10.2022 15:40", "%d.%m.%Y %H:%i"), NULL, 70, NULL),

(6, "IT", "Getting Started with OCI", 3, 2, "NOT_STARTED", NULL, false, NULL, "Kick start your Cloud journey with a strong foundation in core concepts, architecture and features of Oracle Cloud Infrastructure (OCI)", 10, 107, STR_TO_DATE("10.09.2022 12:00", "%d.%m.%Y %H:%i"), 18, NULL, NULL),
(7, "IT", "Compute,Storage and Database", 3, 2, "NOT_STARTED", NULL, false, NULL, "This module focuses on the key features of Cloud. It gives an insight into the basic concepts for optimizing the use of these services.", 10, 108, STR_TO_DATE("17.09.2022 12:00", "%d.%m.%Y %H:%i"), 18, NULL, NULL),
(8, "IT", "OCI Services", 3, 2, "NOT_STARTED", NULL, false, NULL, "Oracle Cloud has a broad platform of cloud services to support a wide range of applications in a scalable environment. This module is a walkthrough of a few of the popular OCI services.", 10, 105, STR_TO_DATE("24.09.2022 12:00", "%d.%m.%Y %H:%i"), 18, NULL, NULL),
(9, "IT", "OCI Security and Administration", 3, 2, "NOT_STARTED", NULL, false, NULL, "This module focuses on the key features of Cloud. It gives an insight into the basic concepts for optimizing the use of these services.", 10, 73, STR_TO_DATE("01.10.2022 12:00", "%d.%m.%Y %H:%i"), 18, NULL, NULL),

(10, "IT", "Introduction to Technical Support", 3, 3, "NOT_STARTED", NULL, false, NULL, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, STR_TO_DATE("05.11.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(11, "IT", "Introduction to Hardware and Operating Systems", 3, 3, "NOT_STARTED", NULL, false, NULL, "If you're ready to enter the world of Information Technology (IT), you need job-ready skills. This course enables you to develop the skills to work with computer hardware and operating systems, and is your first step to prepare for all types of tech related careers that require IT Fundamental skills.", 11, 90, STR_TO_DATE("12.11.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(12, "IT", "Introduction to Software, Programming, and Databases", 3, 3, "NOT_STARTED", NULL, false, NULL, "There are many types of software and understanding software can be overwhelming. This course aims to help you understand more about the types of software and how to manage software from an information technology (IT) perspective. This course will help you understand the basics of software, cloud computing, web browsers, development and concepts of software, programming languages, and database fundamentals.", 11, 90, STR_TO_DATE("19.11.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(13, "IT", "Introduction to Networking and Storage", 3, 3, "NOT_STARTED", NULL, false, NULL, "Gain skills that keep users connected. Learn how to diagnose and repair basic networking and security problems.", 11, 90, STR_TO_DATE("26.11.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(14, "IT", "Introduction to Cybersecurity Essentials", 3, 3, "NOT_STARTED", NULL, false, NULL, "Build key skills needed to recognize common security threats and risks. Discover the characteristics of cyber-attacks and learn how organizations employ best practices to guard against them.", 11, 90, STR_TO_DATE("03.12.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(15, "IT", "Introduction to Cloud Computing", 3, 3, "NOT_STARTED", NULL, false, NULL, "Even though this course does not require any prior cloud computing or programming experience, by the end of the course, you will have created your own account on IBM Cloud and gained some hands-on experience by provisioning a cloud service and working with it.", 11, 90, STR_TO_DATE("10.12.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),
(16, "IT", "Technical Support Case Studies and Capstone", 3, 3, "NOT_STARTED", NULL, false, NULL, "This course gives you an opportunity to show what you’ve learned in the previous IT Technical Support professional certification courses, as well as helping you solidify abstract knowledge into applied skills you can use.", 11, 90, STR_TO_DATE("17.12.2022 12:00", "%d.%m.%Y %H:%i"), NULL, 60, NULL),

(17, "Business", "Module 1", 4, 4, "NOT_STARTED", NULL, false, NULL, "Welcome to the course! In this opening module, you will learn the basics of financial markets, insurance, and CAPM (Capital Asset Pricing Model). This module serves as the foundation of this course.", 15, 184, STR_TO_DATE("10.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(18, "Business", "Module 2", 4, 4, "NOT_STARTED", NULL, false, NULL, "In this next module, dive into some details of behavioral finance, forecasting, pricing, debt, and inflation.", 15, 139, STR_TO_DATE("12.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(19, "Business", "Module 3", 4, 4, "NOT_STARTED", NULL, false, NULL, "Stocks, bonds, dividends, shares, market caps; what are these? Who needs them? Why? Module 3 explores these concepts, along with corporation basics and some basic financial markets history.", 15, 137, STR_TO_DATE("14.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(20, "Business", "Module 4", 4, 4, "NOT_STARTED", NULL, false, NULL, "Take a look into the recent past, exploring recessions, bubbles, the mortgage crisis, and regulation.", 15, 189, STR_TO_DATE("16.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(21, "Business", "Module 5", 4, 4, "NOT_STARTED", NULL, false, NULL, "Options and bond markets are explored in module 5, important components of financial markets.", 15, 125, STR_TO_DATE("18.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(22, "Business", "Module 6", 4, 4, "NOT_STARTED", NULL, false, NULL, "In module 6, Professor Shiller introduces investment banking, underwriting processes, brokers, dealers, exchanges, and new innovations in financial markets.", 15, 141, STR_TO_DATE("20.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),
(23, "Business", "Module 7", 4, 4, "NOT_STARTED", NULL, false, NULL, "Professor Shiller's final module includes lectures about nonprofits and corporations, and your career in finance.", 15, 194, STR_TO_DATE("22.11.2022 13:00", "%d.%m.%Y %H:%i"), NULL, 75, NULL),

(24, "IT", "Google Cloud Fundamentals: Core Infrastructure", 6, NULL, "NOT_STARTED",  NULL, false, NULL, "Google Cloud Fundamentals: Core Infrastructure introduces important concepts and terminology for working with Google Cloud. Through videos and hands-on labs, this course presents and compares many of Google Cloud's computing and storage services, along with important resource and policy management tools.", 13, 90, STR_TO_DATE("10.11.2022 13:20", "%d.%m.%Y %H:%i"), NULL, 85, 4.0),
(25, "IT", "Crash Course on Python", 2, NULL, "NOT_STARTED",  NULL, false, NULL, "Google Cloud Fundamentals: Core Infrastructure introduces important concepts and terminology for working with Google Cloud. Through videos and hands-on labs, this course presents and compares many of Google Cloud's computing and storage services, along with important resource and policy management tools.", 15, 90, STR_TO_DATE("12.11.2022 13:20", "%d.%m.%Y %H:%i"), NULL, 85, 5.0),
(26, "Bioinformatics", "Looking for hidden messages in DNA", 7, NULL, "NOT_STARTED", NULL, false, NULL, "This course begins a series of classes illustrating the power of computing in modern biology. Please join us on the frontier of bioinformatics to look for hidden messages in DNA without ever needing to put on a lab coat.", 16, 90, STR_TO_DATE("14.11.2022 13:20", "%d.%m.%Y %H:%i"), NULL, 85, NULL),
(27, "Bioinformatics", "Genome Sequencing", 8, NULL, "NOT_STARTED", NULL, false, NULL, "You may have heard a lot about genome sequencing and its potential to usher in an era of personalized medicine, but what does it mean to sequence a genome?", 12, 90, STR_TO_DATE("14.11.2022 14:40", "%d.%m.%Y %H:%i"), NULL, 85, NULL);

INSERT INTO subscriptions(id, student_id, lesson_id) VALUES
(1, 10, 10),
(2, 10, 11),
(3, 10, 12),
(4, 10, 13),
(5, 10, 14),
(6, 10, 15),
(7, 10, 16),
(8, 11, 10),
(9, 11, 11),
(10, 11, 12),
(11, 11, 13),
(12, 11, 14),
(13, 11, 15),
(14, 11, 16),
(15, 12, 24),
(16, 12, 25);

INSERT INTO reviews(id, rating, text, user_id, lesson_id, course_id) VALUES
(1, 5, "Review 1", 10, NULL, 3),
(4, 4, "Review 2", 11, NULL, 3),
(2, 4, "Review 3", 12, 24, NULL),
(3, 5, "Review 4", 12, 25, NULL);
