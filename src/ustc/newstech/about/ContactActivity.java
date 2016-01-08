package ustc.newstech.about;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ustc.newstech.R;
import ustc.newstech.data.Constant;
import ustc.newstech.login.LoginHelper;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactActivity extends Activity {
	private static final String TAG="XXXContactActivity";
	private EditText emailView,suggestionView;
	private String email,suggestion,userid;
	private ContactTask contactTask=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		emailView=(EditText)findViewById(R.id.contact_email);
		suggestionView=(EditText)findViewById(R.id.contact_feedback);
		suggestionView.setMovementMethod(new ScrollingMovementMethod());
		Button btn_submit=(Button)findViewById(R.id.btn_submit_contact);
		btn_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(ContactActivity.this, "Submit", Toast.LENGTH_SHORT).show();
				attemptSubmit();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact, menu);
		return false;
		//return true;
	}
	/**
	 * @return "Submit successfully" if success
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String submitFeedback() throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(Constant.suggestHost);	
		DefaultHttpClient httpClient = new DefaultHttpClient();
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();	
		nvps.add(new BasicNameValuePair("userid", userid));
		nvps.add(new BasicNameValuePair("email", email));
		nvps.add(new BasicNameValuePair("suggestion",suggestion));
		Log.d(TAG,userid+email+suggestion);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpClient.execute(httpPost);		
	    int httpcode=response.getStatusLine().getStatusCode();
	    return EntityUtils.toString(response.getEntity());	
	}
	private void attemptSubmit(){
		if (contactTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		// Reset errors.
		emailView.setError(null);
		suggestionView.setError(null);
		// Store values at the time of the login attempt
		email = emailView.getText().toString();
		suggestion = suggestionView.getText().toString();
		userid=AndroidDeviceId.getAndroidId(this);
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			emailView.setError(getString(R.string.error_field_required));
			focusView = emailView;
			cancel = true;
		} else if (!email.contains("@")) {
			emailView.setError(getString(R.string.error_invalid_email));
			focusView = emailView;
			cancel = true;
		}
		if (TextUtils.isEmpty(suggestion)) {
			suggestionView.setError(getString(R.string.error_field_required));
			focusView = suggestionView;
			cancel = true;
		}		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.	
			contactTask=new ContactTask();
			contactTask.execute();
		}
	}
	private class ContactTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result=null;
			try {
				result=submitFeedback();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result){
			if(result!=null){
				if(result.equals("Submit successfully")){
					Toast.makeText(ContactActivity.this, getResources().getString(R.string.success_submit), Toast.LENGTH_SHORT).show();
					suggestionView.setText("");					
				}
			}else{
				if(Network.checkConnection(ContactActivity.this))
					Toast.makeText(ContactActivity.this, getResources().getString(R.string.failure_submit), Toast.LENGTH_SHORT).show();
				}	
			contactTask=null;
		}
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
}
