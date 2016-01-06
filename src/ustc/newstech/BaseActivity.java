package ustc.newstech;

import ustc.custom.widget.ViewHolder;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {
	protected LinearLayout mLeftDrawer;
    protected ListView mDrawerList;
    private String[] mDrawerTitles;
    private Drawable[] mDrawerIcons=new Drawable[5];
	protected ListFragment mFrag;
	private TextView mTitle;
	private Button btn_clear;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		mLeftDrawer=(LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        initDrawerResource();

     // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		/*if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new SampleListFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}*/

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		//getSupportActionBar().setCustomView(R.layout.actionbar_custom);
		LinearLayout customActionBarView =(LinearLayout)LayoutInflater.from(this)
				.inflate(R.layout.custom_actionbar, null);		
		getSupportActionBar().setCustomView(customActionBarView,new ActionBar.LayoutParams( 
				ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		View customView=getSupportActionBar().getCustomView();
		btn_clear=(Button)customView.findViewById(R.id.btn_menu_clear);
		mTitle=(TextView)customView.findViewById(R.id.actionbar_title);		
		setTitle(getResources().getString(R.string.app_name));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_drawer));
        //getActionBar().setHomeButtonEnabled(false);   
		selectItem(0);
	}
	public void setOnBtnClearListener(OnClickListener listener){
		btn_clear.setOnClickListener(listener);
	}
	private void initDrawerResource(){
		mDrawerTitles = getResources().getStringArray(R.array.menu_array);
		mDrawerIcons[0]=getResources().getDrawable(R.drawable.ic_menu_home);
		mDrawerIcons[1]=getResources().getDrawable(R.drawable.ic_menu_search);
		mDrawerIcons[2]=getResources().getDrawable(R.drawable.ic_menu_star);
		mDrawerIcons[3]=getResources().getDrawable(R.drawable.ic_menu_recent_history);
		mDrawerIcons[4]=getResources().getDrawable(R.drawable.ic_menu_info_details);
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    protected void selectItem(int position) {        
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        if(position==1||position==3)btn_clear.setVisibility(View.VISIBLE);
        else btn_clear.setVisibility(View.INVISIBLE);
        toggle();
    }
    public void drawer_btn_click(View view){
    	toggle();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		//return true;
		return false;
	}
	private class DrawerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDrawerTitles.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView = LayoutInflater.from(BaseActivity.this)  
	                    .inflate(R.layout.drawer_list_item, null); 
			}
			ViewHolder holder=ViewHolder.get(convertView);
			TextView textView=(TextView)holder.getView(R.id.drawer_item_text);
			ImageView imgView=(ImageView)holder.getView(R.id.drawer_item_icon);
			textView.setText(mDrawerTitles[position]);
			imgView.setImageDrawable(mDrawerIcons[position]);
			return convertView;
		}    	
    }
	 @Override
    public void setTitle(CharSequence title) {
		 super.setTitle(title);
		 if(mTitle!=null)mTitle.setText(title);
    }  
}
