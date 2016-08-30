package com.adori.personlistsample;

import android.app.Activity;
import android.database.Cursor;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adori.personlistsample.dummy.DummyContent;

/**
 * A fragment representing a single Person detail screen.
 * This fragment is either contained in a {@link PersonListActivity}
 * in two-pane mode (on tablets) or a {@link PersonDetailActivity}
 * on handsets.
 */
public class PersonDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_FIRST_NAME = "first_name";
    public static final String ARG_LAST_NAME = "last_name";
    public static final String ARG_DOB = "dob";
    public static final String ARG_ZIP_CODE = "zip_code";
    public static final String ARG_KEY = "key";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mKey;
    private Person mPerson;
    private TextView mDobView;
    private TextView mZipCodeView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mKey = getArguments().getString(ARG_KEY);
            String firstName = getArguments().getString(ARG_FIRST_NAME);
            String lastName = getArguments().getString(ARG_LAST_NAME);
            String dob = getArguments().getString(ARG_DOB);
            int zipCode = getArguments().getInt(ARG_ZIP_CODE);

            mPerson = new Person(firstName, lastName, dob, zipCode);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.person_detail, container, false);

        if (mPerson != null) {
            mDobView = (TextView) rootView.findViewById(R.id.dob_text_view);
            mZipCodeView = (TextView) rootView.findViewById(R.id.zip_code_text_view);

            updateData(mPerson);
        }

        return rootView;
    }

    public void updateData(Person person) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(person.firstName + " " + person.lastName);
        }

        mDobView.setText(mPerson.dob);
        mZipCodeView.setText(String.valueOf(mPerson.zipCode));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
