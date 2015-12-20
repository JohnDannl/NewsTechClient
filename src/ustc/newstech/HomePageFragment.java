package ustc.newstech;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class HomePageFragment extends Fragment {
	public static final String ARG_HOME_PAGE="ustc.homepage.arg";
	private CollectionPagerAdapter mCollectionPagerAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;
    public static final String TAB_HOT = "TAB_HOT";
    public static final String TAB_NEWEST = "TAB_NEWEST";
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.
	     View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
	     mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
	     mTabHost.setup();
	     initializeTabs(rootView,inflater);
	     mTabHost.setOnTabChangedListener(listener);
	     mCollectionPagerAdapter = new CollectionPagerAdapter(getActivity().getSupportFragmentManager(),getActivity());		     
	     mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
         mViewPager.setAdapter(mCollectionPagerAdapter);
         mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
             @Override
             public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
            	// corresponding tab.
                mTabHost.setCurrentTab(position);
             }
         });
	     
	     return rootView;
	 }
	TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {
             if (tabId.equals(TAB_NEWEST)) {
            	mViewPager.setCurrentItem(0);
            }else if (tabId.equals(TAB_HOT)) {
            	mViewPager.setCurrentItem(1);
            }
        }
    };
    public void initializeTabs(final View rootView,LayoutInflater inflater) {

        TabHost.TabSpec spec;
        
        spec = mTabHost.newTabSpec(TAB_NEWEST);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
            	return rootView.findViewById(R.id.viewpager);
            }
        });
        spec.setIndicator(createTabView(inflater,getString(R.string.newest)));
        mTabHost.addTab(spec);
        
        spec = mTabHost.newTabSpec(TAB_HOT);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return rootView.findViewById(R.id.viewpager);
            }
        });
        spec.setIndicator(createTabView(inflater,getString(R.string.hot)));
        mTabHost.addTab(spec);        
    }
    private View createTabView(LayoutInflater inflater, final String text) {
        View view = inflater.inflate(R.layout.custom_tab, null);
        /*ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageResource(id);*/
//        imageView.setImageDrawable(getResources().getDrawable(id));
        TextView textView = (TextView) view.findViewById(R.id.tab_text);
        textView.setText(text);
        return view;
    }
}
