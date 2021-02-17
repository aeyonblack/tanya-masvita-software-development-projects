package com.acronym.playit.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.acronym.playit.models.Badge;
import com.acronym.playit.models.Quiz;
import com.acronym.playit.models.User;
import com.acronym.playit.utils.Constants;
import com.acronym.playit.utils.QuizUtil;

public class QuizOpenHelper extends SQLiteOpenHelper{

    /*Constants*/
    private static final String TAG  = QuizOpenHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quiz_base";
    private static final String QUIZ_TABLE = "quizzes";
    private static final String PROGRESS_TABLE = "progress";
    private static final String COMPLETED_QUIZZES_TABLE = "completed_quizzes";
    private static final String BADGES_TABLE = "badges";

    /*Column names ~ Quiz table*/
    private static final String KEY_ID = "_id";
    private static final String KEY_QUIZ = "quiz";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SCORE = "score";
    private static final String KEY_SOLUTION = "solution";
    private static final String KEY_STATUS = "status";
    private static final String KEY_GOT_IT = "got_it";

    /*Column names ~ Progress Table*/
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_CURRENT_BEST = "current_best";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_BEST_WEAPON = "best_weapon";
    private static final String KEY_BADGES_COUNT = "badges_count";

    private static final String KEY_BADGE = "badge";
    private static final String KEY_BADGE_COLOR = "badge_color";


    /*Create quiz table command*/
    private static final String QUIZZES_TABLE_CREATE = "CREATE TABLE " + QUIZ_TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_QUIZ + " TEXT, " + KEY_TYPE + " TEXT, "
            + KEY_SCORE + " INTEGER, " + KEY_SOLUTION + " TEXT, " + KEY_STATUS + " INTEGER );";

    /*Create progress table command*/
    private static final String PROGRESS_TABLE_CREATE = "CREATE TABLE " + PROGRESS_TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TOTAL_SCORE + " INTEGER, " + KEY_CURRENT_BEST
            + " INTEGER, " + KEY_LEVEL + " INTEGER, " + KEY_BADGES_COUNT + " INTEGER, "
            + KEY_COMPLETED + " INTEGER, " + KEY_BEST_WEAPON + " TEXT );";

    /*Create completed quizzes_table command*/
    private static final String COMPLETED_QUIZZES_TABLE_CREATE = "CREATE TABLE " +
            COMPLETED_QUIZZES_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_QUIZ + " TEXT, " + KEY_TYPE + " TEXT, " + KEY_GOT_IT + " INTEGER, "
            + KEY_SOLUTION + " TEXT );";

    /*Create badge table command*/
    private static final String BADGES_TABLE_CREATE = "CREATE TABLE " + BADGES_TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_BADGE + " TEXT, " + KEY_BADGE_COLOR + " TEXT );";

    /*SQL Database*/
    private SQLiteDatabase mReadableDatabase;
    private SQLiteDatabase mWritableDatabase;

    // Can't explain this variable any further
    private Context mContext;


