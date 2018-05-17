package edu.dartmouth.cs.havvapa;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import edu.dartmouth.cs.havvapa.models.Content;

public class WhatsNewActivity extends AppCompatActivity
{
    private static final String TAG = "FragmentLayoutACTIVITY";

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, " onCreate()");

        Toast.makeText(this, "FragmentLayout: OnCreate()", Toast.LENGTH_SHORT)
                .show();

        // Sets the view. Depending on orientation it will select either
        // res/layout/fragment_layout.xml (portrait mode) or
        // res/layout-land/fragment_layout.xml (landscape mode). This is done
        // automatically by the system.
        setContentView(R.layout.activity_whats_new);
        getSupportActionBar().setTitle("MainActivity");  // Set the title. I don't know how it can give null pointer exception.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // FragmentLayout activity's Lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, " onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, " onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, " onRestart()");
    }

// This is a secondary activity, to show what the user has selected when the
    // screen is not large enough to show it all in one activity.

    public static class DetailsActivity extends Activity {

        private static final String TAG = "DetailsACTIVITY";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d(TAG, " onCreate()");

            Toast.makeText(this, "DetailsActivity", Toast.LENGTH_SHORT).show();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.

                // create fragment
                DetailsFragment details = new DetailsFragment();

                // get and set the position input by user (i.e., "index")
                // which is the construction arguments for this fragment
                details.setArguments(getIntent().getExtras());

                //
                getFragmentManager().beginTransaction()
                        .add(android.R.id.content, details).commit();
            }
        }

        // DetailsActivity Lifecycle

        @Override
        protected void onStart() {
            super.onStart();
            Log.d(TAG, " onStart()");
        }

        @Override
        protected void onResume() {
            super.onResume();
            Log.d(TAG, " onResume()");
        }

        @Override
        protected void onPause() {
            super.onPause();
            Log.d(TAG, " onPause()");
        }

        @Override
        protected void onStop() {
            super.onStop();
            Log.d(TAG, " onStop()");
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            Log.d(TAG, " onDestroy()");
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            Log.d(TAG, " onRestart()");
        }


    }

    // This is the "top-level" fragment, showing a list of items that the user
    // can pick. Upon picking an item, it takes care of displaying the data to
    // the user as appropriate based on the current UI layout.

    // Displays a list of items that are managed by an adapter similar to
    // ListActivity. It provides several methods for managing a list view, such
    // as the onListItemClick() callback to handle click events.

    public static class TitlesFragment extends ListFragment {
        private static final String TAG = "TitlesFRAGMENT";
        boolean mDualPane;
        int mCurCheckPosition = 0;

        // onActivityCreated() is called when the activity's onCreate() method
        // has returned.

        // TitlesFragment's Lifecycle

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d(TAG, "onAttach()");
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate()");
        }

