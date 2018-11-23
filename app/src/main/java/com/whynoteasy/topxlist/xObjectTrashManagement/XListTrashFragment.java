package com.whynoteasy.topxlist.xObjectTrashManagement;

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
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.general.GeneralTrashTouchHelper;
import com.whynoteasy.topxlist.listActivities.LOLRecyclerViewAdapter;

import java.util.List;

/**
 * List of trashed xLists
 */

public class XListTrashFragment extends Fragment {

    //column count could be used so that on larger devices the cards are not stretched too wide
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LOTLRecyclerViewAdapter.OnTrashListFragmentInteractionListener mListener;
    private List<XListTagsSharesPojo> listOfListWithTags;
    private LOTLRecyclerViewAdapter adapterRef;

    //Interaction Types which the adapter can signal
    public static final int INTERACTION_CLICK = 0;
    public static final int INTERACTION_DELETE = 1;
    public static final int INTERACTION_MARK = 2;

    //Mandatory empty constructor
    public XListTrashFragment() {
    }

    public static XListTrashFragment newInstance(int columnCount) {
        XListTrashFragment fragment = new XListTrashFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataRepository myRep = DataRepository.getRepository();

        //get the trashed XLists with Tags and Shares for the Adapter
        listOfListWithTags = myRep.getTrashedXListsWithTagsShares();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        System.out.println("ON create trash done");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
            adapterRef = new LOTLRecyclerViewAdapter(listOfListWithTags, mListener, this.getActivity());
            recyclerView.setAdapter(adapterRef);

            //The ItemTouchHelperAnimation Stuff
            GeneralTrashTouchHelper swipeHelper = new GeneralTrashTouchHelper(adapterRef);
            ItemTouchHelper touchHelper = new ItemTouchHelper(swipeHelper);
            touchHelper.attachToRecyclerView((RecyclerView) view);

            System.out.println("ON create view trash done");

            return recyclerView;
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LOTLRecyclerViewAdapter.OnTrashListFragmentInteractionListener) {
            mListener = (LOTLRecyclerViewAdapter.OnTrashListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTrashListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMainListFragmentInteractionListener {
        void onListFragmentInteraction(LOLRecyclerViewAdapter lolAdapter, int position, int interactionType);
    }

    //This is to get the adapter reference through the fragment: needed so the main view can tell the adapter to redraw
    public LOTLRecyclerViewAdapter getAdapterRef(){
        return adapterRef;
    }

}
