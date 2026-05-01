package com.student.app.service;

import com.student.app.model.*;
import com.student.app.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.InputStream;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class StudentServiceImplementation implements StudentService {

	private static final Logger logger = LogManager.getLogger(StudentServiceImplementation.class);

	@Autowired
	private StudentPersonalRepository personalRepo;
	@Autowired
	private StudentAcademicRepository academicRepo;
	@Autowired
	private StudentAttendanceRepository attendanceRepo;
	@Autowired
	private StudentSportsRepository sportsRepo;
	@Autowired
	private StudentJoinRepository joinRepo;


	@Override
	public void saveStudentsFromExcel(MultipartFile file) {
		logger.info("Processing Excel file: {}", file.getOriginalFilename());
		try (InputStream is = file.getInputStream();
				Workbook workbook = new XSSFWorkbook(is)) {

			// Personal Sheet
			logger.debug("Reading Personal sheet...");
			Sheet personalSheet = workbook.getSheet("Personal");
			List<StudentPersonal> personalList = new ArrayList<>();
			Iterator<Row> iterator = personalSheet.iterator();
			iterator.next();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				StudentPersonal s = new StudentPersonal();
				s.setStudentId((int) row.getCell(0).getNumericCellValue());
				s.setFirstName(row.getCell(1).getStringCellValue());
				s.setLastName(row.getCell(2).getStringCellValue());
				s.setEmail(row.getCell(3).getStringCellValue());
				s.setGender(row.getCell(4).getStringCellValue());
				s.setDob(row.getCell(5).getStringCellValue());
				personalList.add(s);
				logger.trace("Parsed StudentPersonal: {}", s);
			}
			personalRepo.saveAll(personalList);
			logger.info("Saved {} StudentPersonal records", personalList.size());

			// Academic Sheet
			logger.debug("Reading Academic sheet...");
			Sheet academicSheet = workbook.getSheet("Academic");
			List<StudentAcademic> academicList = new ArrayList<>();
			iterator = academicSheet.iterator();
			iterator.next();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				StudentAcademic s = new StudentAcademic();
				s.setStudentId((int) row.getCell(0).getNumericCellValue());
				s.setDepartment(row.getCell(1).getStringCellValue());
				s.setAverageMarks(row.getCell(2).getNumericCellValue());
				academicList.add(s);
				logger.trace("Parsed StudentAcademic: {}", s);
			}
			academicRepo.saveAll(academicList);
			logger.info("Saved {} StudentAcademic records", academicList.size());

			// Attendance Sheet
			logger.debug("Reading Attendance sheet...");
			Sheet attendanceSheet = workbook.getSheet("Attendance");
			List<StudentAttendance> attendanceList = new ArrayList<>();
			iterator = attendanceSheet.iterator();
			iterator.next();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				StudentAttendance s = new StudentAttendance();
				s.setStudentId((int) row.getCell(0).getNumericCellValue());
				s.setAttendedClasses((int) row.getCell(1).getNumericCellValue());
				s.setTotalClasses((int) row.getCell(2).getNumericCellValue());
				attendanceList.add(s);
				logger.trace("Parsed StudentAttendance: {}", s);
			}
			attendanceRepo.saveAll(attendanceList);
			logger.info("Saved {} StudentAttendance records", attendanceList.size());

			// Sports Sheet
			logger.debug("Reading Sports sheet...");
			Sheet sportsSheet = workbook.getSheet("Sports");
			List<StudentSports> sportsList = new ArrayList<>();
			iterator = sportsSheet.iterator();
			iterator.next();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				StudentSports s = new StudentSports();
				s.setStudentId((int) row.getCell(0).getNumericCellValue());
				s.setSportName(row.getCell(1).getStringCellValue());
				s.setLevel(row.getCell(2).getStringCellValue());
				s.setAchievements(row.getCell(3).getStringCellValue());
				sportsList.add(s);
				logger.trace("Parsed StudentSports: {}", s);
			}
			sportsRepo.saveAll(sportsList);
			logger.info("Saved {} StudentSports records", sportsList.size());

			logger.info("Excel file processing completed successfully.");

		} catch (Exception e) {
			logger.error("Failed to parse and save Excel data: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to parse and save Excel data: " + e.getMessage(), e);
		}
	}

	@Override
	public StudentPersonal saveStudentPersonal(StudentPersonal personal) {
		logger.info("Saving StudentPersonal with ID {}", personal.getStudentId());
		return personalRepo.save(personal);
	}

	@Override
	public StudentAcademic saveStudentAcademic(StudentAcademic academic) {
		logger.info("Saving StudentAcademic with ID {}", academic.getStudentId());
		return academicRepo.save(academic);
	}

	@Override
	public StudentAttendance saveStudentAttendance(StudentAttendance attendance) {
		logger.info("Saving StudentAttendance with ID {}", attendance.getStudentId());
		return attendanceRepo.save(attendance);
	}

	@Override
	public StudentSports saveStudentSports(StudentSports sports) {
		logger.info("Saving StudentSports with ID {}", sports.getStudentId());
		return sportsRepo.save(sports);
	}

	@Override
	public List<StudentPersonal> getAllStudentPersonal() {
		logger.info("Fetching all StudentPersonal records");
		return personalRepo.findAll();
	}

	@Override
	public StudentPersonal getStudentPersonalById(int id) {
		logger.info("Fetching StudentPersonal by ID {}", id);
		return personalRepo.findById(id).orElse(null);
	}

	@Override
	public StudentAcademic getStudentAcademicById(int id) {
		logger.info("Fetching StudentAcademic by ID {}", id);
		return academicRepo.findById(id).orElse(null);
	}

	@Override
	public StudentAttendance getStudentAttendanceById(int id) {
		logger.info("Fetching StudentAttendance by ID {}", id);
		return attendanceRepo.findById(id).orElse(null);
	}

	@Override
	public StudentSports getStudentSportsById(int id) {
		logger.info("Fetching StudentSports by ID {}", id);
		return sportsRepo.findById(id).orElse(null);
	}

	@Override
	public List<StudentFullInfoDTO> getFullInfo() {
		logger.info("Fetching full student info via joinRepo");
		List<Object[]> rows = joinRepo.fetchFullStudentInfoNative();

		List<StudentFullInfoDTO> result = new ArrayList<>();
		for (Object[] row : rows) {
			StudentFullInfoDTO dto = new StudentFullInfoDTO(
					(Integer) row[0],
					(String) row[1],
					(String) row[2],
					(String) row[3],
					(String) row[4],
					(String) row[5],
					(String) row[6],
					(Double) row[7],
					(Integer) row[8],
					(Integer) row[9],
					(String) row[10],
					(String) row[11]
					);
			result.add(dto);
			logger.trace("Constructed StudentFullInfoDTO: {}", dto);
		}

		logger.info("Fetched {} full student records", result.size());
		return result;
	}

	@Override
	public byte[] generateStudentReportPDF(int studentId) {
		logger.info("Generating PDF report for student ID {}", studentId);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			StudentPersonal personal = getStudentPersonalById(studentId);
			StudentAcademic academic = getStudentAcademicById(studentId);
			StudentAttendance attendance = getStudentAttendanceById(studentId);
			StudentSports sports = getStudentSportsById(studentId);
			Object obj =  (Object) personal;
			if(obj.equals(null)) {
				return null;
			} else{
				Document document = new Document();
				PdfWriter.getInstance(document, byteArrayOutputStream);
				document.open();

				document.add(new Paragraph("Student Report"));
				document.add(new Paragraph("---------------------------------------------------"));

				if (personal != null) {
					logger.debug("Adding personal info to PDF for student ID {}", studentId);
					document.add(new Paragraph("Personal Info:"));
					document.add(new Paragraph("ID: " + personal.getStudentId()));
					document.add(new Paragraph("Name: " + personal.getFirstName() + " " + personal.getLastName()));
					document.add(new Paragraph("Gender: "+personal.getGender()));
					document.add(new Paragraph("Email: "+personal.getEmail()));
					document.add(new Paragraph("DOB: " + personal.getDob()));

					document.add(new Paragraph(" "));
				} else {
					logger.warn("No personal info found for student ID {}", studentId);
				}

				if (academic != null) {
					logger.debug("Adding academic info to PDF for student ID {}", studentId);
					document.add(new Paragraph("Academic Info:"));
					document.add(new Paragraph("Department: " + academic.getDepartment()));
					document.add(new Paragraph("Average Marks: " + academic.getAverageMarks()));
					document.add(new Paragraph(" "));
				} else {
					logger.warn("No academic info found for student ID {}", studentId);
				}

				if (attendance != null) {
					logger.debug("Adding attendance info to PDF for student ID {}", studentId);
					document.add(new Paragraph("Attendance Info:"));
					document.add(new Paragraph("Attended Classes: " + attendance.getAttendedClasses()));
					document.add(new Paragraph("Total Classes: " + attendance.getTotalClasses()));
					document.add(new Paragraph(" "));
				} else {
					logger.warn("No attendance info found for student ID {}", studentId);
				}

				if (sports != null) {
					logger.debug("Adding sports info to PDF for student ID {}", studentId);
					document.add(new Paragraph("Sports Info:"));
					document.add(new Paragraph("Sport: " + sports.getSportName()));
					document.add(new Paragraph("Level: " + sports.getLevel()));
					document.add(new Paragraph("Achievements: " + sports.getAchievements()));
				} else {
					logger.warn("No sports info found for student ID {}", studentId);
				}

				document.close();
				logger.info("PDF report generated successfully for student ID {}", studentId);


				return byteArrayOutputStream.toByteArray();
			}
		} catch (Exception e) {
			logger.error("Error generating PDF report for student ID {}: {}", studentId, e.getMessage(), e);
			return null;
		}

	}
}
