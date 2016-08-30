package com.adori.personlistsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.adori.personlistsample.util.ConfirmationDialog;

/**
 * An activity representing a single Person detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PersonListActivity}.
 */
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

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
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
                ConfirmationDialog.newInstance("Delete person",
                        "Are you sure you want to delete this person?",
                        "Delete", "Cancel").show(getSupportFragmentManager(), DIALOG_TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveClick(ConfirmationDialog dialog) {
        PersonsFirebaseDatabase database = PersonsFirebaseDatabase.getInstance();
        database.deletePerson(mPersonKey);
        finish();
    }

    @Override
    public void onNegativeClick(ConfirmationDialog dialog) {
        dialog.dismiss();
    }

    private void updateData(Intent intent) {
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
                .commitAllowingStateLoss();
    }
}
