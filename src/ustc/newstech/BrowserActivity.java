package ustc.newstech;

import ustc.utils.Network;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserActivity extends Activity {
	private static final String TAG="XXXBrowserActivity";
	public static final String ARG_URL="ustc.newstech.arg_url";
	private ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		
		setTitle(getResources().getString(R.string.app_name));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);  
		
		addActionBarProgressBar();
		WebView webView=(WebView)findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		//getWindow().requestFeature(Window.FEATURE_PROGRESS);
		webView.setWebChromeClient(new WebChromeClient(){
			 public void onProgressChanged(WebView view, int progress) {
			     // Activities and WebViews measure progress with different scales.
			     // The progress meter will automatically disappear when we reach 100%
				 progressBar.setProgress(progress);
			   }
		});
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if(Network.checkConnection(BrowserActivity.this))
					view.loadData(getResources().getString(R.string.page_not_found),"text/html;charset=UTF-8",null);
				else
					view.loadData(getResources().getString(R.string.network_connection_failure),"text/html;charset=UTF-8",null);
			   }
			@Override
			public boolean shouldOverrideUrlLoading (WebView view, String url){
				return false;
			}
		});
		Intent intent = getIntent();
		String url=intent.getStringExtra(ARG_URL);
		webView.loadUrl(url);
		//Log.d(TAG,"onCreate()");
	}

	private void addActionBarProgressBar(){
		progressBar=new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,10));
		progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_drawable));
		// retrieve the top view of our application
		final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
		decorView.addView(progressBar);

		// Here we try to position the ProgressBar to the correct position by looking
		// at the position where content area starts. But during creating time, sizes 
		// of the components are not set yet, so we have to wait until the components
		// has been laid out
		// Also note that doing progressBar.setY(136) will not work, because of different
		// screen densities and different sizes of actionBar
		ViewTreeObserver observer = progressBar.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        View contentView = decorView.findViewById(android.R.id.content);
		        progressBar.setY(contentView.getY() - 10);

		        ViewTreeObserver observer = progressBar.getViewTreeObserver();
		        observer.removeGlobalOnLayoutListener(this);
		    }
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browser, menu);
		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
    protected void onResume(){
		super.onResume();
		//Log.d(TAG,"onResume()");
	}
	@Override
    protected void onStop(){
		super.onStop();
		//Log.d(TAG,"onStop()");
	}
	@Override
    protected void onDestroy(){
		super.onDestroy();
		//Log.d(TAG,"onDestroy()");
	}

}
