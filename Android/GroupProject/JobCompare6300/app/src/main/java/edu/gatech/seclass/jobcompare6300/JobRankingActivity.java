package edu.gatech.seclass.jobcompare6300;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JobRankingActivity extends AppCompatActivity {
    static List<Job> jobList = new ArrayList<>();
    private List<Integer> selectedJobs = new ArrayList<>(); // Size should never be more than 2
    private final String unselectedButtonColor = "#5f5fc4";
    private final String selectedButtonColor = "#001064";
    private DBHelper dbHelper = new DBHelper(JobRankingActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_ranking);

        populateJobList();

        // Inspiration from:
        // https://stackoverflow.com/questions/18207470/adding-table-rows-dynamically-in-android
        TableLayout table = findViewById(R.id.rankingTableID);
        TableRow.LayoutParams itemParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1f);

        int index = 0;
        for (Job job : jobList) {
            TableRow row = new TableRow(this);
            table.addView(row);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1f);
            row.setPadding(36, 20, 36, 20);
            row.setLayoutParams(layoutParams);

            TextView title = new TextView(this);
            title.setText(job.getTitle());
            title.setTextSize(16);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            title.setLayoutParams(itemParams);
            title.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(title);

            TextView company = new TextView(this);
            company.setText(job.getCompany());
            company.setTextSize(16);
            company.setTextColor(Color.parseColor("#FFFFFF"));
            company.setLayoutParams(itemParams);
            company.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(company);

            Button selectButton = new Button(this);
            selectButton.setText("Select");
            setButtonColor(unselectedButtonColor, selectButton);
            selectButton.setTextColor(Color.parseColor("#FFFFFF"));
            selectButton.setId(index);
            index++;
            selectButton.setOnClickListener(selectJob(selectButton));
            row.addView(selectButton);
            selectButton.setLayoutParams(new TableRow.LayoutParams(340, 140));

            if (job.isCurrentJob()) {
                title.setText(job.getTitle() + " (Current)");
                row.setBackgroundResource(R.drawable.borders);
            }
        }
    }

    public void openMainMenu(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


    // Create an onclick handler for the buttons. Found on: https://stackoverflow.com/a/4458450
    View.OnClickListener selectJob(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                // If the current button text is "Select", we know it's not currently selected.
                // Change the button text and background color, and add the button ID (which uniquely
                // identifies a job) to the selected job list.
                if (button.getText() == "Select") {
                    button.setText("Selected");
                    setButtonColor(selectedButtonColor, button);
                    selectedJobs.add(button.getId());

                    // If two jobs are selected, open the comparison table screen.
                    if (selectedJobs.size() == 2) {
                        openComparisonTable();
                    }
                } else {
                    // Otherwise, revert the button text and color and remove from the selected job list.
                    button.setText("Select");
                    setButtonColor(unselectedButtonColor, button);
                    selectedJobs.remove(Integer.valueOf(button.getId()));
                }
            }
        };
    }

    private void openComparisonTable() {
        Intent intent = new Intent(this, ComparisonTableActivity.class).putExtra("job1ID", selectedJobs.get(0)).putExtra("job2ID", selectedJobs.get(1));
        startActivity(intent);
    }

    private void setButtonColor(String hexCode, Button button) {
        // Found on: https://www.codegrepper.com/code-examples/java/how+to+change+background+tint+color+programmatically+android
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.parseColor(hexCode));
        button.setBackground(buttonDrawable);
    }

    // get job offers from db, recalculate score based on the latest weights, and sort the list.
    private void populateJobList() {
        jobList = dbHelper.getOfferRecord();
        ComparisonSettings settings = dbHelper.getSetting();
        for(Job job : jobList){ dbHelper.updateScore(job, settings); }
        jobList = dbHelper.getOfferRecord();
        jobList.sort(Comparator.comparing(Job::getScore).reversed());
    }
}