package com.whynoteasy.topxlist.general;

import android.widget.Filter;
import android.widget.Filterable;

import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//this is so the lists can be filtered
public class CustomListFilter extends Filter {

    CustomFilterContract filterAdapter;
    boolean trashMode;

    public CustomListFilter(CustomFilterContract filterAdapter, boolean trashMode) {
        this.filterAdapter = filterAdapter;
        this.trashMode = trashMode;
    }

    public interface CustomFilterContract {
        void publishResults(List<XListTagsSharesPojo> values);
    }

    @Override
    public FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        DataRepository myRep = DataRepository.getRepository();
        List<XListTagsSharesPojo> allLists = null;
        if (!trashMode) {
            allLists = myRep.getListsWithTagsShares();
        } else {
            allLists = myRep.getTrashedXListsWithTagsShares();
        }
        //We dont want duplicated and we want the set to be ordered by insertion order
        LinkedHashSet<XListTagsSharesPojo> searchResults = new LinkedHashSet<>();

        String query = constraint.toString().toLowerCase().trim();

        //use the query to search your data somehow
        if (query.startsWith("#") && !query.contains(" ")) {
            //Filter results based on a single hashtag
            for (XListTagsSharesPojo tempList : allLists) {
                if (tempList.tagsToString().toLowerCase().contains(query)) {
                    searchResults.add(tempList);
                }
            }
        } else {
            //Filter results based on words:
            //priority one: hashtag
            //priority two: title
            //priority three: short description
            //priority four: long description

            //Split the search query by spaces:
            String[] searchTokens = query.split("\\s+");

            //priority one: hashtag
            for (String token : searchTokens) {
                for (XListTagsSharesPojo tempList : allLists) {
                    if (tempList.tagsToString().toLowerCase().contains(query)) {
                        searchResults.add(tempList);
                    }
                }
            }

            //priority two: title
            for (String token : searchTokens) {
                for (XListTagsSharesPojo tempList : allLists) {
                    if (tempList.getXListModel().getXListTitle().toLowerCase().contains(token)) {
                        searchResults.add(tempList);
                    }
                }
            }

            //priority three: short description
            for (String token : searchTokens) {
                for (XListTagsSharesPojo tempList : allLists) {
                    if (tempList.getXListModel().getXListShortDescription().toLowerCase().contains(token)) {
                        searchResults.add(tempList);
                    }
                }
            }

            //priority four: long description
            for (String token : searchTokens) {
                for (XListTagsSharesPojo tempList : allLists) {
                    if (tempList.getXListModel().getXListLongDescription().toLowerCase().contains(token)) {
                        searchResults.add(tempList);
                    }
                }
            }
        }
        //Convert the LinkedHashset to ArrayList
        ArrayList<XListTagsSharesPojo> tempResult = new ArrayList<>();
        tempResult.addAll(0,searchResults);

        //set the results
        results.values = tempResult;
        results.count = tempResult.size();

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publishResults(CharSequence constraint, FilterResults results) {
        filterAdapter.publishResults((List<XListTagsSharesPojo>) results.values);
    }
}
