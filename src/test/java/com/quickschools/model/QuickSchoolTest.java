package com.quickschools.model;

import com.quickschools.model.service.SQLGenerator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class QuickSchoolTest {


    private SQLGenerator sqlGenerator = new SQLGenerator();


    @Test
    public void testGeneratingSql() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");


        Grade grade = new Grade(3, "3rd Grade");
        Field<Grade> gradeID = new Field<>(grade, "id");

        Join<Field<Student>, Field<Grade>> join = new Join<>(studentID, gradeID);

        String generatedSql = sqlGenerator.generate(
                Arrays.asList(studentID, gradeID, studentName),
                Collections.singletonList(join));
        assertEquals(generatedSql, "SELECT Student.id, Student.name, Grade.id " +
                "FROM Grade, Student WHERE Student.id = Grade.id;");
    }

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


        String generatedSql = sqlGenerator.generate(
                Arrays.asList(studentID, gradeID, studentName, studentID1),
                Arrays.asList(join, join1));
        assertEquals("SELECT Student.id, Student.name, Grade.id " +
                "FROM Grade, Student WHERE Student.id = Grade.id;", generatedSql);
    }

    @Test
    public void testWithSingleTable() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");
        String generatedSql = sqlGenerator.generate(Arrays.asList(studentID, studentName), Collections.emptyList());
        assertEquals("SELECT Student.id, Student.name FROM Student;", generatedSql);
    }

    @Test
    public void testWithMultipleTables() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");


        Grade grade = new Grade(3, "3rd Grade");
        Field<Grade> gradeID = new Field<>(grade, "id");
        Join<Field<Student>, Field<Grade>> studentGradeJoin = new Join<>(studentID, gradeID);

        Subject subject = new Subject(65, "Science");
        Field<Subject> subjectID = new Field<>(subject, "id");
        Field<Subject> subjectName = new Field<>(subject, "name");

        Join<Field<Subject>, Field<Grade>> subjectGradeJoin = new Join<>(subjectID, gradeID);

        String generatedSql = sqlGenerator.generate(
                Arrays.asList(studentID, studentName, gradeID, subjectID, subjectName),
                Arrays.asList(studentGradeJoin, subjectGradeJoin));
        assertEquals("SELECT Subject.name, Student.id, Student.name, Grade.id, Subject.id " +
                "FROM Grade, Student, Subject WHERE Student.id = Grade.id AND Subject.id = Grade.id;", generatedSql);
    }


    @Test
    public void testValidations() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(Collections.emptyList(), Collections.emptyList()));
        assertEquals("One or more fields required.", illegalArgumentException.getMessage());

        illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(null, Collections.emptyList()));
        assertEquals("One or more fields required.", illegalArgumentException.getMessage());

        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");
        illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(Arrays.asList(studentID, studentName), null));
        assertEquals("Parameter joins cannot be null.", illegalArgumentException.getMessage());
    }

    @Test
    public void testInvalidJoins() {
        Student student = new Student(1, "John", Gender.MALE);
        Field<Student> studentID = new Field<>(student, "id");
        Field<Student> studentName = new Field<>(student, "name");


        Grade grade = new Grade(3, "3rd Grade");
        Field<Grade> gradeID = new Field<>(grade, "id");
        Join<Field<Student>, Field<Grade>> studentGradeJoin = new Join<>(studentID, gradeID);

        Subject subject = new Subject(65, "Science");
        Field<Subject> subjectID = new Field<>(subject, "id");
        Field<Subject> subjectName = new Field<>(subject, "name");

        Join<Field<Subject>, Field<Grade>> subjectGradeJoin = new Join<>(subjectID, gradeID);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(
                        Arrays.asList(studentID, studentName, gradeID, subjectID, subjectName),
                        Collections.singletonList(studentGradeJoin)));
        assertEquals("One or more join is missing to form SQL.", illegalArgumentException.getMessage());


        illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(
                        Arrays.asList(studentID, studentName, gradeID, subjectID, subjectName),
                        Collections.emptyList()));
        assertEquals("One or more join is missing to form SQL.", illegalArgumentException.getMessage());

        illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> sqlGenerator.generate(
                        Arrays.asList(studentID, studentName, gradeID),
                        Arrays.asList(studentGradeJoin, subjectGradeJoin)));
        assertEquals("Invalid field Subject.id in join.", illegalArgumentException.getMessage());
    }

    private class Subject extends Entity {
        private int id;
        private String name;

        public Subject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Subject";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Subject subject = (Subject) o;

            return id != subject.id;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result;
            return result;
        }
    }

}
