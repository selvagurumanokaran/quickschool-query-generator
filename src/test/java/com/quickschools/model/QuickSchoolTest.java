package com.quickschools.model;

import com.quickschools.model.service.SQLGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class QuickSchoolTest {


    SQLGenerator subject = new SQLGenerator();

    @Test
    public void testGeneratingWithDuplicate() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");

        Student student1 = new Student(1, "John", Gender.MALE);
        Field<Student> studentID1 = new Field<>(student1, "id");

        Grade grade = new Grade(3, "3rd Grade");
        Field<Grade> gradeID = new Field<>(grade, "id");

        Join<Field<Student>, Field<Grade>> join = new Join<>(studentID, gradeID);

        Join<Field<Student>, Field<Grade>> join1 = new Join<>(studentID1, gradeID);


        String generatedSql = subject.generate(Arrays.asList(studentID, gradeID, studentName, studentID1), Arrays.asList(join, join1));
        assertEquals(generatedSql, "SELECT Student.id, Student.name, Grade.id FROM Student, Grade WHERE Student.id = Grade.id;");
    }

    @Test
    public void testSingleTable() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");
        String generatedSql = subject.generate(Arrays.asList(studentID, studentName), Collections.emptyList());
        assertEquals(generatedSql, "SELECT Student.id, Student.name FROM Student;");
    }

}
