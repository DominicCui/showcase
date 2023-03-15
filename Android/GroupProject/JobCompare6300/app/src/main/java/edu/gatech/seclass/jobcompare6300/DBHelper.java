package edu.gatech.seclass.jobcompare6300;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private final String SETTING = "SETTING";
    private final String ID = "ID";
    private final String JOB = "JOB";
    private final String TITLE = "TITLE";
    private final String COMPANY = "COMPANY";
    private final String LOCATION = "LOCATION";
    private final String LIVING_COST = "LIVING_COST";
    private final String YEARLY_SALARY = "YEARLY_SALARY";
    private final String YEARLY_BONUS = "YEARLY_BONUS";
    private final String ALLOWED_WEEKLY_TELEWORK = "ALLOWED_WEEKLY_TELEWORK";
    private final String AYS = "AYS";
    private final String LEAVE_DAYS = "LEAVE_D";
    private final String NUMBER_OF_SHARES = "NUMBER_OF_SHARES";
    private final String AYB = "AYB";
    private final String CURRENT_JOB = "CURRENT_JOB";
    private final String SCORE = "SCORE";

    public DBHelper(@Nullable Context context){
        super(context, "job.db"  , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create Job table in DB
        String createJobTable = "CREATE TABLE " +
                JOB + " ( " +
                TITLE + " varchar(128) NOT NULL, " +
                COMPANY + " varchar(128) NOT NULL, " +
                LOCATION + " varchar(128) NOT NULL, " +
                LIVING_COST + " int, " +
                YEARLY_SALARY + " double, " +
                YEARLY_BONUS + " double, " +
                ALLOWED_WEEKLY_TELEWORK + " int NOT NULL, " +
                LEAVE_DAYS + " int NOT NULL, " +
                NUMBER_OF_SHARES + " int NOT NULL, " +
                AYS + " double NOT NULL, " +
                AYB + " double NOT NULL, " +
                CURRENT_JOB + " boolean, " +
                SCORE + " double NOT NULL, " +
                "primary key ( " + TITLE + ", " + COMPANY + ", " + LOCATION + ") " +
                ");";
        db.execSQL(createJobTable);

        // create setting table in DB
        String createSettingTable = "CREATE TABLE " +
                SETTING + "( " +
                ID + " INTEGER primary key, "+
                YEARLY_SALARY + " int NOT NULL, " +
                YEARLY_BONUS + " int NOT NULL, " +
                ALLOWED_WEEKLY_TELEWORK + " int NOT NULL, " +
                LEAVE_DAYS + " int NOT NULL, " +
                NUMBER_OF_SHARES + " int NOT NULL " +
                ");";
        db.execSQL(createSettingTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Maybe next version
//        if (oldVersion < 2){
//            db.execSQL("ALTER TABLE " + JOB +
//                            " ADD " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT; "
//                    );
//        }

    }

    // save job to DB
    public void addJob(@NotNull Job job) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TITLE, job.getTitle());
        cv.put(COMPANY, job.getCompany());
        cv.put(LOCATION, job.getLocation());
        cv.put(LIVING_COST, job.getLivingCost());
        cv.put(YEARLY_SALARY, job.getYearlySalary());
        cv.put(YEARLY_BONUS, job.getYearlyBonus());
        cv.put(ALLOWED_WEEKLY_TELEWORK, job.getRemoteDays());
        cv.put(LEAVE_DAYS, job.getLeaveDays());
        cv.put(NUMBER_OF_SHARES, job.getNumberOfShares());
        cv.put(AYS, job.getAys());
        cv.put(AYB, job.getAyb());
        cv.put(SCORE, job.getScore());
        cv.put(CURRENT_JOB, job.isCurrentJob());

        db.insert(JOB, null , cv);
        db.close();
    }

    // save the updated setting to DB
    public void updateCurrentJob(@NotNull Job currentJob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TITLE, currentJob.getTitle());
        cv.put(COMPANY, currentJob.getCompany());
        cv.put(LOCATION, currentJob.getLocation());
        cv.put(LIVING_COST, currentJob.getLivingCost());
        cv.put(YEARLY_SALARY, currentJob.getYearlySalary());
        cv.put(YEARLY_BONUS, currentJob.getYearlyBonus());
        cv.put(ALLOWED_WEEKLY_TELEWORK, currentJob.getRemoteDays());
        cv.put(LEAVE_DAYS, currentJob.getLeaveDays());
        cv.put(NUMBER_OF_SHARES, currentJob.getNumberOfShares());
        cv.put(AYS, currentJob.getAys());
        cv.put(AYB, currentJob.getAyb());
        cv.put(SCORE, currentJob.getScore());

        db.update(JOB, cv, "CURRENT_JOB = 1", null);
        db.close();
    }

    // get current job from db
    public Job getCurrentJob() {
        Job currentJob = null;
        String selectCurrent = "SELECT * FROM " + JOB + " WHERE " + CURRENT_JOB + " = 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectCurrent, null); // result set

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            String company = cursor.getString(cursor.getColumnIndex(COMPANY));
            String location = cursor.getString(cursor.getColumnIndex(LOCATION));
            int living_cost = cursor.getInt(cursor.getColumnIndex(LIVING_COST));
            double yearly_salary = cursor.getDouble(cursor.getColumnIndex(YEARLY_SALARY));
            double yearly_bonus = cursor.getDouble(cursor.getColumnIndex(YEARLY_BONUS));
            int allowed_weekly_telework = cursor.getInt(cursor.getColumnIndex(ALLOWED_WEEKLY_TELEWORK));
            int leave_days = cursor.getInt(cursor.getColumnIndex(LEAVE_DAYS));
            int number_of_shares = cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SHARES));
            double ays = cursor.getDouble(cursor.getColumnIndex(AYS));
            double ayb = cursor.getDouble(cursor.getColumnIndex(AYB));
            boolean current_job = cursor.getInt(cursor.getColumnIndex(CURRENT_JOB)) == 1;
            double score = cursor.getDouble(cursor.getColumnIndex(SCORE));
            currentJob = new Job(title, company, location, living_cost, yearly_salary,
                    yearly_bonus, allowed_weekly_telework, leave_days, number_of_shares, ays, ayb,
                    current_job, score);
        }

        cursor.close();
        db.close();
        return currentJob;
    }

    // retrieve all records of job offer from DB
    public List<Job> getOfferRecord() {
        List<Job> offerList = new ArrayList<>();

        String selectAll = "SELECT * FROM " + JOB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null); // result set

        if (cursor.moveToFirst()) {
            do{
                String title = cursor.getString(cursor.getColumnIndex(TITLE));
                String company = cursor.getString(cursor.getColumnIndex(COMPANY));
                String location= cursor.getString(cursor.getColumnIndex(LOCATION));
                int living_cost = cursor.getInt(cursor.getColumnIndex(LIVING_COST));
                double yearly_salary = cursor.getDouble(cursor.getColumnIndex(YEARLY_SALARY));
                double yearly_bonus = cursor.getDouble(cursor.getColumnIndex(YEARLY_BONUS));
                int allowed_weekly_telework = cursor.getInt(cursor.getColumnIndex(ALLOWED_WEEKLY_TELEWORK));
                int leave_days = cursor.getInt(cursor.getColumnIndex(LEAVE_DAYS));
                int number_of_shares = cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SHARES));
                double ays = cursor.getDouble(cursor.getColumnIndex(AYS));
                double ayb = cursor.getDouble(cursor.getColumnIndex(AYB));
                boolean current_job = cursor.getInt(cursor.getColumnIndex(CURRENT_JOB)) == 1;
                double score = cursor.getDouble(cursor.getColumnIndex(SCORE));
                Job newJob = new Job(title, company, location, living_cost, yearly_salary,
                        yearly_bonus,allowed_weekly_telework, leave_days, number_of_shares, ays, ayb,
                        current_job, score);
                offerList.add(newJob);
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return offerList;
    }

    // retrieve setting from DB
    public ComparisonSettings getSetting() {
        ComparisonSettings settings = null;
        String selectAll = "SELECT * FROM " + SETTING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            int yearly_salary = cursor.getInt(cursor.getColumnIndex(YEARLY_SALARY));
            int yearly_bonus = cursor.getInt(cursor.getColumnIndex(YEARLY_BONUS));
            int allowed_weekly_telework = cursor.getInt(cursor.getColumnIndex(ALLOWED_WEEKLY_TELEWORK));
            int leave_days = cursor.getInt(cursor.getColumnIndex(LEAVE_DAYS));
            int number_of_shares = cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SHARES));

            settings = new ComparisonSettings(yearly_salary, yearly_bonus, allowed_weekly_telework, leave_days, number_of_shares);
        } else {
            // On first run just init everything to 1.
            settings = new ComparisonSettings();

            ContentValues cv = new ContentValues();

            cv.put(ID, 1);
            cv.put(YEARLY_SALARY, 1);
            cv.put(YEARLY_BONUS, 1);
            cv.put(ALLOWED_WEEKLY_TELEWORK, 1);
            cv.put(LEAVE_DAYS, 1);
            cv.put(NUMBER_OF_SHARES, 1);

            db.insert(SETTING, null , cv);
        }

        cursor.close();
        db.close();
        return settings;
    }

    // save the updated setting to DB
    public void updateSetting(@NotNull ComparisonSettings settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(YEARLY_SALARY, settings.getwYearlySalary());
        cv.put(YEARLY_BONUS, settings.getwYearlyBonus());
        cv.put(ALLOWED_WEEKLY_TELEWORK, settings.getwRemoteDays());
        cv.put(LEAVE_DAYS, settings.getwLeaveDays());
        cv.put(NUMBER_OF_SHARES, settings.getwShares());

        db.update(SETTING, cv, "ID = 1", null);
        db.close();
    }

    public void updateScore(Job job, ComparisonSettings settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        job.calculateScores(settings);
        cv.put(SCORE, job.getScore());

        db.update(JOB, cv, "TITLE = ? AND COMPANY = ? AND LOCATION = ?", new String[]{job.getTitle(), job.getCompany(), job.getLocation()});
        db.close();
    }

    public Job getMostRecentlyAddedJob() {
        Job newestJob = null;
        String selectJob = "SELECT * FROM " + JOB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectJob, null); // result set

        if (cursor.moveToLast()) {  // Most recent row is the newest job.
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            String company = cursor.getString(cursor.getColumnIndex(COMPANY));
            String location = cursor.getString(cursor.getColumnIndex(LOCATION));
            int living_cost = cursor.getInt(cursor.getColumnIndex(LIVING_COST));
            double yearly_salary = cursor.getDouble(cursor.getColumnIndex(YEARLY_SALARY));
            double yearly_bonus = cursor.getDouble(cursor.getColumnIndex(YEARLY_BONUS));
            int allowed_weekly_telework = cursor.getInt(cursor.getColumnIndex(ALLOWED_WEEKLY_TELEWORK));
            int leave_days = cursor.getInt(cursor.getColumnIndex(LEAVE_DAYS));
            int number_of_shares = cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SHARES));
            double ays = cursor.getDouble(cursor.getColumnIndex(AYS));
            double ayb = cursor.getDouble(cursor.getColumnIndex(AYB));
            boolean current_job = cursor.getInt(cursor.getColumnIndex(CURRENT_JOB)) == 1 ? true : false;
            double score = cursor.getDouble(cursor.getColumnIndex(SCORE));
            newestJob = new Job(title, company, location, living_cost, yearly_salary,
                    yearly_bonus, allowed_weekly_telework, leave_days, number_of_shares, ays, ayb,
                    current_job, score);
        }

        cursor.close();
        db.close();
        return newestJob;
    }
}
