package ustc.newstech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
	private String[] category_values=null,category_entries=null;
	
	public CollectionPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	public CollectionPagerAdapter(FragmentManager fm,Context context){
		super(fm);
		category_entries=context.getResources().getStringArray(R.array.category_entry);
		category_values=context.getResources().getStringArray(R.array.category_value);   		
	}
	@Override
    public Fragment getItem(int i) {
        Fragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putString(NewsListFragment.ARG_TYPE, category_values[i]); 
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
    	return category_values.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
    	return category_entries[position];
    }
}

