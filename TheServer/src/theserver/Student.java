package theserver;

public class Student {
    
    private String number;
    private String fullName;
    private String facultyCourseDegree;
    private String personalCode;

    public Student() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFacultyCourseDegree() {
        return facultyCourseDegree;
    }

    public void setFacultyCourseDegree(String facultyCourseDegree) {
        this.facultyCourseDegree = facultyCourseDegree;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    } 
}
