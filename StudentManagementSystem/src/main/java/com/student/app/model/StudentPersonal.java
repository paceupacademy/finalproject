package com.student.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_personal")
public class StudentPersonal {

    @Id
    private int studentId;

    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String dob;

    public StudentPersonal() {}

    public StudentPersonal(int studentId, String firstName, String lastName, String email, String gender, String dob) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

}
