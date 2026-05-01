package com.student.app.endpoint;

import com.student.app.generated.soap.StudentReportRequest;
import com.student.app.generated.soap.StudentReportResponse;
import com.student.app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.*;

import java.util.Base64;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * StudentReportEndpoint:
 * ----------------------
 * Demonstrates how Spring Boot integrates with Spring Web Services (Spring-WS) to expose SOAP endpoints.
 *
 * Key Concepts:
 * -------------
 * 1. @Endpoint:
 *    - Marks this class as a SOAP endpoint.
 *    - Equivalent to @RestController in REST, but for SOAP.
 *
 * 2. @PayloadRoot:
 *    - Maps incoming SOAP messages to a specific method.
 *    - Uses namespace + localPart (root element of the SOAP request).
 *
 * 3. @RequestPayload:
 *    - Binds the SOAP request body to a Java object (StudentReportRequest).
 *
 * 4. @ResponsePayload:
 *    - Ensures the method return value is serialized back into a SOAP response.
 *
 * 5. Service Layer Integration:
 *    - StudentService is injected via @Autowired.
 *    - Business logic (PDF generation) is delegated to the service layer.
 *
 * 6. Base64 Encoding:
 *    - PDF bytes are encoded into Base64 string for transport in SOAP response.
 */
@Endpoint
public class StudentReportEndpoint {

    // Namespace URI defines unique identifier for SOAP service
	//Actual XML message namespace
    private static final String NAMESPACE_URI = "http://student.com/report";

    private final StudentService studentService;

    private static final Logger logger = LogManager.getLogger(StudentReportEndpoint.class);

    @Autowired
    public StudentReportEndpoint(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * SOAP operation: getStudentReport
     * - Triggered when SOAP request has root element <StudentReportRequest>
     * - Namespace must match NAMESPACE_URI
     * - Returns StudentReportResponse with Base64 encoded PDF
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "StudentReportRequest")
    @ResponsePayload
    public StudentReportResponse getStudentReport(@RequestPayload StudentReportRequest request) {
        int studentId = request.getStudentId();
        logger.info("SOAP request received for student report, ID={}", studentId);

        byte[] pdfBytes = studentService.generateStudentReportPDF(studentId);

        if (Objects.isNull(pdfBytes)) {
            logger.error("Failed to generate PDF report for student ID {}", studentId);
            StudentReportResponse errorResponse = new StudentReportResponse();
            errorResponse.setStudentId(studentId);
            errorResponse.setPdfBase64("ERROR: Could not generate report");
            return errorResponse;
        }

        String encodedPdf = Base64.getEncoder().encodeToString(pdfBytes);
        logger.debug("PDF report generated and encoded for student ID {}", studentId);

        StudentReportResponse response = new StudentReportResponse();
        response.setStudentId(studentId);
        response.setPdfBase64(encodedPdf);

        logger.info("SOAP response prepared successfully for student ID {}", studentId);
        return response;
    }
}


/*
SOAP Client Request (XML)
			│
			▼
┌───────────────────────────────┐
│ MessageDispatcherServlet      │
│ Central SOAP controller       │
└───────────────┬───────────────┘
        		│
        		▼
┌────────────────────────────────┐
│ StudentReportEndpoint          │
│ @PayloadRoot matches request   │
│ Method invoked                 │
└────────────────┬───────────────┘
        		 │
        		 ▼
┌────────────────────────────────┐
│ StudentService                 │
│ Business logic: generate PDF   │
└───────────────┬────────────────┘
        		│
        		▼
┌───────────────────────────────┐
│ Response Object (SOAP)        │
│ PDF encoded as Base64 string  │
└───────────────┬───────────────┘
        		│
        		▼
	SOAP Client Response (XML)
*/