package com.adori.personlistsample;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class PersonEditActivity extends AppCompatActivity {

    public static final int DATA_EDITED = 2;

    public static final String EDIT_ACTION = "edit";

    public static final String FIRST_NAME_EXT = "first_name";
    public static final String LAST_NAME_EXT = "last_name";
    public static final String DOB_EXT = "dob";
    public static final String ZIP_CODE_EXT = "zip_code";

    public static final String KEY_EXT = "key";

    protected static EditText mFirstName;
    protected static EditText mLastName;
    protected static EditText mDob;
    protected static EditText mZipCode;

    private String mKey = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mDob = (EditText) findViewById(R.id.dob);
        mDob.setInputType(InputType.TYPE_NULL);
        mDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDatePickerDialog(view);
                }
            }
        });
        mZipCode = (EditText) findViewById(R.id.zip_code);

        if (getIntent().getAction() != null && getIntent().getAction().equals(EDIT_ACTION)) {
            mKey = getIntent().getStringExtra(KEY_EXT);

            if (getIntent().getStringExtra(FIRST_NAME_EXT) != null) {
                mFirstName.setText(getIntent().getStringExtra(FIRST_NAME_EXT));
            }

            if (getIntent().getStringExtra(LAST_NAME_EXT) != null) {
                mLastName.setText(getIntent().getStringExtra(LAST_NAME_EXT));
            }

            if (getIntent().getStringExtra(DOB_EXT) != null) {
                mDob.setText(getIntent().getStringExtra(DOB_EXT));
            }

            if (getIntent().getIntExtra(ZIP_CODE_EXT, 0) != 0) {
                mZipCode.setText(String.valueOf(getIntent().getIntExtra(ZIP_CODE_EXT, 0)));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save_person:
                if (!save()) {
                    return true;
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkEmpties() {
        if (mFirstName.getText()
                .toString().equals("") ||
                mLastName.getText()
                        .toString().equals("") ||
                mDob.getText()
                        .toString().equals("") ||
                mZipCode.getText()
                        .toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return false;
    }

    private boolean save() {
        if (checkEmpties())
            return false;
        Person person = new Person(mFirstName.getText().toString(),
                mLastName.getText().toString(),
                mDob.getText().toString(),
                Integer.valueOf(mZipCode.getText().toString()));

        PersonsFirebaseDatabase db = PersonsFirebaseDatabase.getInstance();
        if (mKey != null) {
            db.editPerson(mKey, person);
            Intent intent = new Intent();
            intent.putExtra(KEY_EXT, mKey);
            intent.putExtra(FIRST_NAME_EXT, person.firstName);
            intent.putExtra(LAST_NAME_EXT, person.lastName);
            intent.putExtra(DOB_EXT, person.dob);
            intent.putExtra(ZIP_CODE_EXT, person.zipCode);
            setResult(DATA_EDITED, intent);
        } else {
            db.writeNewPerson(person);
        }

        return true;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = month + " / " + day + " / " + year;
            mDob.setText(date);
        }
    }
}
