package app.bruner.appforall.data;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

//Define DAO interface
@androidx.room.Dao
public interface GradeDao {

    //Add grade to database
    @Insert
    void insert(GradeModel grade);

    //Update grade in the database
    @Update
    void update(GradeModel grade);

    //Delete specific grade in the database
    @Delete
    void delete(GradeModel grade);

    //Delete all grades
    @Query("DELETE FROM grades")
    void deleteAllGrades();

    //Select all from gredes
    @Query("SELECT * FROM grades")
    LiveData<List<GradeModel>> getAllGrades();

    // Select the grade that id equal :id (function parameter id)
    @Query("SELECT * FROM grades WHERE id=:id")
    LiveData<List<GradeModel>> getById(String id);

    @Query("SELECT * FROM grades WHERE course = :course")
    LiveData<List<GradeModel>> getByCourse(String course);
}