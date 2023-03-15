package edu.gatech.seclass.jobcompare6300;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JobSavedActivity extends AppCompatActivity {
    private DBHelper dbHelper = new DBHelper(JobSavedActivity.this);
    private Button uiCompareWithCurrentJobButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_saved);

        uiCompareWithCurrentJobButton = findViewById(R.id.compareWithCurrentJobButton);
        setCompareWithCurrentJobButtonVisibility();
    }

    public void openMainMenu(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openNewJobOffer(View view) {
        Intent intent = new Intent(this, JobDetailsActivity.class);
        startActivity(intent);
    }

    public void compareWithCurrentJob(View view) {
        Intent intent = new Intent(this, ComparisonTableActivity.class).putExtra("useCurrent", true);
        startActivity(intent);

    }

    private void setCompareWithCurrentJobButtonVisibility() {
        uiCompareWithCurrentJobButton.setEnabled(currentJobExists());
    }

    private boolean currentJobExists(){
        return dbHelper.getCurrentJob() != null;
    }
}