package com.adori.personlistsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adori.personlistsample.util.ConfirmationDialog;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;

public class PersonListActivity extends AppCompatActivity implements ConfirmationDialog.Listener{

    private static final String FIREBASE_URL = "https://personlistsample.firebaseio.com/";
    private static final String DIALOG_TAG = "dialog-tag";

    private ArrayList<String> mSelectedKeys;
    private List<View> mSelectedViews;

    private boolean mDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonListActivity.this, PersonEditActivity.class));
            }
        });

        View recyclerView = findViewById(R.id.person_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        mSelectedKeys = new ArrayList<>();
        mSelectedViews = new ArrayList<>();

        if (savedInstanceState != null) {
            mDeleteMode = savedInstanceState.getBoolean("mDeleteMode");
            mSelectedKeys = savedInstanceState.getStringArrayList("mSelectedKeys");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mDeleteMode", mDeleteMode);
        outState.putStringArrayList("mSelectedKeys", mSelectedKeys);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Firebase firebase = new Firebase(FIREBASE_URL).child("persons");
        firebase.keepSynced(true);
        PersonListAdapter adapter = new PersonListAdapter(firebase.limitToFirst(50), Person.class,
                R.layout.person_list_content, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switchDeleteMode(false);
                return true;
            case R.id.action_delete_bulk:
                ConfirmationDialog.newInstance(getString(R.string.dialog_delete_bulk_title),
                        getString(R.string.dialog_delete_bulk_message),
                        getString(R.string.dialog_delete_bulk_ok_button), getString(R.string.dialog_delete_bulk_cancel_button)).show(getSupportFragmentManager(), DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPositiveClick(ConfirmationDialog dialog) {
        for (String key : mSelectedKeys) {
            PersonsDatabase database = PersonsFirebaseDatabase.getInstance();
            database.deletePerson(key);
        }
        switchDeleteMode(false);
    }

    @Override
    public void onNegativeClick(ConfirmationDialog dialog) {
        dialog.dismiss();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(mDeleteMode);
        menu.getItem(0).setVisible(mDeleteMode);

        return true;
    }

    protected void switchDeleteMode(boolean enabled) {
        mDeleteMode = enabled;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enabled);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (enabled) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
            for (View v : mSelectedViews) {
                v.setAlpha(0);
            }
            mSelectedViews.clear();
            mSelectedKeys.clear();
        }
        invalidateOptionsMenu();
    }

    public class PersonListAdapter extends FirebaseListAdapter<PersonListAdapter.PersonsViewHolder, Person> {

        /**
         * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
         *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
         * @param mModelClass Firebase will marshall the data at a location into an instance of a class that you provide
         * @param mLayout     This is the mLayout used to represent a single list item. You will be responsible for populating an
         *                    instance of the corresponding view with the data from an instance of mModelClass.
         * @param activity    The activity containing the ListView
         */
        public PersonListAdapter(Query mRef, Class<Person> mModelClass, int mLayout, Activity activity) {
            super(mRef, mModelClass, mLayout, activity);
        }

        private void handleSelection(String key, View v) {
            if (mSelectedKeys.contains(key)) {
                v.setAlpha(0);
                mSelectedKeys.remove(key);
                mSelectedViews.remove(v);
                if (mSelectedKeys.size() == 0) {
                    switchDeleteMode(false);
                }
            } else {
                v.setAlpha(1);
                mSelectedKeys.add(key);
                mSelectedViews.add(v);
            }
        }

        @Override
        protected void populateView(final PersonsViewHolder holder, final Person model, final String key) {
            holder.mPerson = model;
            String fullName = model.firstName + " " + model.lastName;
            holder.mNameView.setText(fullName);

            if (mSelectedKeys.contains(key)) {
                View checkView = holder.mView.findViewById(R.id.select_image);
                checkView.setAlpha(1);
            }

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!mDeleteMode) {
                        switchDeleteMode(true);
                    }
                    View checkView = view.findViewById(R.id.select_image);
                    handleSelection(key, checkView);
                    return true;
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDeleteMode) {
                        View checkView = view.findViewById(R.id.select_image);
                        handleSelection(key, checkView);
                    } else {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, PersonDetailActivity.class);
                        intent.putExtra(PersonDetailFragment.ARG_KEY, key);
                        intent.putExtra(PersonDetailFragment.ARG_FIRST_NAME, model.firstName);
                        intent.putExtra(PersonDetailFragment.ARG_LAST_NAME, model.lastName);
                        intent.putExtra(PersonDetailFragment.ARG_DOB, model.dob);
                        intent.putExtra(PersonDetailFragment.ARG_ZIP_CODE, model.zipCode);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public PersonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_list_content, parent, false);
            return new PersonsViewHolder(view);
        }

        public class PersonsViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView mNameView;
            public Person mPerson;

            public PersonsViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }
}