//        		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//			Log.d(TAG, "onCreateView()");
//
//		}


        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "onStart()");
        }

        @Override
        public void onResume() {
            Log.d(TAG, "onResume()");
            super.onResume();
        }


        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause()");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onStop()");

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.d(TAG, " onDestroyView()");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.d(TAG, " onDetach()");
        }




        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Log.d(TAG, "onActivityCreated()");

            // You can use getActivity(), which returns the activity associated
            // with a fragment.
            // The activity is a context (since Activity extends Context) .

            Toast.makeText(getActivity(), "TitlesFragment:onActivityCreated",
                    Toast.LENGTH_LONG).show();

            // Populate list with our static array of titles in list in the
            // Shakespeare class
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    Content.CATEGORIES));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            // R.id.details relates to the res/layout-land/fragment_layout.xml
            // This is first created when the phone is switched to landscape
            // mode

            View detailsFrame = getActivity().findViewById(R.id.details);

            Toast.makeText(getActivity(), "detailsFrame " + detailsFrame,
                    Toast.LENGTH_LONG).show();

            // Check that a view exists and is visible
            // A view is visible (0) on the screen; the default value.
            // It can also be invisible and hidden, as if the view had not been
            // added.
            //
            mDualPane = detailsFrame != null
                    && detailsFrame.getVisibility() == View.VISIBLE;

            Toast.makeText(getActivity(), "mDualPane " + mDualPane,
                    Toast.LENGTH_LONG).show();

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected
                // item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                showDetails(mCurCheckPosition);
            } else {
                // We also highlight in uni-pane just for fun
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(mCurCheckPosition, true);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            Toast.makeText(getActivity(), "onSaveInstanceState",
                    Toast.LENGTH_LONG).show();

            outState.putInt("curChoice", mCurCheckPosition);
        }

        // If the user clicks on an item in the list (e.g., Henry V then the
        // onListItemClick() method is called. It calls a helper function in
        // this case.

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {

            Toast.makeText(getActivity(),
                    "onListItemClick position is" + position, Toast.LENGTH_LONG)
                    .show();

            showDetails(position);
        }

        // Helper function to show the details of a selected item, either by
        // displaying a fragment in-place in the current UI, or starting a whole
        // new activity in which it is displayed.

        void showDetails(int index) {
            mCurCheckPosition = index;

            // The basic design is mutli-pane (landscape on the phone) allows us
            // to display both fragments (titles and details) with in the same
            // activity; that is FragmentLayout -- one activity with two
            // fragments.
            // Else, it's single-pane (portrait on the phone) and we fire
            // another activity to render the details fragment - two activities
            // each with its own fragment .
            //
            if (mDualPane) {
                // We can display everything in-place with fragments, so update
                // the list to highlight the selected item and show the data.
                // We keep highlighted the current selection
                getListView().setItemChecked(index, true);

                // Check what fragment is currently shown, replace if needed.
                DetailsFragment details = (DetailsFragment) getFragmentManager()
                        .findFragmentById(R.id.details);
                if (details == null || details.getShownIndex() != index) {
                    // Make new fragment to show this selection.

                    details = DetailsFragment.newInstance(index);

                    Toast.makeText(getActivity(),
                            "showDetails dual-pane: create and relplace fragment",
                            Toast.LENGTH_LONG).show();

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager()
                            .beginTransaction();
                    ft.replace(R.id.details, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                // That is: if this is a single-pane (e.g., portrait mode on a
                // phone) then fire DetailsActivity to display the details
                // fragment

                // Create an intent for starting the DetailsActivity
                Intent intent = new Intent();

                // explicitly set the activity context and class
                // associated with the intent (context, class)
                intent.setClass(getActivity(), DetailsActivity.class);

                // pass the current position
                intent.putExtra("index", index);

                startActivity(intent);
            }
        }
    }

    // This is the secondary fragment, displaying the details of a particular
    // item.

    public static class DetailsFragment extends Fragment {

        private static final String TAG = "DetailsFRAGMENT";
        // Create a new instance of DetailsFragment, initialized to show the
        // text at 'index'.

        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        // DetailsFragment LifeCycle
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d(TAG, "onAttach()");
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate()");
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "onStart()");
        }

        @Override
        public void onResume() {
            Log.d(TAG, "onResume()");
            super.onResume();
        }


        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause()");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onStop()");

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.d(TAG, " onDestroyView()");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.d(TAG, " onDetach()");
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        // The system calls this when it's time for the fragment to draw its
        // user interface for the first time. To draw a UI for your fragment,
        // you must return a View from this method that is the root of your
        // fragment's layout. You can return null if the fragment does not
        // provide a UI.

        // We create the UI with a scrollview and text and return a reference to
        // the scoller which is then drawn to the screen

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Toast.makeText(getActivity(), "DetailsFragment:onCreateView",
                    Toast.LENGTH_LONG).show();

            // programmatically create a scrollview and texview for the text in
            // the container/fragment layout. Set up the properties and add the
            // view.

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
                            .getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            text.setText(Content.ARTICLES[getShownIndex()]);
            return scroller;
        }
    }

}
