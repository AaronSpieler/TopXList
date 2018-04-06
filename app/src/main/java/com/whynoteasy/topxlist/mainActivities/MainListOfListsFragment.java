package com.whynoteasy.topxlist.mainActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MainListOfListsFragment extends Fragment {

    //column count could be used so that on larger devices the cards are not stretched too wide
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;

    private List<XListTagsPojo> listOfListWithTags;

    private LOLRecyclerViewAdapter adapterRef;

    //Mandatory empty constructor
    public MainListOfListsFragment() {
    }

    public static MainListOfListsFragment newInstance(int columnCount) {
        MainListOfListsFragment fragment = new MainListOfListsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDataRepository myRep = new LocalDataRepository(getActivity());

        //get the ListsWithTags for the Adapter
        listOfListWithTags = myRep.getListsWithTags();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lol, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //Instantiate and set the adapter
            adapterRef = new LOLRecyclerViewAdapter(listOfListWithTags, mListener, this.getActivity());
            recyclerView.setAdapter(adapterRef);

            //The ItemTouchHelperAnimation Stuff
            ListTouchHelper swipeAndDragHelper = new ListTouchHelper(adapterRef);
            ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
            touchHelper.attachToRecyclerView((RecyclerView) view);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(XListTagsPojo item);
    }

    //This is to get the adapter reference through the fragment: needed so the main view can tell the adapter to redraw
    public LOLRecyclerViewAdapter getAdapterRef(){
        return adapterRef;
    }

}
