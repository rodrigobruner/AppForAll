package app.bruner.appforall.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//This annotation define a room database with Grade model in version 1
@Database(entities = {GradeModel.class}, version = 2)
public abstract class GradeDatabase extends RoomDatabase {
    //A instance of database(singleton)
    private static volatile GradeDatabase instance;
    //Number of threads for the service
    private static final int NUMBER_OF_THREADS = 4;
    //Create a Executor Service to run the db activities in another thread(background), not in main thread
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    //Get DAO
    public abstract GradeDao gradeDao();

    //Get the singlenton instance
    public static GradeDatabase getInstance(final Context context) {
        if (instance == null) { //If on instance create one
            synchronized (GradeDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    GradeDatabase.class, "grade_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback) //Callback to populate DB when it is created
                            .build();
                }
            }
        }
        return instance;
    }

    //Callback to populate DB when it is created
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Execute db operations in background
            databaseWriteExecutor.execute(() -> {
                GradeDao dao = instance.gradeDao();
            });
        }
    };
}