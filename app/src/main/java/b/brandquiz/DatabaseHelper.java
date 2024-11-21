package b.brandquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizApp.db";
    private static final int DATABASE_VERSION = 2; // Updated to version 2

    // User table
    private static final String USER_TABLE = "user";
    private static final String USER_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    // Question table
    private static final String QUESTION_TABLE = "questions";
    private static final String QUESTION_ID = "id";
    private static final String QUESTION_TEXT = "question";
    private static final String OPTION_A = "optionA";
    private static final String OPTION_B = "optionB";
    private static final String OPTION_C = "optionC";
    private static final String OPTION_D = "optionD";
    private static final String CORRECT_ANSWER = "correctAnswer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + USER_TABLE + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT UNIQUE, " +
                PASSWORD + " TEXT)";

        db.execSQL(createUserTable);

        String createQuestionTable = "CREATE TABLE " + QUESTION_TABLE + " (" +
                QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QUESTION_TEXT + " TEXT, " +
                OPTION_A + " TEXT, " +
                OPTION_B + " TEXT, " +
                OPTION_C + " TEXT, " +
                OPTION_D + " TEXT, " +
                CORRECT_ANSWER + " TEXT)";

        db.execSQL(createQuestionTable);

        // Preload questions
        preloadQuestions(db);
    }

    private void preloadQuestions(SQLiteDatabase db) {
        String[] questions = {
                "What is the capital of France?|Paris|London|Berlin|Madrid|A",
                "What is 2 + 2?|3|4|2|5|B",
                "What is the largest ocean?|Atlantic|Indian|Arctic|Pacific|D",
                "What is the capital of Japan?|Beijing|Seoul|Tokyo|Bangkok|C",
                "What is 5 * 6?|30|25|35|40|A",
                "Who wrote 'Romeo and Juliet'?|Shakespeare|Hemingway|Dickens|Austen|A",
                "What is the boiling point of water?|100°C|90°C|80°C|120°C|A",
                "What is the capital of Italy?|Rome|Madrid|Berlin|Paris|A",
                "What is 10 / 2?|2|5|10|3|B",
                "What is the largest planet?|Earth|Mars|Jupiter|Saturn|C"
        };

        for (String q : questions) {
            String[] parts = q.split("\\|");
            ContentValues values = new ContentValues();
            values.put(QUESTION_TEXT, parts[0]);
            values.put(OPTION_A, parts[1]);
            values.put(OPTION_B, parts[2]);
            values.put(OPTION_C, parts[3]);
            values.put(OPTION_D, parts[4]);
            values.put(CORRECT_ANSWER, parts[5]);
            db.insert(QUESTION_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrade
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + QUESTION_TABLE);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Optional: Handle downgrades explicitly if needed
        super.onDowngrade(db, oldVersion, newVersion);
    }

    // Register a user
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        long result = db.insert(USER_TABLE, null, values);
        return result != -1; // Return true if registration is successful
    }

    // Check if a user exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + USERNAME + "=? AND " + PASSWORD + "=?", new String[]{username, password});
        return cursor.getCount() > 0; // Return true if user exists
    }

    // Retrieve questions in random order
    public Cursor getQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + QUESTION_TABLE + " ORDER BY RANDOM()", null);
    }
}
