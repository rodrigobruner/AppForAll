package app.bruner.appforall.ui.addGrade;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import app.bruner.appforall.R;
import app.bruner.appforall.data.Courses;
import app.bruner.appforall.data.GradeModel;
import app.bruner.appforall.databinding.FragmentAddGradeBinding;
import app.bruner.appforall.viewModel.GradeViewModel;

public class AddGradeFragment extends Fragment {

    //Tag for debug in log
    private static String TAG = "AddGradeFragment";

    //Binding
    private FragmentAddGradeBinding binding;

    //Adapter and list of course to set up the listView
    private ArrayAdapter<String> adapterCourses;
    private String[] courses = Courses.COURSES; //Get from the class courses

    //Stings to save options selected
    private String selectedCourse, selectedCredit;
    private int gradeId; //Used when edit a grade

    // the layer that link View and repository
    private GradeViewModel gradeViewModel;

    //Widgets used in the interface
    private EditText editTextFirstName, editTextLastName, editTextMark;
    private ListView listViewCourse;
    private RadioGroup radioGroupCredits;
    private Button buttonSave;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gradeViewModel = new ViewModelProvider(this).get(GradeViewModel.class);

        binding = FragmentAddGradeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Get the widgets from layout
        editTextFirstName = binding.editTextFirstName;
        editTextLastName = binding.editTextLastName;
        editTextMark = binding.editTextMark;
        listViewCourse = binding.listViewCourse;
        radioGroupCredits = binding.radioGroupCredits;
        buttonSave = binding.buttonSave;

        //set up the ListView
        //Create the adapter
        adapterCourses = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_single_choice, courses);
        listViewCourse.setAdapter(adapterCourses); //Add adapter
        //Enable radio in the list view, just to show witch is selected
        listViewCourse.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //When is clicked in a option
        listViewCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse = courses[i]; // set selected course
            }
        });

        //Listen the radios for changes
        radioGroupCredits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null) {
                    //When change set selected
                    selectedCredit = radioButton.getText().toString();
                }
            }
        });



        //Get parameters passed between fragments
        Bundle bundle = getArguments();
        if (bundle != null) { //If have value
            GradeModel grade = (GradeModel) bundle.getSerializable("GRADE"); //Get grade
            if (grade != null) {
                gradeId = grade.getId(); //Set ID
                //Set fields
                editTextFirstName.setText(grade.getFirstName());
                editTextLastName.setText(grade.getLastName());
                editTextMark.setText(grade.getMarks());
                selectedCourse = grade.getCourse();
                selectCourse(grade.getCourse());
                selectedCredit = grade.getCredits();
                selectCredits(grade.getCredits());
            }

            getArguments().clear();
        }

        //Whe the button is pressed
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call the function save and pass the values of the fields
                save(   editTextFirstName.getText().toString(),
                        editTextLastName.getText().toString(),
                        selectedCourse,
                        selectedCredit,
                        editTextMark.getText().toString());
            }
        });

        return root;
    }

    //Function to select the course when edit
    private void selectCourse(String content) {
        // browse through the list views
        for (int i = 0; i < adapterCourses.getCount(); i++) {
            //check that the value of the list view is the same as that of the content
            if (adapterCourses.getItem(i).equals(content)) {
                listViewCourse.setItemChecked(i, true); //Set the radio button
                listViewCourse.setSelection(i); //Set as selected
                break; //Stop loop
            }
        }
    }

    //Function to select the credits when edit
    private void selectCredits(String option) {
        // browse through the radios in the radio group
        for (int i = 0; i < radioGroupCredits.getChildCount(); i++) {
            View radio = radioGroupCredits.getChildAt(i); //Get radio as View
            RadioButton radioButton = (RadioButton) radio; //Cast the view in a RadioButton
            if (radioButton.getText().toString().equals(option)) { //Check if the content of the radio match the option
                radioButton.setChecked(true); //Check the radio
                break; //Stop loop
            }
        }
    }

    private void clearForm() {
        Toast.makeText(getContext(),"OK", Toast.LENGTH_SHORT);
        editTextFirstName.setText("");
        editTextLastName.setText("");
        editTextMark.setText("");
        listViewCourse.clearChoices();
        radioGroupCredits.clearCheck();
        selectedCourse = null;
        selectedCredit = null;
        gradeId = 0;
    }


    //Function to save data in db
    private void save(String firstName, String lastName, String course, String credits, String marks) {

        //Validate th fields
        if (firstName.isEmpty()) {
            //Show a message for the user
            Toast.makeText(requireContext(), getString(R.string.form_validation_first_name), Toast.LENGTH_SHORT).show();
            return; //abort this function
        }

        if (lastName.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.form_validation_last_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (course == null) {
            Toast.makeText(requireContext(), getString(R.string.form_validation_course), Toast.LENGTH_SHORT).show();
            return;
        }

        if (credits == null) {
            Toast.makeText(requireContext(), getString(R.string.form_validation_credits), Toast.LENGTH_SHORT).show();
            return;
        }

        if (marks == null) {
            Toast.makeText(requireContext(), getString(R.string.form_validation_marks), Toast.LENGTH_SHORT).show();
            marks = "0"; //I consider that the mark will be 0
        }

        //Create new model
        GradeModel gradeModel = new GradeModel(firstName, lastName, course, credits, marks);

        try {

            if (gradeId > 0) { //is a edition?
                gradeModel.setId(gradeId); //set the id
                gradeViewModel.update(gradeModel); //Update in db
                Toast.makeText(requireContext(), getString(R.string.msg_update_success), Toast.LENGTH_LONG).show(); //Show a message for the user
                return;
            }
            //Save data in db
            gradeViewModel.insert(gradeModel);
            Toast.makeText(requireContext(), getString(R.string.msg_save_success), Toast.LENGTH_LONG).show(); //Show a message for the user
        } catch (Exception e){ //If some error occur
            Log.i(TAG, e.getMessage()); //Show on log
            if (gradeId > 0) { //If is a edition
                Toast.makeText(requireContext(), getString(R.string.msg_update_error), Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(requireContext(), getString(R.string.msg_save_error), Toast.LENGTH_LONG).show();
        }
        clearForm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        /* I clear the form when it is in the background,
        so if it returns to the add grade screen, the form is empty.*/
        clearForm();
    }
}