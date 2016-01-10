package com.amigo.todo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amigo.todo.contentprovider.ToDoContentProvider;
import com.amigo.todo.databases.ToDoTable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ToDoEditActivity extends AppCompatActivity {

    private static final String TAG = ToDoEditActivity.class.getSimpleName();

    EditText title, description;
    TextView dueDate;
    CheckBox checkBox;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
    Date date;

    private Uri todoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.header);
        getSupportActionBar().setHomeButtonEnabled(true);


        //get the elements
        title = (EditText) findViewById(R.id.titleEditText);
        description = (EditText) findViewById(R.id.summaryEditText);
        dueDate = (TextView) findViewById(R.id.dueDateTextView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        Bundle bundle = getIntent().getExtras();

        // check from the saved Instance
        todoUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(ToDoContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (bundle != null) {
            todoUri = bundle
                    .getParcelable(ToDoContentProvider.CONTENT_ITEM_TYPE);

            fillData(todoUri);
        }
    }

    private void fillData(Uri uri) {
        String[] projection = { ToDoTable.COLUMN_TASK,
                ToDoTable.COLUMN_SUMMARY,ToDoTable.COLUMN_IS_DONE,  ToDoTable.COLUMN_DUE_DATE };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String time = cursor.getString(cursor
                    .getColumnIndexOrThrow(ToDoTable.COLUMN_DUE_DATE));
            date = new Date(time);
            Log.i(TAG, date.toString());
            dueDate.setText(formatter.format(date));

            title.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ToDoTable.COLUMN_TASK)));
            description.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ToDoTable.COLUMN_SUMMARY)));
            int markAsDone = cursor.getInt(cursor.getColumnIndexOrThrow(ToDoTable.COLUMN_IS_DONE));
            if(markAsDone==1)
                checkBox.setChecked(true);
            // always close the cursor
            cursor.close();
        }
    }

    public void setDueDate(View view) {
        DialogFragment dateFragment = new DateFragment();
        dateFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void confirm(View view) {
        if (TextUtils.isEmpty(title.getText().toString())) {
            makeToast();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(ToDoContentProvider.CONTENT_ITEM_TYPE, todoUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String dueDateStr = (String) dueDate.getText();
        String descriptionStr = description.getText().toString();
        String titleStr = title.getText().toString();
        int markAsDone = checkBox.isChecked()==true?1:0;

        // only save if either summary or description
        // is available

        if (titleStr.length() == 0 || dueDateStr.length()==0) {
            return;
        }

        Log.i(TAG, "onSaveState " + String.valueOf(date));

        ContentValues values = new ContentValues();
        values.put(ToDoTable.COLUMN_DUE_DATE, String.valueOf(date));
        values.put(ToDoTable.COLUMN_SUMMARY, descriptionStr);
        values.put(ToDoTable.COLUMN_TASK, titleStr);
        values.put(ToDoTable.COLUMN_IS_DONE, markAsDone);

        if (todoUri == null) {
            // New todo
            todoUri = getContentResolver().insert(ToDoContentProvider.CONTENT_URI, values);
        } else {
            // Update todo
            getContentResolver().update(todoUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(ToDoEditActivity.this, "Please maintain a summary",
                Toast.LENGTH_LONG).show();
    }


    /****** Date Picker Dialog Fragment ******/

    @SuppressLint("ValidFragment")
    public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{


        public DateFragment() {
            // Required empty public constructor
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 23, 59, 59);
            date = c.getTime();
            //Log.i(TAG, "onDateSet " + String.valueOf(date));
            //Log.i(TAG, "onDateSet " + String.valueOf(date.getTime()/1000));
            month++;
            dueDate.setText(day+"/"+month+"/"+year);
        }

    }
}
