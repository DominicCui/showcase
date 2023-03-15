package edu.gatech.seclass.jobcompare6300;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class JobDetailsActivity extends AppCompatActivity {

    // UI job details attributes
    private EditText uiJobTitle;
    private EditText uiJobCompany;
    private EditText uiJobLocation;
    private EditText uiJobLivingCost;
    private EditText uiJobYearlySalary;
    private EditText uiJobYearlyBonus;
    private EditText uiJobRemoteDays;
    private EditText uiJobLeaveDays;
    private EditText uiJobNumberOfShares;

    private boolean isCurrentJobScreen;
    private boolean isEditingCurrentJob;
    private DBHelper dbHelper = new DBHelper(JobDetailsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        uiJobTitle = findViewById(R.id.jobTitleID);
        uiJobCompany = findViewById(R.id.jobCompanyID);
        uiJobLocation = findViewById(R.id.jobLocationID);
        uiJobLivingCost = findViewById(R.id.jobLivingCostID);
        uiJobYearlySalary = findViewById(R.id.jobYearlySalaryID);
        uiJobYearlyBonus = findViewById(R.id.jobYearlyBonusID);
        uiJobRemoteDays = findViewById(R.id.jobRemoteDaysID);
        uiJobLeaveDays = findViewById(R.id.jobLeaveDaysID);
        uiJobNumberOfShares = findViewById(R.id.jobNumberOfSharesID);

        isCurrentJobScreen = getIntent().getBooleanExtra("isCurrentJob", false);
        isEditingCurrentJob = false;

        TextView title = findViewById(R.id.pageTitleID);
        if (!isCurrentJobScreen) {
            title.setText("Enter Job Offer");
        } else {
            updateCurrentJobUI();
        }
    }

    // Helper method called for both new job offers and current job creation.
    private Job createJobFromUI(boolean isCurrent) {
        String offerTitle = uiJobTitle.getText().toString();
        String offerCompany = uiJobCompany.getText().toString();
        String offerLocation = uiJobLocation.getText().toString();
        String offerLivingCostStr = uiJobLivingCost.getText().toString();
        String offerYearlySalaryStr = uiJobYearlySalary.getText().toString();
        String offerYearlyBonusStr = uiJobYearlyBonus.getText().toString();
        String offerRemoteDaysStr = uiJobRemoteDays.getText().toString();
        String offerLeaveDaysStr = uiJobLeaveDays.getText().toString();
        String offerNumberOfSharesStr = uiJobNumberOfShares.getText().toString();
        int offerLivingCost = 0;
        double offerYearlySalary = 0.0;
        double offerYearlyBonus = 0.0;
        int offerRemoteDays = 0;
        int offerLeaveDays = 0;
        int offerNumberOfShares = 0;
        CharSequence blankStringError = "Cannot be left blank";
        CharSequence offerLivingCostError = "Must be an integer > 0 (usually between 50-300)";
        CharSequence offerYearlySalaryError = "Must be a double (dollar value)";
        CharSequence offerYearlyBonusError = "Must be a double (dollar value)";
        CharSequence offerRemoteDaysError = "Must be an integer between 0 and 5";
        CharSequence offerLeaveDaysError = "Must be an integer between 0 and 365";
        CharSequence offerNumberOfSharesError = "Must be an integer (number of whole shares)";
        boolean allValidInput = true;

        // Empty and incorrect input error checking (and data type conversion)
        if (offerTitle.equals("")) {
            uiJobTitle.setError(blankStringError);
            allValidInput = false;
        }
        if (offerCompany.equals("")) {
            uiJobCompany.setError(blankStringError);
            allValidInput = false;
        }
        if (offerLocation.equals("")) {
            uiJobLocation.setError(blankStringError);
            allValidInput = false;
        }

        try {
            offerLivingCost = Integer.parseInt(offerLivingCostStr);
            if (offerLivingCost <= 0){
                uiJobLivingCost.setError(offerLivingCostError);
                allValidInput = false;
            }
        } catch (NumberFormatException e) {
            uiJobLivingCost.setError(offerLivingCostError);
            allValidInput = false;
        }

        try {
            offerYearlySalary = Double.parseDouble(offerYearlySalaryStr);
        } catch (NumberFormatException e) {
            uiJobYearlySalary.setError(offerYearlySalaryError);
            allValidInput = false;
        }

        try {
            offerYearlyBonus = Double.parseDouble(offerYearlyBonusStr);
        } catch (NumberFormatException e) {
            uiJobYearlyBonus.setError(offerYearlyBonusError);
            allValidInput = false;
        }

        try {
            offerRemoteDays= Integer.parseInt(offerRemoteDaysStr);
            if (offerRemoteDays < 0 || offerRemoteDays > 5) {
                uiJobRemoteDays.setError(offerRemoteDaysError);
                allValidInput = false;
            }
        } catch (NumberFormatException e) {
            uiJobRemoteDays.setError(offerRemoteDaysError);
            allValidInput = false;
        }

        try {
            offerLeaveDays = Integer.parseInt(offerLeaveDaysStr);
            if (offerLeaveDays < 0 || offerLeaveDays > 365) {
                uiJobLeaveDays.setError(offerLeaveDaysError);
                allValidInput = false;
            }
        } catch (NumberFormatException e) {
            uiJobLeaveDays.setError(offerLeaveDaysError);
            allValidInput = false;
        }

        try {
            offerNumberOfShares = Integer.parseInt(offerNumberOfSharesStr);
        } catch (NumberFormatException e) {
            uiJobNumberOfShares.setError(offerNumberOfSharesError);
            allValidInput = false;
        }

        if (!allValidInput) {
            return null;
        }

        ComparisonSettings settings = dbHelper.getSetting();
        return new Job(offerTitle,
                offerCompany,
                offerLocation,
                offerLivingCost,
                offerYearlySalary,
                offerYearlyBonus,
                offerRemoteDays,
                offerLeaveDays,
                offerNumberOfShares,
                isCurrent,
                settings);
    }

    // Creates a new job with the `isCurrentJob` boolean set to true.
    public void createCurrentJob(View view) {
        Job newCurrentJob = createJobFromUI(true);
        if (newCurrentJob != null) {
            dbHelper.addJob(newCurrentJob);
            openMainMenu(view);
        }
    }

    // Updates the current job.
    public void updateCurrentJob(View view) {
        Job newCurrentJob = createJobFromUI(true);
        if (newCurrentJob != null) {
            ComparisonSettings settings = dbHelper.getSetting();
            newCurrentJob.calculateScores(settings);
            dbHelper.updateCurrentJob(newCurrentJob);
            openMainMenu(view);
        }
    }

    // Creates a new job offer and goes to the job offer saved screen.
    public void createNewOffer(View view) {
        Job newJobOffer = createJobFromUI(false);
        if (newJobOffer != null) {
            dbHelper.addJob(newJobOffer);
            startActivity(new Intent(this, JobSavedActivity.class));
        }
    }

    // Called from the "Save" button in the UI. Calls the appropriate function based on if we're
    // looking at the current job screen.
    public void save(View view) {
        if (isCurrentJobScreen) {
            if (isEditingCurrentJob) {
                updateCurrentJob(view);
            } else {
                createCurrentJob(view);
            }
        } else {
            createNewOffer(view);
        }
    }

    private void updateCurrentJobUI() {
        Job currentJob = dbHelper.getCurrentJob();
        if (currentJob != null) {
            uiJobTitle.setText(String.valueOf(currentJob.getTitle()));
            uiJobCompany.setText(String.valueOf(currentJob.getCompany()));
            uiJobLocation.setText(String.valueOf(currentJob.getLocation()));
            uiJobLivingCost.setText(String.valueOf(currentJob.getLivingCost()));
            uiJobYearlySalary.setText(String.valueOf(currentJob.getYearlySalary()));
            uiJobYearlyBonus.setText(String.valueOf(currentJob.getYearlyBonus()));
            uiJobRemoteDays.setText(String.valueOf(currentJob.getRemoteDays()));
            uiJobLeaveDays.setText(String.valueOf(currentJob.getLeaveDays()));
            uiJobNumberOfShares.setText(String.valueOf(currentJob.getNumberOfShares()));
            isEditingCurrentJob = true;
        }
    }

    // Called from the "Cancel" button in the UI. Opens the main menu and does not save anything.
    public void openMainMenu(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}