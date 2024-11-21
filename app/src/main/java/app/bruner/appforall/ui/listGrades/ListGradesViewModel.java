package app.bruner.appforall.ui.listGrades;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListGradesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ListGradesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is list grades fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}