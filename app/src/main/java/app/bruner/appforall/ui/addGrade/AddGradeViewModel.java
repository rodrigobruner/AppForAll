package app.bruner.appforall.ui.addGrade;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddGradeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddGradeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add grade fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}