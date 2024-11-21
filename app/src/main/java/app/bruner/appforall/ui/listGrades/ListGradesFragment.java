package app.bruner.appforall.ui.listGrades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gradeViewModel = new ViewModelProvider(this).get(GradeViewModel.class);

        binding = FragmentListGradesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Get the RecyclerView
        recyclerViewGrades = binding.recyclerViewGrades;
        gradesRecyclerViewAdapter = new GradesRecyclerViewAdapter(); //Create new adapter
        recyclerViewGrades.setLayoutManager(new LinearLayoutManager(getContext())); //Set the layout
        recyclerViewGrades.setAdapter(gradesRecyclerViewAdapter); //set adapter

        //get all grades and set on RecyclerView
        gradeViewModel.getAllGrades().observe(getViewLifecycleOwner(), grades -> {
            gradesRecyclerViewAdapter.submitList(grades);
        });

        //swipe to delete, is request implement a ItemTouchHelper
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    //Get the the model
                    GradeModel grade = gradesRecyclerViewAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                    //Through the Model view, tell the bank to delete the record
                    gradeViewModel.delete(grade);
                    //Show a message for the user
                    Toast.makeText(getContext(), getString(R.string.msg_delete_success), Toast.LENGTH_LONG).show();
                } catch (Exception e){ //If some error occur
                    Toast.makeText(getContext(), getString(R.string.msg_delete_error), Toast.LENGTH_LONG).show();
                    Log.i(TAG, e.getMessage());
                }
            }
        }).attachToRecyclerView(recyclerViewGrades);

        gradesRecyclerViewAdapter.setOnItemClickListener(new GradesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GradeModel grade) {
                //Create new Bundle to pass parameter to other fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("GRADE", grade); //Set the GRADE parameter with grade model
                NavController navController = Navigation.findNavController(root); //Get navigation controller
                //Change fragment
                navController.navigate(R.id.action_listGradesFragment_to_addGradeFragment, bundle);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}