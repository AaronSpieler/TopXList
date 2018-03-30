package com.whynoteasy.topxlist.listActivities;

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
import com.whynoteasy.topxlist.object.XElemModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */

//TODO: in the activity call this method
/*
    newInstance(1,theListID)
*/
public class ListOfElementsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private static final String ARG_LIST_ID = "list-id";

    //repository Instance
    LocalDataRepository myRep;

    //the id of the lists whos elements are displayed
    int theListID;

    //List of the Elements
    //It is pulled onCreate, but should be updated every time it changes
    private List<XElemModel> listOfElements;

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

        myRep = new LocalDataRepository(getActivity());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            //this is where we get the listID from
            theListID = getArguments().getInt(ARG_LIST_ID);
        } else {
            //because if we didnt get the list id its pointless to continue
            System.err.println("The List of Elements Fragment did not get the List ID passed");
            System.exit(0);
        }

        //get the ListsWithTags for the Adapter
        listOfElements = myRep.getElementsByListID(theListID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xelem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            LOERecyclerViewAdapter tempAdapRef = new LOERecyclerViewAdapter(listOfElements, mListener, this.getActivity());
            recyclerView.setAdapter(tempAdapRef);

            //TODO: replace with own custom imtemTouchHelper
            //The ItemTouchHelperAnimation Stuff
            ElementTouchHelper swipeAndDragHelper = new ElementTouchHelper(tempAdapRef);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(XElemModel item);
    }
}
