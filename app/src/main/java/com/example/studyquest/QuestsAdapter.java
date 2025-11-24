package com.example.studyquest;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.studyquest.models.Quest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class QuestsAdapter extends ArrayAdapter<Quest> {

    private final String username;

    public QuestsAdapter(Context context, List<Quest> quests, String username) {
        super(context, 0, quests);
        this.username = username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Quest q = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_quest, parent, false);
        }

        TextView title   = convertView.findViewById(R.id.textQuestTitle);
        TextView desc    = convertView.findViewById(R.id.textQuestDesc);
        TextView xp      = convertView.findViewById(R.id.textQuestXp);
        ProgressBar progress = convertView.findViewById(R.id.questProgressBar);
        TextView percent = convertView.findViewById(R.id.textQuestPercent);

        if (q != null) {
            title.setText(q.name);
            desc.setText(q.description);
            xp.setText("+" + q.xp_reward + " XP");

            int progressValue = computeProgress(q);
            progress.setProgress(progressValue);
            percent.setText(progressValue + "%");
        }

        float density = getContext().getResources().getDisplayMetrics().density;
        int extraBottomPx = (int) (24 * density);

        convertView.setPadding(
                convertView.getPaddingLeft(),
                convertView.getPaddingTop(),
                convertView.getPaddingRight(),
                extraBottomPx
        );

        return convertView;
    }

    /**
     * Computes progress percent for each quest.
     * - Flashcards: based on answeredCount / totalCards (per user + quest).
     * - Others: 0% or 100% from q.completed.
     */
    private int computeProgress(Quest q) {
        if (q == null) return 0;

        // Non-flashcard quests: just 0 or 100
        if (q.quest_type == null || !q.quest_type.equals("flashcards")) {
            return q.completed ? 100 : 0;
        }

        // Flashcard quests: read from SharedPreferences
        SharedPreferences prefs = getContext()
                .getSharedPreferences("flashcard_progress", Context.MODE_PRIVATE);

        String prefix = "user_" + username + "_quest_" + q.id + "_";

        int answeredCount = prefs.getInt(prefix + "answeredCount", 0);
        String flashcardsJson = prefs.getString(prefix + "flashcards", null);

        int total = 0;
        if (flashcardsJson != null) {
            try {
                Type listType = new TypeToken<List<Object>>() {}.getType();
                List<Object> list = new Gson().fromJson(flashcardsJson, listType);
                if (list != null) {
                    total = list.size();
                }
            } catch (Exception ignored) {}
        }

        // If we can't read total from prefs, assume 10 (your default)
        if (total <= 0) {
            total = 10;
        }

        int percent = 0;
        if (total > 0) {
            percent = (int) Math.round((answeredCount * 100.0) / total);
        }

        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        // If backend marks quest as completed, force 100%
        if (q.completed) {
            percent = 100;
        }

        return percent;
    }
}
