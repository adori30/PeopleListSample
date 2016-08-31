package com.adori.personlistsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adori.personlistsample.util.ConfirmationDialog;

public class PersonDetailActivity extends AppCompatActivity implements ConfirmationDialog.Listener {

    public static final int EDIT_CODE = 1;
    private static final String DIALOG_TAG = "dialog-tag";
    private static final String FRAGMENT_TAG = "frag-tag";

    private String mPersonKey;
    private Person mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonDetailActivity.this, PersonEditActivity.class);
                intent.setAction(PersonEditActivity.EDIT_ACTION);
                intent.putExtra(PersonEditActivity.KEY_EXT, mPersonKey);
                intent.putExtra(PersonEditActivity.FIRST_NAME_EXT, mPerson.firstName);
                intent.putExtra(PersonEditActivity.LAST_NAME_EXT, mPerson.lastName);
                intent.putExtra(PersonEditActivity.DOB_EXT, mPerson.dob);
                intent.putExtra(PersonEditActivity.ZIP_CODE_EXT, mPerson.zipCode);
                startActivityForResult(intent, EDIT_CODE);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mPersonKey = intent.getStringExtra(PersonDetailFragment.ARG_KEY);
            String firstName = intent.getStringExtra(PersonDetailFragment.ARG_FIRST_NAME);
            String lastName = intent.getStringExtra(PersonDetailFragment.ARG_LAST_NAME);
            String dob = intent.getStringExtra(PersonDetailFragment.ARG_DOB);
            int zipCode = intent.getIntExtra(PersonDetailFragment.ARG_ZIP_CODE, 0);
            mPerson = new Person(firstName, lastName, dob, zipCode);

            Bundle arguments = new Bundle();
            arguments.putString(PersonDetailFragment.ARG_KEY,
                    mPersonKey);
            arguments.putString(PersonDetailFragment.ARG_FIRST_NAME, firstName);
            arguments.putString(PersonDetailFragment.ARG_LAST_NAME, lastName);
            arguments.putString(PersonDetailFragment.ARG_DOB, dob);
            arguments.putInt(PersonDetailFragment.ARG_ZIP_CODE, zipCode);
            PersonDetailFragment fragment = new PersonDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.person_detail_container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == PersonEditActivity.DATA_EDITED) {
            PersonDetailFragment fragment = (PersonDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_TAG);
            mPerson = new Person(intent.getStringExtra(PersonDetailFragment.ARG_FIRST_NAME),
                    intent.getStringExtra(PersonDetailFragment.ARG_LAST_NAME),
                    intent.getStringExtra(PersonDetailFragment.ARG_DOB),
                    intent.getIntExtra(PersonDetailFragment.ARG_ZIP_CODE, 0));
            fragment.updateData(mPerson);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                navigateUpTo(new Intent(this, PersonListActivity.class));
                return true;
            case R.id.action_delete:
                ConfirmationDialog.newInstance(getString(R.string.dialog_delete_single_title),
                        getString(R.string.dialog_delete_single_message),
                        getString(R.string.dialog_delete_ok_button), getString(R.string.dialog_delete_cancel_button)).show(getSupportFragmentManager(), DIALOG_TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveClick(ConfirmationDialog dialog) {
        PersonsDatabase database = PersonsFirebaseDatabase.getInstance();
        database.deletePerson(mPersonKey);
        displayDeleteHintToast();
        finish();
    }

    private void displayDeleteHintToast() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getAppContext());
        if (!preferences.getBoolean("delete_hint_showed", false)) {
            Toast.makeText(BaseApplication.getAppContext(),
                    R.string.delete_several_hint_toast, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("delete_hint_showed", true);
            editor.apply();
        }
    }

    @Override
    public void onNegativeClick(ConfirmationDialog dialog) {
        dialog.dismiss();
    }
}
