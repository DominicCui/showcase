package edu.gatech.seclass.jobcompare6300;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper = new DBHelper(MainActivity.this);
    private Button uiCompareJobsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiCompareJobsButton = findViewById(R.id.compareJobsButton);
        setCompareJobsButtonVisibility();
    }

    // Opens the job ranking screen.
    public void openJobRankings(View view) {
        Intent intent = new Intent(this, JobRankingActivity.class);
        startActivity(intent);
    }

    // Opens the current job view.
    public void openCurrentJob(View view) {
        Intent intent = new Intent(this, JobDetailsActivity.class).putExtra("isCurrentJob", true);
        startActivity(intent);
    }

    // Opens the new job offer view.
    public void openNewJobOffer(View view) {
        Intent intent = new Intent(this, JobDetailsActivity.class).putExtra("isCurrentJob", false);
        startActivity(intent);
    }

    // Opens the comparison settings view.
    public void openComparisonSettings(View view) {
        Intent intent = new Intent(this, ComparisonSettingsActivity.class);
        startActivity(intent);
    }

    private void setCompareJobsButtonVisibility(){
        uiCompareJobsButton.setEnabled(jobsForComparisonExist());
    }

    private boolean jobsForComparisonExist(){
        // at least 2 jobs need to exist for comparison per spec
        return dbHelper.getOfferRecord().size() >= 2;
    }
}

