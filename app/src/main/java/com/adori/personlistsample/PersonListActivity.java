package com.adori.personlistsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.adori.personlistsample.dummy.DummyContent;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Persons. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PersonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PersonListActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://personlistsample.firebaseio.com/";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private List<String> mSelectedKeys;
    private List<View> mSelectedViews;

    private boolean mDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getApplicationContext());

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

        if (findViewById(R.id.person_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Firebase firebase = new Firebase(FIREBASE_URL).child("persons");
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
                for (String key : mSelectedKeys) {
                    PersonsFirebaseDatabase database = PersonsFirebaseDatabase.getInstance();
                    database.deletePerson(key);
                }
                switchDeleteMode(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                   /* if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PersonDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        PersonDetailFragment fragment = new PersonDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.person_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, PersonDetailActivity.class);
                        intent.putExtra(PersonDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }*/
                }
            });
        }

        @Override
        public PersonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_list_content, parent, false);
            return new PersonsViewHolder(view);
        }

    /*
    * holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PersonDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        PersonDetailFragment fragment = new PersonDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.person_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PersonDetailActivity.class);
                        intent.putExtra(PersonDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });*/

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
