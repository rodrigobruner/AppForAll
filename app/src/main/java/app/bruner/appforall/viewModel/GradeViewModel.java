package app.bruner.appforall.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import app.bruner.appforall.data.GradeModel;
import app.bruner.appforall.repo.GradeRepository;

public class GradeViewModel extends AndroidViewModel {

    // Grade repository instance
    private GradeRepository repository;

    //All the grades are present
    private LiveData<List<GradeModel>> allGredes;

    public GradeViewModel(@NonNull Application application) {
        super(application);

        repository = new GradeRepository(application);
        allGredes = repository.getAllGrades();
    }

    // below method is use to insert the data to our repository.
    public void insert(GradeModel model) {
        repository.insert(model);
    }

    // below line is to update data in our repository.
    public void update(GradeModel model) {
        repository.update(model);
    }

    // below line is to delete the data in our repository.
    public void delete(GradeModel model) {
        repository.delete(model);
    }

    // below method is to delete all the grades
    public void deleteAllCourses() {
        repository.deleteAllGrades();
    }

    // below method is to get all the grades
    public LiveData<List<GradeModel>> getAllGrades() {
        return allGredes;
    }

    //  below method is to get the grade by id
    public LiveData<List<GradeModel>> getByID(String id) {
        return repository.getById(id);
    }

    //  below method is to get the grade by course
    public LiveData<List<GradeModel>> getByCourse(String course) {
        return repository.getByCourse(course);
    }
}
