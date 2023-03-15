package edu.gatech.seclass.jobcompare6300;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ComparisonSettingsActivity extends AppCompatActivity {

    // UI comparison settings attributes
    private EditText uiWeightSalary;
    private EditText uiWeightBonus;
    private EditText uiWeightTelework;
    private EditText uiWeightLeave;
    private EditText uiWeightEquity;
    private DBHelper dbHelper = new DBHelper(ComparisonSettingsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_comparison_settings);

        uiWeightSalary = findViewById(R.id.weightSalaryID);
        uiWeightBonus = findViewById(R.id.weightBonusID);
        uiWeightTelework = findViewById(R.id.weightTeleworkID);
        uiWeightLeave = findViewById(R.id.weightLeaveID);
        uiWeightEquity = findViewById(R.id.weightEquityID);
        updateUI();
    }

    public void saveWeights(View view) {
        boolean allValidInput = true;
        int weightSalary = 0;
        int weightBonus = 0;
        int weightTelework = 0;
        int weightLeave = 0;
        int weightEquity = 0;

        CharSequence blankStringError = "Must be a non-negative integer";
        CharSequence weightsMoreThanZero = "At least one weight must be greater than 0";
        try {
            weightSalary = Integer.parseInt(uiWeightSalary.getText().toString());
        } catch (NumberFormatException e) {
            uiWeightSalary.setError(blankStringError);
            allValidInput = false;
        }
        try {
            weightBonus = Integer.parseInt(uiWeightBonus.getText().toString());
        } catch (NumberFormatException e) {
            uiWeightBonus.setError(blankStringError);
            allValidInput = false;
        }
        try {
            weightTelework = Integer.parseInt(uiWeightTelework.getText().toString());
        } catch (NumberFormatException e) {
            uiWeightTelework.setError(blankStringError);
            allValidInput = false;
        }
        try {
            weightLeave = Integer.parseInt(uiWeightLeave.getText().toString());
        } catch (NumberFormatException e) {
            uiWeightLeave.setError(blankStringError);
            allValidInput = false;
        }
        try {
            weightEquity = Integer.parseInt(uiWeightEquity.getText().toString());
        } catch (NumberFormatException e) {
            uiWeightEquity.setError(blankStringError);
            allValidInput = false;
        }

        // If all the fields are filled out, check that at least one isn't 0 to avoid divide by zero crash.
        if (allValidInput &&
                (weightSalary + weightBonus + weightTelework + weightLeave + weightEquity == 0)) {
            Toast toast = Toast.makeText(getApplicationContext(), weightsMoreThanZero, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            allValidInput = false;
        }

        if (!allValidInput) {
            return;
        }
        ComparisonSettings settings = new ComparisonSettings(weightSalary, weightBonus, weightTelework, weightLeave, weightEquity);
        dbHelper.updateSetting(settings);
        // Go back to main menu after clicking save.
        openMainMenu(view);
    }

    public void openMainMenu(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void updateUI() {
        ComparisonSettings settings = dbHelper.getSetting();
        int leaveDays = settings.getwLeaveDays();
        int remoteDays = settings.getwRemoteDays();
        int shares = settings.getwShares();
        int bonus = settings.getwYearlyBonus();
        int salary = settings.getwYearlySalary();

        uiWeightSalary.setText(String.valueOf(salary));
        uiWeightBonus.setText(String.valueOf(bonus));
        uiWeightTelework.setText(String.valueOf(remoteDays));
        uiWeightLeave.setText(String.valueOf(leaveDays));
        uiWeightEquity.setText(String.valueOf(shares));
    }
}