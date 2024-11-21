package app.bruner.appforall.repo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.bruner.appforall.data.GradeDao;
import app.bruner.appforall.data.GradeDatabase;
import app.bruner.appforall.data.GradeModel;

public class GradeRepository {
    //Dao instance
    private GradeDao gradeDao;
    //Numbers of threads in the pool
    private static final int NUMBER_OF_THREADS = 2;
    //Instance to execute the queries in background
    private final ExecutorService dbExecutor;

    //Constructor
    public GradeRepository(Application application) {
        GradeDatabase database = GradeDatabase.getInstance(application);
        //Set dao
        gradeDao = database.gradeDao();

        //Set executor service
        dbExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    //Function to insert grade
    public void insert(GradeModel model) {
        //run in background the insert command
        dbExecutor.execute(() -> gradeDao.insert(model));
    }

    //Function to update grade
    public void update(GradeModel model) {
        dbExecutor.execute(() -> gradeDao.update(model));
    }

    //Function to delete grade
    public void delete(GradeModel model) {
        dbExecutor.execute(() -> gradeDao.delete(model));
    }

    //Function to delete all grades
    public void deleteAllGrades() {
        dbExecutor.execute(() -> gradeDao.deleteAllGrades());
    }

    //Function to get all grades
    public LiveData<List<GradeModel>> getAllGrades() {
        return gradeDao.getAllGrades();
    }

    //Function to get by id
    public LiveData<List<GradeModel>> getById(String id) {
        return gradeDao.getById(id);
    }

    //Function to get by course code
    public LiveData<List<GradeModel>> getByCourse(String course) {
        return gradeDao.getByCourse(course);
    }
}