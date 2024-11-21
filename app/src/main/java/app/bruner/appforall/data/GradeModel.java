package app.bruner.appforall.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "grades") //This annotation define table name
public class GradeModel implements Serializable {
    //ID is auto generate by db
    @PrimaryKey(autoGenerate = true)
    private int id;
    //First name
    private String firstName;
    //Last name
    private String lastName;
    //Course
    private String course;
    //Credits
    private String credits;
    //Marks
    private String marks;

    //Constructor
    public GradeModel(String firstName, String lastName, String course, String credits, String marks) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;
        this.credits = credits;
        this.marks = marks;
    }

    //Getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }
}