    // Constructor
    public QuizOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }


    /* Instantiate the database, Create it if doesn't already exist*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUIZZES_TABLE_CREATE);
        db.execSQL(PROGRESS_TABLE_CREATE);
        db.execSQL(COMPLETED_QUIZZES_TABLE_CREATE);
        db.execSQL(BADGES_TABLE_CREATE);
        fillDatabaseWithData(db);
        updateProgressTable(db);
        fillCompleted(db);
    }


    // Populate the database with quizzes
    private void fillDatabaseWithData(SQLiteDatabase db) {

        // Fill the database with  10 quizzes to begin with
        String[] quizzes = {Constants.MATH_0, Constants.MATH_1, Constants.MATH_2, Constants.MATH_3,
                Constants.MATH_4, Constants.MATH_5, Constants.MATH_6, Constants.MATH_7,
                Constants.MATH_8, Constants.MATH_9};
        String[] types = {Constants.MATH,Constants.MATH,Constants.MATH,Constants.MATH,
                Constants.MATH,Constants.MATH,Constants.MATH,
                Constants.MATH,Constants.MATH,Constants.MATH};
        int[] scores = {3,5,5,2,5,2,2,5,2,2};
        String[] solutions = {QuizUtil.mathMap(Constants.MATH_0), QuizUtil.mathMap(Constants.MATH_1),
                QuizUtil.mathMap(Constants.MATH_2), QuizUtil.mathMap(Constants.MATH_3),
                QuizUtil.mathMap(Constants.MATH_4), QuizUtil.mathMap(Constants.MATH_5),
                QuizUtil.mathMap(Constants.MATH_6), QuizUtil.mathMap(Constants.MATH_7),
                QuizUtil.mathMap(Constants.MATH_8), QuizUtil.mathMap(Constants.MATH_9)};
        int[] done_statuses = {0,0,0,0,0,0,0,0,0,0};

        // Content Values defining the data to be stored in the specified column
        ContentValues values = new ContentValues();
        for (int i = 0; i < quizzes.length; i++) {
            values.put(KEY_QUIZ, quizzes[i]);
            values.put(KEY_TYPE, types[i]);
            values.put(KEY_SCORE, scores[i]);
            values.put(KEY_SOLUTION, solutions[i]);
            values.put(KEY_STATUS, done_statuses[i]);
            db.insert(QUIZ_TABLE, null, values);
        }

    }

    /*Updates the user's current progress*/
    private void updateProgressTable(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_TOTAL_SCORE, 0);
        values.put(KEY_CURRENT_BEST, 0);
        values.put(KEY_LEVEL, 1);
        values.put(KEY_COMPLETED, 0);
        values.put(KEY_BEST_WEAPON, "None");
        values.put(KEY_BADGES_COUNT, 0);
        db.insert(PROGRESS_TABLE, null, values);
    }

    /*Mark a quiz as completed, by adding it to the completed database*/
    private void fillCompleted(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUIZ, Constants.MATH_0);
        values.put(KEY_SOLUTION, "40");
        values.put(KEY_TYPE, Constants.MATH);
        values.put(KEY_GOT_IT, 1);
        db.insert(COMPLETED_QUIZZES_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QUIZ_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COMPLETED_QUIZZES_TABLE);
        onCreate(db);
    }


    /**
     * Any quiz found within the completed quiz table is
     * marked as completed, and therefore not asked again!
     * @param quiz the quiz
     * @param solution correct response to the quiz
     * @param type e.g math, riddle, science
     * @param gotIt = 0 if user got it incorrectly and 1 if user got it correctly [kinda weird]
     */
    void insertCompletedQuiz(String quiz, String solution, String type, int gotIt) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUIZ, quiz);
        values.put(KEY_SOLUTION, solution);
        values.put(KEY_TYPE, type);
        values.put(KEY_GOT_IT, gotIt);

        try {
            if (mWritableDatabase == null) {
                mWritableDatabase = getWritableDatabase();
            }
            mWritableDatabase.insert(COMPLETED_QUIZZES_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        }
    }


    /**
     * This method queries a specific quiz from the quiz table
     * @param position the position from which the quiz should be queried
     * @return the quiz object
     */
    Quiz query(int position) {
        String query = "SELECT * FROM " + QUIZ_TABLE
                + " ORDER BY " + KEY_SCORE + " ASC " + "LIMIT " + position + ",1";

        Cursor cursor = null;
        Quiz quiz = new Quiz();

        try {

            if (mReadableDatabase == null) {
                mReadableDatabase = getReadableDatabase();
            }
            cursor = mReadableDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            quiz.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            quiz.setQuiz(cursor.getString(cursor.getColumnIndex(KEY_QUIZ)));
            quiz.setSolution(cursor.getString(cursor.getColumnIndex(KEY_SOLUTION)));
            quiz.setScore(cursor.getInt(cursor.getColumnIndex(KEY_SCORE)));
            quiz.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
            quiz.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));

        } catch (Exception e) {

            Log.d(TAG, "exception: " + e.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }

        return quiz;
    }

    Quiz queryCompleted(int position) {
        String query = "SELECT * FROM " + COMPLETED_QUIZZES_TABLE + " LIMIT " + position + ",1";

        Cursor cursor = null;
        Quiz quiz = new Quiz();

        try {
            if (mReadableDatabase == null) {
                mReadableDatabase = getReadableDatabase();
            }
            cursor = mReadableDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            quiz.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            quiz.setQuiz(cursor.getString(cursor.getColumnIndex(KEY_QUIZ)));
            quiz.setSolution(cursor.getString(cursor.getColumnIndex(KEY_SOLUTION)));
            quiz.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            quiz.setGotIt(cursor.getInt(cursor.getColumnIndex(KEY_GOT_IT)));

        } catch (Exception e) {

            Log.d(TAG, "Error:" + e.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }

        return quiz;
    }


    long count() {
        if (mReadableDatabase == null) {
            mReadableDatabase = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDatabase, QUIZ_TABLE);
    }

    int completedCount() {
        User user = progressQuery(0);
        if (user.getTotalCompleted() == 50) {
            insertBadge("STUDENT", "#00e68a");
            toast("STUDENT badge Unlocked!");
        }
        else if (user.getTotalCompleted() == 500) {
            insertBadge("CHAMP", "#ff80ff");
            toast("CHAMP badge unlocked");
        }
        else if (user.getTotalCompleted() == 1000) {
            insertBadge("THE WITCHER", "#ff5050");
            toast("THE WITCHER badge unlocked");
        }
        return user.getTotalCompleted()+1;
    }

    public User progressQuery(int position) {
        String query = "SELECT * FROM " + PROGRESS_TABLE + " LIMIT " + position + ",1";
        Cursor cursor = null;
        User user = new User();

        try {

            if (mReadableDatabase == null) {
                mReadableDatabase = getReadableDatabase();
            }

            cursor = mReadableDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setTotalScore(cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_SCORE)));
            user.setCurrentBest(cursor.getInt(cursor.getColumnIndex(KEY_CURRENT_BEST)));
            user.setLevel(cursor.getInt(cursor.getColumnIndex(KEY_LEVEL)));
            user.setBestWeapon(cursor.getString(cursor.getColumnIndex(KEY_BEST_WEAPON)));
            user.setTotalCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)));
            user.setBadgeCount(cursor.getInt(cursor.getColumnIndex(KEY_BADGES_COUNT)));

        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }


    void updateTotalScore(int id, int totalScore) {
        User user = progressQuery(0);
        totalScore += user.getTotalScore();
        int totalCompleted = user.getTotalCompleted() + 1;
        try {
            if (mWritableDatabase == null) {
                mWritableDatabase = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_TOTAL_SCORE, totalScore);
            values.put(KEY_COMPLETED, totalCompleted);
            mWritableDatabase.update(PROGRESS_TABLE, values,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        }

        /*Update level*/
        updateLevel();
    }

    void updateCurrentBest(int id, int currentBest) {
        User user = progressQuery(0);
        int bestScore;
        if (currentBest > user.getCurrentBest()) {
            bestScore = currentBest;
        }
        else {
            bestScore = user.getCurrentBest();
        }
        try {
            if (mWritableDatabase == null) {
                mWritableDatabase = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_CURRENT_BEST, bestScore);
            mWritableDatabase.update(PROGRESS_TABLE, values,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        }
    }

    private void updateLevel() {
        User user = progressQuery(0);
        int level = user.getLevel();
        if (user.getTotalScore() >= 50 && user.getTotalScore() < 60) {
            level = 2;
            insertBadge("LEVEL 2", "#800080");
            toast("You have reached level 2! New badge UNLOCKED!");
        }
        else if (user.getTotalScore() >= 60 && user.getTotalScore() < 300) {
            level = 3;
            insertBadge("LEVEL 3", "#00ccff");
            toast("You have reached level 3! New badge UNLOCKED!");
        }
        else if (user.getTotalScore() >= 300 && user.getTotalScore() < 500) {
            level = 4;
            insertBadge("LEVEL 4", "#ffcc00");
            toast("You have reached level 4! New badge UNLOCKED!");
        }
        else if (user.getTotalScore() >= 500 && user.getTotalScore() < 750) {
            level = 5;
            insertBadge("LEVEL 5", "#ac3939");
            toast("You have reached level 5! New badge UNLOCKED!");
        }
        else if (user.getTotalScore() >= 750 && user.getTotalScore() < 1000) {
            level = 6;
            insertBadge("STARFISH", "#cc9900");
            toast("STARFISH badge unlocked!");
        }
        else if (user.getTotalScore() >= 1000 && user.getTotalScore() < 2000) {
            level = 7;
            toast("Level 7 has been reached!");
        }
        else if (user.getTotalScore() >= 2000 && user.getTotalScore() < 3500) {
            level = 8;
            toast("Level 8 has been reached!");
        }
        else if (user.getTotalScore() >= 3500 && user.getTotalScore() < 6000) {
            level = 9;
            toast("You have reached level 9!");
        }
        else if (user.getTotalScore() >= 6000 && user.getTotalScore() < 10000) {
            level = 10;
            insertBadge("EINSTEIN", "#cc6699");
            toast("EINSTEIN badge unlocked!");
        }

        try {
            if (mWritableDatabase == null) {
                mWritableDatabase = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_LEVEL, level);
            mWritableDatabase.update(PROGRESS_TABLE, values,
                    KEY_ID + " = ? ", new String[]{String.valueOf(user.getId())});
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        }

    }

    private void insertBadge(String name, String color) {

        ContentValues values = new ContentValues();
        values.put(KEY_BADGE, name);
        values.put(KEY_BADGE_COLOR, color);

        ContentValues progressValues = new ContentValues();
        User currentUser = progressQuery(0);
        int badgeCount = currentUser.getBadgeCount() +  1;
        progressValues.put(KEY_BADGES_COUNT, badgeCount);

        try {
            if (mWritableDatabase == null) {
                mWritableDatabase = getWritableDatabase();
            }
            mWritableDatabase.insert(BADGES_TABLE, null, values);
            mWritableDatabase.update(PROGRESS_TABLE, progressValues,
                    KEY_ID + " = ? ", new String[]{String.valueOf(currentUser.getId())});
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        }

        toast(name + " badge added");

    }

    Badge queryBadge(int position) {
        String query = "SELECT * FROM " + BADGES_TABLE + " LIMIT " + position + ",1";
        Cursor cursor = null;
        Badge badge = new Badge();

        try {
            if (mReadableDatabase == null) {
                mReadableDatabase = getReadableDatabase();
            }
            cursor = mReadableDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            badge.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            badge.setName(cursor.getString(cursor.getColumnIndex(KEY_BADGE)));
            badge.setColor(cursor.getString(cursor.getColumnIndex(KEY_BADGE_COLOR)));
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return badge;
    }

    private void toast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


}
