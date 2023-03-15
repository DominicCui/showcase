package edu.gatech.seclass.jobcompare6300;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ComparisonTableActivity extends AppCompatActivity {
    private DBHelper dbHelper = new DBHelper(ComparisonTableActivity.this);
    private boolean useCurrentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison_table);

        int job1ID = getIntent().getIntExtra("job1ID", 0);
        int job2ID = getIntent().getIntExtra("job2ID", 1);
        useCurrentJob = getIntent().getBooleanExtra("useCurrent", false);

        Job job1;
        Job job2;

        if (useCurrentJob) {
            job1 = dbHelper.getCurrentJob();
            job2 = dbHelper.getMostRecentlyAddedJob();
        } else {
            job1 = JobRankingActivity.jobList.get(job1ID);
            job2 = JobRankingActivity.jobList.get(job2ID);
        }

        // Populate the table with job details.
        TextView titleJob1 = findViewById(R.id.titleJob1);
        TextView titleJob2 = findViewById(R.id.titleJob2);
        titleJob1.setText(job1.getTitle());
        titleJob2.setText(job2.getTitle());

        TextView companyJob1 = findViewById(R.id.companyJob1);
        TextView companyJob2 = findViewById(R.id.companyJob2);
        companyJob1.setText(job1.getCompany());
        companyJob2.setText(job2.getCompany());

        TextView locationJob1 = findViewById(R.id.locationJob1);
        TextView locationJob2 = findViewById(R.id.locationJob2);
        locationJob1.setText(job1.getLocation());
        locationJob2.setText(job2.getLocation());

        TextView salaryJob1 = findViewById(R.id.salaryJob1);
        TextView salaryJob2 = findViewById(R.id.salaryJob2);
        salaryJob1.setText(Double.toString(job1.getYearlySalary()));
        salaryJob2.setText(Double.toString(job2.getYearlySalary()));

        TextView bonusJob1 = findViewById(R.id.bonusJob1);
        TextView bonusJob2 = findViewById(R.id.bonusJob2);
        bonusJob1.setText(Double.toString(job1.getYearlyBonus()));
        bonusJob2.setText(Double.toString(job2.getYearlyBonus()));

        TextView teleworkJob1 = findViewById(R.id.teleworkJob1);
        TextView teleworkJob2 = findViewById(R.id.teleworkJob2);
        teleworkJob1.setText(Integer.toString(job1.getRemoteDays()));
        teleworkJob2.setText(Integer.toString(job2.getRemoteDays()));

        TextView leaveJob1 = findViewById(R.id.leaveJob1);
        TextView leaveJob2 = findViewById(R.id.leaveJob2);
        leaveJob1.setText(Integer.toString(job1.getLeaveDays()));
        leaveJob2.setText(Integer.toString(job2.getLeaveDays()));

        TextView equityJob1 = findViewById(R.id.equityJob1);
        TextView equityJob2 = findViewById(R.id.equityJob2);
        equityJob1.setText(Integer.toString(job1.getNumberOfShares()));
        equityJob2.setText(Integer.toString(job2.getNumberOfShares()));
    }

    public void openMainMenu(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    // Opens the job ranking screen.
    public void openJobRankings(View view) {
        Intent intent;
        if (!useCurrentJob) {
            intent = new Intent(this, JobRankingActivity.class);
        } else {
            // This is the back button, so if you're on the compare with current job, back should take you to the job saved screen.
            intent = new Intent(this, JobSavedActivity.class);
        }
        startActivity(intent);
    }
}