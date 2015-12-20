package ustc.newstech.discovery;

import ustc.newstech.R;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class DiscoveryFragment extends Fragment{
	public static final String ARG_DISCOVERY="ustc.arg.discovery";
	public static final String TAG="XXXDiscoveryFragment";
	private TextView query_status=null;
	private FrameLayout searchFragmentContainer=null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);
		query_status=(TextView)rootView.findViewById(R.id.query_status);
		searchFragmentContainer=(FrameLayout)rootView.findViewById(R.id.search_fragment_container);
		setupSearchView(rootView);
		return rootView;
	}	
	private void setupSearchView(View rootView){
		SearchManager searchManager =
		           (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
	            (SearchView)rootView.findViewById(R.id.search_view);
		searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getActivity().getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setQueryRefinementEnabled(true);		
		// Two ways to expand the search menu item in order to show by default the query
	    //searchView.setIconified(false); //has been set in xml
		//searchView.onActionViewExpanded();
		// remove the blue focus line under the searchview
		int searchPlateId = searchView.getContext().getResources()
	            .getIdentifier("android:id/search_plate", null, null);
	    View searchPlateView = searchView.findViewById(searchPlateId);
	    if (searchPlateView != null) {
	        searchPlateView.setBackgroundColor(getResources().getColor(R.color.gray_dark));
	        int searchTextId = searchPlateView.getContext().getResources()
	        		.getIdentifier("android:id/search_src_text", null, null);
		    TextView searchText = (TextView) searchPlateView.findViewById(searchTextId);
	        if (searchText!=null) {
	            searchText.setTextColor(Color.WHITE);
	            //searchText.setHintTextColor(Color.WHITE);
	        }
	    }
	    // remove the blue focus line under the search submit button
	    int submitId = searchView.getContext().getResources()
	            .getIdentifier("android:id/submit_area", null, null);
	    View submitView = searchView.findViewById(submitId);
	    if (submitView != null) {
	    	submitView.setBackgroundColor(getResources().getColor(R.color.gray_dark));
	    }	    
    }
	
 public void doNewsSearch(String query){
	 if(query!=null){
     	updateSuggestionProvider(query);
     	query_status.setText("\""+query+"\""+getResources().getString(R.string.search_result));
     	replaceSearchFragment(query);
     }  
 }
 private void updateSuggestionProvider(String query){
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
             MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
     suggestions.saveRecentQuery(query, null);
	}
public void clearSuggestionProvider(){
	SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
	        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
	suggestions.clearHistory();
}	

 private void replaceSearchFragment(String keywords){
		if (searchFragmentContainer == null) return;
		// Create fragment and give it an argument specifying the article it should show
		SearchFragment newFragment = new SearchFragment();
		Bundle args = new Bundle();
		args.putString(SearchFragment.ARG_KEYWORDS, keywords);
		newFragment.setArguments(args);

		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.search_fragment_container, newFragment);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();		
	}
}
