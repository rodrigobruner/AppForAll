package app.bruner.appforall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import app.bruner.appforall.R;
import app.bruner.appforall.data.GradeModel;


public class GradesRecyclerViewAdapter extends ListAdapter<GradeModel, GradesRecyclerViewAdapter.ViewHolder> {

    // creating a variable for on item click listener.
    private OnItemClickListener listener;

    public GradesRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }

    public GradesRecyclerViewAdapter(@NonNull AsyncDifferConfig<GradeModel> config) {
        super(config);
    }

    public static final DiffUtil.ItemCallback<GradeModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GradeModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull GradeModel oldItem, @NonNull GradeModel newItem) {
                    // Compare unique IDs or other unique properties
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull GradeModel oldItem, @NonNull GradeModel newItem) {
                    // Compare all properties
                    return  oldItem.getFirstName().equals(newItem.getFirstName()) &&
                            oldItem.getLastName().equals(newItem.getLastName()) &&
                            oldItem.getCourse().equals(newItem.getCourse()) &&
                            oldItem.getCredits().equals(newItem.getCredits()) &&
                            oldItem.getMarks().equals(newItem.getMarks());
                }
            };

    @NonNull
    @Override
    public GradesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grade_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull GradesRecyclerViewAdapter.ViewHolder holder, int position) {
        GradeModel grade = getItem(position);

        //Get label from String.xml
        String labelId =  holder.itemView.getContext().getString(R.string.list_id);
        String labelCourse =  holder.itemView.getContext().getString(R.string.list_course);
        String labelCredits =  holder.itemView.getContext().getString(R.string.list_credits);
        String labelMark =  holder.itemView.getContext().getString(R.string.list_mark);

        //Set in R.layout.grade_item the text(label+value)
        holder.id.setText(labelId+" "+grade.getId());
        holder.firstName.setText(grade.getFirstName());
        holder.lastName.setText(grade.getLastName());
        holder.course.setText(labelCourse +" "+ grade.getCourse());
        holder.credits.setText(labelCredits +" "+ grade.getCredits());
        holder.marks.setText(labelMark +" "+ grade.getMarks());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Fields
        TextView id, firstName, lastName, course, credits, marks;

        //Constructor
        public ViewHolder(View view) {
            super(view);
            //Get fields
            id = view.findViewById(R.id.id);
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);
            course = view.findViewById(R.id.course);
            credits = view.findViewById(R.id.credits);
            marks = view.findViewById(R.id.marks);

            // Set up the on click event in the view
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GradeModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}