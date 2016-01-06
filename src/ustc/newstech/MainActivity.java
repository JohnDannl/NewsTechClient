package ustc.newstech;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.actionbarsherlock.view.Menu;

import ustc.bitmap.imagecache.ImageCache;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.newstech.about.AboutFragment;
import ustc.newstech.data.Constant;
import ustc.newstech.database.NewsTechDBHelper;
import ustc.newstech.discovery.DiscoveryFragment;
import ustc.newstech.history.HistoryFragment;
import ustc.newstech.login.LoginHelper;
import ustc.newstech.login.VolunteerFragment;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import ustc.utils.update.UpdateManager;


import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends BaseActivity {
	private static final String TAG="XXXMainActivity";
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "thumbs";
	private UpdateManager mUpdateManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);        
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//      final int height = displayMetrics.heightPixels;
		int screenHeight = displayMetrics.heightPixels;
		//Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", height,width));
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, screenHeight/5);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);//will revoke the background empty image
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        handleIntent(getIntent());        
        //Log.d(TAG,"onCreate()");
        if(MyApplication.start_launch){
			MyApplication.start_launch=false; //To ensure just to execute when app launches
			mUpdateManager = new UpdateManager(this);	        
	        mUpdateManager.checkUpdateInfo();
		}
	}	
	@Override
    protected void onNewIntent(Intent intent) {
		setIntent(intent);
        handleIntent(intent);
        //Log.d(TAG,"onNewIntent()");
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		//return true;
		return false;
	}   
	 @Override
	    public void onResume() {			
	        super.onResume();  
	        if(mImageFetcher!=null)mImageFetcher.setExitTasksEarly(false);
	        //Log.d(TAG, "onResume()");
	    }

	    @Override
	    protected void onPause() {	    	
	        super.onPause();
	        if(mImageFetcher!=null){
	        	mImageFetcher.setExitTasksEarly(true);
	    	    mImageFetcher.flushCache();
	        }
	        //Log.d(TAG, "onPause()");
	    }
	    @Override
	    protected void onStop(){
	    	super.onStop();
	    	//Log.d(TAG, "onStop()");
	    }
	    @Override
	    protected void onDestroy() {    	
	        super.onDestroy();    
	        if(mImageFetcher!=null)mImageFetcher.closeCache();
	        //Log.d(TAG, "onDestroy()");
	    }
	    @Override
	    protected void selectItem(int position){
	    	super.selectItem(position);
	    	// update the main content by replacing fragments
	    	Fragment fragment;
	    	Bundle args;
	    	FragmentManager fragmentManager = getSupportFragmentManager();
	    	switch(position){
	    		case 0:
	    			fragment =new HomePageFragment();
	    			args = new Bundle();
	    	        args.putInt(HomePageFragment.ARG_HOME_PAGE, position);
	    	        fragment.setArguments(args);
	    	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	    	        break;
	    		case 1:
	    			fragment =new DiscoveryFragment();
	    			args =new Bundle();
	    			args.putInt(DiscoveryFragment.ARG_DISCOVERY, position);
	    			fragment.setArguments(args);    
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,
	    					DiscoveryFragment.TAG).commit();
	    			break;
	    		case 2:
	    			fragment =new VolunteerFragment();
	    			args =new Bundle();
	    			args.putInt(VolunteerFragment.ARG_OBJECT, position);
	    			fragment.setArguments(args);    
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,
	    					VolunteerFragment.TAG).commit();
	    			break;
	    		case 3:
	    			fragment =new HistoryFragment();
	    			args =new Bundle();
	    			args.putInt(HistoryFragment.ARG_OBJECT, position);
	    			fragment.setArguments(args);    
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,
	    					HistoryFragment.TAG).commit();
	    			break;
	    		case 4:
	    			fragment =new AboutFragment();
	    			args =new Bundle();
	    			args.putInt(AboutFragment.ARG_OBJECT, position);
	    			fragment.setArguments(args);    
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,
	    					AboutFragment.TAG).commit();
	    			break;
	    		default:
	    			fragment = new ObjectFragment();
	    	        args = new Bundle();
	    	        args.putInt(ObjectFragment.ARG_OBJECT, position);
	    	        fragment.setArguments(args);
	    	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	    	        break;
	    	}   

	    }
	    public ImageFetcher getImageFetcher(){
			return mImageFetcher;
		}
	    private void handleIntent(Intent intent) {
	        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	            String query = intent.getStringExtra(SearchManager.QUERY);
	            //use the query to search your data somehow
	            if(query!=null){
	            	DiscoveryFragment discFrag=(DiscoveryFragment)getSupportFragmentManager()
	            			.findFragmentByTag(DiscoveryFragment.TAG);
	            	if(discFrag==null)return;
	            	discFrag.doNewsSearch(query);
	            }            
	        }
	    }
	    public String getUserId(){
			 String userId=Network.getMacAddress(this);
			 if(userId==null)userId= AndroidDeviceId.getUUId(this);
			 if(userId==null)userId=Constant.pa_anonymous;
			 return userId;
		 }		    
}
