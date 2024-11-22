package app.bruner.appforall.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.bruner.appforall.R;
import app.bruner.appforall.adapter.GradesRecyclerViewAdapter;
import app.bruner.appforall.data.Courses;
import app.bruner.appforall.data.GradeModel;
import app.bruner.appforall.databinding.FragmentSearchBinding;
import app.bruner.appforall.viewModel.GradeViewModel;

public class SearchFragment extends Fragment {

    //Tag for debug in log
    private static String TAG = "SearchFragment";

    //Binding
    private FragmentSearchBinding binding;

    //Adapter and list of course to set up the listView
    private ArrayAdapter<String> adapterCourses;
    private String[] courses = Courses.COURSES;

    //Widgets used in the interface
    private RadioGroup radioGroupSearch;
    private EditText editTextSearchId;
    private ListView listViewSearchCourse;
    private Button buttonSearch;
    private TextView textViewMessages;

    //Stings to save options selected
    private String selectedOption, selectedCourse;

    //Set up the variables to use in the RecyclerView
    private RecyclerView recyclerViewGrades;
    private GradeViewModel gradeViewModel;
    private GradesRecyclerViewAdapter gradesRecyclerViewAdapter;

    //Create a executor and mainHandler for run query in background
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicialize o GradeViewModel
        gradeViewModel = new ViewModelProvider(this).get(GradeViewModel.class);

        //Get the widgets from layout
        radioGroupSearch = binding.radioGroupSearch;
        editTextSearchId = binding.editTextSearchId;
        listViewSearchCourse = binding.listViewSearchCourse;
        buttonSearch = binding.buttonSearch;
        textViewMessages = binding.textViewMessages;
        recyclerViewGrades = binding.recyclerViewGrades;

        //Hidde this textView
        textViewMessages.setVisibility(View.GONE);



        //set up the ListView
        //Create the adapter
        adapterCourses = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_single_choice, courses);
        listViewSearchCourse.setAdapter(adapterCourses);//Add adapter
        //Enable radio in the list view, just to show witch is selected
        listViewSearchCourse.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //When is clicked in a option
        listViewSearchCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse = courses[i]; // set selected course
            }
        });

        // hidden the fields
        initialSettings();

        // When a radio is selected
        radioGroupSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                initialSettings(); //hidde fields

                // get selected radio
                RadioButton selectedRadioButton = root.findViewById(i);

                //  If the widget is not found (when clearing the radio group it will be null)
                if (selectedRadioButton == null) {
                    return;
                }

                // If ID selected
                if (selectedRadioButton.getText().toString().equals(getString(R.string.search_id))) {
                    // Show editText and button
                    editTextSearchId.setVisibility(View.VISIBLE);
                    buttonSearch.setVisibility(View.VISIBLE);
                }

                // If course selected
                if (selectedRadioButton.getText().toString().equals(getString(R.string.search_course))) {
                    // Show listView and button
                    listViewSearchCourse.setVisibility(View.VISIBLE);
                    buttonSearch.setVisibility(View.VISIBLE);
                }

                //Set selected option
                selectedOption = selectedRadioButton.getText().toString();
            }
        });

        // When the button is pressed
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(root); //Call function search
            }
        });

        return root;
    }

    //Set fields
    public void initialSettings() {
        // Hidden fields
        editTextSearchId.setVisibility(View.GONE);
        listViewSearchCourse.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.GONE);
        textViewMessages.setVisibility(View.GONE);
    }


    //Run in background the query
    private void search(View root) {
        executor.submit(() -> {
            // Create new adapter
            gradesRecyclerViewAdapter = new GradesRecyclerViewAdapter();
            LiveData<List<GradeModel>> gradeList = null;

            // If search by id
            if (selectedOption.equals(getString(R.string.search_id))) {
                // Call get by id and set the result on RecyclerView
                gradeList = gradeViewModel.getByID(editTextSearchId.getText().toString());
            }

            // If search by course
            if (selectedOption.equals(getString(R.string.search_course))) {
                // Call get by course and set the result on RecyclerView
                gradeList = gradeViewModel.getByCourse(selectedCourse);
            }

            // Come back to the main thread to update the UI
            LiveData<List<GradeModel>> finalGradeList = gradeList;
            mainHandler.post(() -> {
                recyclerViewGrades.setLayoutManager(new LinearLayoutManager(getContext())); // Set the layout
                recyclerViewGrades.setAdapter(gradesRecyclerViewAdapter); // Set adapter

                if (finalGradeList != null) {
                    // Get all grades and set on RecyclerView
                    finalGradeList.observe(getViewLifecycleOwner(), grades -> {
                        if (grades == null || grades.isEmpty()) {
                            textViewMessages.setText(R.string.msg_search_not_found);
                            textViewMessages.setVisibility(View.VISIBLE);
                            return;
                        }
                        textViewMessages.setVisibility(View.GONE);
                        gradesRecyclerViewAdapter.submitList(grades);
                    });
                }

                // Swipe to delete, is request implement a ItemTouchHelper
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        try {
                            // Get the model
                            GradeModel grade = gradesRecyclerViewAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                            // Through the Model view, tell the bank to delete the record
                            gradeViewModel.delete(grade);
                            // Show a message for the user
                            Toast.makeText(getContext(), getString(R.string.msg_delete_success), Toast.LENGTH_LONG).show();
                        } catch (Exception e) { // If some error occur
                            Toast.makeText(getContext(), getString(R.string.msg_delete_error), Toast.LENGTH_LONG).show();
                            Log.i(TAG, e.getMessage());
                        }
                    }
                }).attachToRecyclerView(recyclerViewGrades);

                gradesRecyclerViewAdapter.setOnItemClickListener(new GradesRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(GradeModel grade) {
                        // Create new Bundle to pass parameter to other fragment
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GRADE", grade); // Set the GRADE parameter with grade model
                        NavController navController = Navigation.findNavController(root); // Get navigation controller
                        // Change fragment
                        navController.navigate(R.id.action_searchFragment_to_addGradeFragment, bundle);
                    }
                });

                // Reset all radio in the radio group
                radioGroupSearch.clearCheck();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}