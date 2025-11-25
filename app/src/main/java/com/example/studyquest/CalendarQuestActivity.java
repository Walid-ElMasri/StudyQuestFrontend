package com.example.studyquest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.studyquest.api.ApiService;
import com.example.studyquest.api.RetrofitClient;
import com.example.studyquest.models.QuestCompleteResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarQuestActivity extends AppCompatActivity {

    // Generate per-user SharedPreferences name
    private String getPrefsName() {
        return "calendar_tasks_" + username;
    }

    private static final String KEY_TASKS_MAP = "tasks_map";

    private CalendarView calendarView;
    private EditText editTask;
    private Button btnAddTask;
    private ListView listTasks;
    private Button btnFinish;

    private ApiService apiService;
    private int questId;
    private String username;
    private boolean questMode = false;

    private long selectedDate;
    private Map<Long, List<CalendarTask>> tasksByDate = new HashMap<>();
    private CalendarTaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_quest);

        // Find views
        calendarView = findViewById(R.id.calendarView);
        editTask = findViewById(R.id.editTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        listTasks = findViewById(R.id.listTasks);
        btnFinish = findViewById(R.id.btnFinishCalendarQuest);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // MUST load username BEFORE loadTasks()
        questId = getIntent().getIntExtra("questId", -1);
        username = getIntent().getStringExtra("username");

        questMode = questId != -1 && username != null && !username.trim().isEmpty();
        if (!questMode) btnFinish.setVisibility(View.GONE);

        // Now it's safe to load per-user tasks
        loadTasks();

        // Init selected date
        selectedDate = normalizeDate(calendarView.getDate());

        // Set up adapter
        adapter = new CalendarTaskAdapter(this, new ArrayList<>());
        listTasks.setAdapter(adapter);
        refreshTaskList();

        // Calendar styling
        styleCalendarColors();
        forceCalendarTextWhite();

        // Date change listener
        calendarView.setOnDateChangeListener((view, y, m, d) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(y, m, d, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            selectedDate = cal.getTimeInMillis();
            refreshTaskList();
            forceCalendarTextWhite();
        });

        // Add task
        btnAddTask.setOnClickListener(v -> {
            String title = editTask.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Write a task first.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CalendarTask> list = getTasksForSelectedDate();
            CalendarTask t = new CalendarTask();
            t.title = title;
            t.done = false;
            list.add(t);

            editTask.setText("");
            refreshTaskList();
            saveTasks();
        });

        // Finish quest
        btnFinish.setOnClickListener(v -> {
            if (!questMode) return;
            if (!hasAnyDoneTask()) {
                Toast.makeText(this, "Mark at least one task as done to finish.", Toast.LENGTH_SHORT).show();
                return;
            }
            completeQuestOnServer();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceCalendarTextWhite();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTasks();
    }

    // --------- DATE NORMALIZATION ---------

    private long normalizeDate(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // --------- CALENDAR COLORS ---------

    private void styleCalendarColors() {
        calendarView.setFocusedMonthDateColor(Color.WHITE);
        calendarView.setUnfocusedMonthDateColor(Color.WHITE);
        calendarView.setWeekSeparatorLineColor(Color.WHITE);
        calendarView.setSelectedWeekBackgroundColor(Color.parseColor("#AB30FF"));
    }

    // Force ALL text inside CalendarView to be white
    private void forceCalendarTextWhite() {
        calendarView.post(() -> {
            Typeface tf = null;
            try {
                tf = ResourcesCompat.getFont(this, R.font.jersey15_regular);
            } catch (Exception ignored) {}

            traverseCalendarViews(calendarView, tf);
        });
    }

    private void traverseCalendarViews(View v, Typeface tf) {
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            tv.setTextColor(Color.WHITE);
            if (tf != null) tv.setTypeface(tf);

            CharSequence txt = tv.getText();
            if (txt != null && txt.toString().matches(".*\\d{4}.*")) {
                tv.setTextSize(26f); // month-year
            } else {
                tv.setTextSize(18f); // normal day numbers
            }

        } else if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) {
                traverseCalendarViews(g.getChildAt(i), tf);
            }
        }
    }

    // ---------- TASKS LOGIC ----------

    private List<CalendarTask> getTasksForSelectedDate() {
        List<CalendarTask> list = tasksByDate.get(selectedDate);
        if (list == null) {
            list = new ArrayList<>();
            tasksByDate.put(selectedDate, list);
        }
        return list;
    }

    private void refreshTaskList() {
        List<CalendarTask> list = getTasksForSelectedDate();
        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private boolean hasAnyDoneTask() {
        for (List<CalendarTask> list : tasksByDate.values())
            for (CalendarTask t : list)
                if (t.done) return true;
        return false;
    }

    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences(getPrefsName(), MODE_PRIVATE);
        prefs.edit().putString(KEY_TASKS_MAP, new Gson().toJson(tasksByDate)).apply();
    }

    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences(getPrefsName(), MODE_PRIVATE);
        String json = prefs.getString(KEY_TASKS_MAP, null);

        tasksByDate = new HashMap<>();
        if (json != null) {
            try {
                Type type = new TypeToken<Map<Long, List<CalendarTask>>>() {}.getType();
                Map<Long, List<CalendarTask>> loaded = new Gson().fromJson(json, type);

                if (loaded != null) {
                    for (Long key : loaded.keySet()) {
                        tasksByDate.put(normalizeDate(key), loaded.get(key));
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void completeQuestOnServer() {
        apiService.completeQuest(questId, username).enqueue(new Callback<QuestCompleteResponse>() {
            @Override
            public void onResponse(Call<QuestCompleteResponse> call, Response<QuestCompleteResponse> response) {
                Toast.makeText(CalendarQuestActivity.this,
                        response.isSuccessful() ? "Quest complete!" : "Could not complete quest.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuestCompleteResponse> call, Throwable t) {
                Toast.makeText(CalendarQuestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // ---------- INNER CLASSES ----------

    private static class CalendarTask {
        String title;
        boolean done;
    }

    private class CalendarTaskAdapter extends android.widget.ArrayAdapter<CalendarTask> {

        CalendarTaskAdapter(Context c, List<CalendarTask> tasks) {
            super(c, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CalendarTask task = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_calendar_task, parent, false);
            }

            TextView circle = convertView.findViewById(R.id.textCircle);
            TextView title = convertView.findViewById(R.id.textTaskTitle);

            title.setText(task.title);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20f);

            circle.setText(task.done ? "●" : "○");
            circle.setTextColor(task.done ? Color.parseColor("#6EC3FF") : Color.GRAY);

            View.OnClickListener toggle = v -> {
                task.done = !task.done;
                notifyDataSetChanged();
                saveTasks();
            };

            circle.setOnClickListener(toggle);
            convertView.setOnClickListener(toggle);

            return convertView;
        }
    }
}
