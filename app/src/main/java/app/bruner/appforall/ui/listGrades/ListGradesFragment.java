package app.bruner.appforall.ui.listGrades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.bruner.appforall.R;
import app.bruner.appforall.adapter.GradesRecyclerViewAdapter;
import app.bruner.appforall.data.GradeModel;
import app.bruner.appforall.databinding.FragmentListGradesBinding;
import app.bruner.appforall.ui.addGrade.AddGradeFragment;
import app.bruner.appforall.viewModel.GradeViewModel;

public class ListGradesFragment extends Fragment {
    //Tag for debug in log
    private static String TAG = "ListGradesFragment";

    //biding
    private FragmentListGradesBinding binding;

    //Set up the variables to use in the RecyclerView
    private RecyclerView recyclerViewGrades;
    private GradeViewModel gradeViewModel;
    private GradesRecyclerViewAdapter gradesRecyclerViewAdapter;

    //Get the textView
    private TextView textViewMsg;

    //Create a executor and mainHandler for run query in background
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gradeViewModel = new ViewModelProvider(this).get(GradeViewModel.class);

        binding = FragmentListGradesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Get space for message and hide it
        textViewMsg = binding.textViewMsg;
        textViewMsg.setVisibility(View.GONE);

        //Get the RecyclerView
        recyclerViewGrades = binding.recyclerViewGrades;

        //Call function to generate the list
        listAll(root);

        return root;
    }


    //Run in background the query
    private void listAll(View root) {
        executor.submit(() -> {

            // Create new adapter
            gradesRecyclerViewAdapter = new GradesRecyclerViewAdapter();
            LiveData<List<GradeModel>> gradeList = gradeViewModel.getAllGrades();

            // Come back to the main thread to update the UI
            mainHandler.post(() -> {
                recyclerViewGrades.setLayoutManager(new LinearLayoutManager(getContext())); // Set the layout
                recyclerViewGrades.setAdapter(gradesRecyclerViewAdapter); // Set adapter

                // Get all grades and set on RecyclerView
                gradeList.observe(getViewLifecycleOwner(), grades -> {
                    if (grades == null || grades.isEmpty()) {
                        textViewMsg.setText(R.string.msg_list_not_fund);
                        textViewMsg.setVisibility(View.VISIBLE);
                        return;
                    }
                    gradesRecyclerViewAdapter.submitList(grades);
                });

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
                        navController.navigate(R.id.action_listGradesFragment_to_addGradeFragment, bundle);
                    }
                });
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}