package com.student.app.listener;

import com.student.app.model.StudentInsertLog;
import com.student.app.model.StudentPersonal;
import com.student.app.repository.StudentInsertLogRepository;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * StudentPersonalListener:
 * ------------------------
 * This class demonstrates how **JPA Entity Listeners** integrate with Spring Boot.
 *
 * Key Concepts:
 * -------------
 * 1. @Component:
 *    - Marks this class as a Spring-managed bean.
 *    - Allows Spring to inject dependencies (like repositories).
 *
 * 2. Static Repository Injection:
 *    - JPA listeners are not Spring-managed by default.
 *    - To access Spring beans inside a listener, we inject the repository into a static field.
 *
 * 3. @Autowired init():
 *    - Spring calls this method during bean initialization.
 *    - Sets the static reference to StudentInsertLogRepository.
 *
 * 4. @PostPersist:
 *    - JPA lifecycle callback annotation.
 *    - Triggered automatically after a StudentPersonal entity is persisted (inserted into DB).
 *    - Useful for **auditing, logging, or triggering side effects**.
 *
 * 5. Logging Insert Events:
 *    - After a StudentPersonal record is saved, a StudentInsertLog entry is created.
 *    - Captures studentId, message, and timestamp.
 *    - Ensures every insert is tracked for auditing.
 */
@Component
public class StudentPersonalListener {

    // Static repository reference used inside JPA listener
    private static StudentInsertLogRepository logRepo;

    private static final Logger logger = LogManager.getLogger(StudentPersonalListener.class);

    // Inject repository into static field
    @Autowired
    public void init(StudentInsertLogRepository repo) {
        StudentPersonalListener.logRepo = repo;
        logger.info("StudentInsertLogRepository injected into StudentPersonalListener");
    }

    /**
     * @PostPersist → Runs after StudentPersonal entity is inserted.
     * - Creates a StudentInsertLog entry.
     * - Saves it using StudentInsertLogRepository.
     * - Provides automatic logging of insert events.
     */
    @PostPersist
    public void afterInsert(StudentPersonal student) {
    	String message;
        logger.info("PostPersist triggered for StudentPersonal with ID {}", student.getStudentId());

        StudentInsertLog logEntry = new StudentInsertLog();
        logEntry.setStudentId(student.getStudentId());
        message="Inserted StudentPersonal record for " + student.getFirstName() + " " + student.getLastName();
        logEntry.setTimestamp(LocalDateTime.now());

        try {
            logRepo.save(logEntry);
            logger.debug("StudentInsertLog saved: {}", logEntry);
        } catch (Exception e) {
            logger.error("Failed to save StudentInsertLog for student ID {}: {}", student.getStudentId(), message, e);
        }
    }
}
