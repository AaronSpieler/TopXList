package com.whynoteasy.topxlist.elemActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataObjects.XElemModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
*/

public class ListOfElementsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private static final String ARG_LIST_ID = "list-id";

    //the id of the lists who's elements are displayed
    private int theListID;

    //List of the Elements
    //It is pulled onCreate, but should be updated every time it changes
    private List<XElemModel> listOfElements;

    //Interaction Types which the adapter can signal
    public static final int INTERACTION_CLICK = 0;
    public static final int INTERACTION_DELETE = 1;
    public static final int INTERACTION_MARK = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListOfElementsFragment() {
    }

    public static ListOfElementsFragment newInstance(int columnCount, int listID) {
        ListOfElementsFragment fragment = new ListOfElementsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_LIST_ID, listID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataRepository myRep = DataRepository.getRepository();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            //this is where we get the listID from
            theListID = getArguments().getInt(ARG_LIST_ID);
        } else {
            //because if we didn't get the list id its pointless to continue
            System.err.println("The List of Elements Fragment did not get the List ID passed");
            System.exit(0);
        }

        //get the ListsWithTags for the Adapter
        listOfElements = myRep.getElementsByListID(theListID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loe, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            LOERecyclerViewAdapter loeAdapter= new LOERecyclerViewAdapter(listOfElements, mListener, this.getActivity());
            recyclerView.setAdapter(loeAdapter);

            //The ItemTouchHelperAnimation Stuff
            ElementTouchHelper swipeAndDragHelper = new ElementTouchHelper(loeAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
            touchHelper.attachToRecyclerView(recyclerView);

            //For apparent smoothness, but it may make the recycler view uncrossable???
            //recyclerView.setNestedScrollingEnabled(false);

            return recyclerView;
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
                    + " must implement OnMainListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //So the activity can listen for changes happening in the fragment
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(LOERecyclerViewAdapter loeAdapter, int position, int interactionType);
    }
}
