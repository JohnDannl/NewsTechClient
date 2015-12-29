package ustc.newstech.login;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;

import ustc.newstech.R;
import ustc.utils.AndroidDeviceId;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends BaseFragment{
	public static final String TAG="XXXLoginFragment";
	public static final String ARG_OBJECT = "object";		
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView,registerLinkView;
	private EditText nameView,passwordView,emailView;
	private Button btn_logout,btn_login;
	private AuthTask mAuthTask=null;	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
	    int index=args.getInt(ARG_OBJECT);
	}	
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.	
		 initializeLoginInfo();
	     final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
	     mLoginFormView = rootView.findViewById(R.id.login_form);
		 mLoginStatusView = rootView.findViewById(R.id.login_status);
		 mLoginStatusMessageView = (TextView)rootView.findViewById(R.id.login_status_message);
		 nameView=(EditText)rootView.findViewById(R.id.name);
		 passwordView=(EditText)rootView.findViewById(R.id.password);
		 if(name!=null)nameView.setText(name);
		 if(password!=null)passwordView.setText(password);
		 btn_login=(Button)rootView.findViewById(R.id.btn_login);
		 btn_login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptLogin();				
			}
			 
		 });
		 registerLinkView=(TextView)rootView.findViewById(R.id.register_link);
		 registerLinkView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.showRegister();
			}			 
		 });	     
	     return rootView;
	 }
	private void setRgisterLinkViewText(String text){
		String styledText = "<u><font color='black'>"+text+"</font></u>";
		registerLinkView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
	}
	private boolean checkLogin(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		Log.d(TAG, sharedPref.getString("name", null)+":"
				+sharedPref.getString("password", null)+":"
				+sharedPref.getString("userid", null)+":"
				+sharedPref.getString("cookie", null));
		if(sharedPref.getString("name", null)!=null
				&&sharedPref.getString("password", null)!=null
				&&sharedPref.getString("userid", null)!=null
				&&sharedPref.getString("cookie", null)!=null)
			return true;
		else return false;
	}
	private void storeUserInfo(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor=sharedPref.edit();	
		editor.putString("name", name);
		editor.putString("userid",userid);
		editor.putString("password", password);
		editor.commit();
	}
	private void putCookie(Context context,String cookie){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor=sharedPref.edit();	
		editor.putString("cookie",cookie);
		editor.commit();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}		
	}
	private class AuthTask extends AsyncTask<Void,Void,String>{
		@Override
		protected void onPreExecute(){				
			showProgress(true);
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return loginHelper.login();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result){	
			showProgress(false);
			if(result!=null){
				if(result.equals("Login successfully")){
					Toast.makeText(getActivity(), getResources().getString(R.string.success_login), Toast.LENGTH_SHORT).show();
					putCookie(getActivity(),loginHelper.getCookie());
					listener.showLogout();
				}else if(result.equals("User not exists")){
					Toast.makeText(getActivity(), getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
					listener.showRegister();
				}else{					
					Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
				}
			}else{
					Toast.makeText(getActivity(), getResources().getString(R.string.failure_login), Toast.LENGTH_SHORT).show();
				}				
			mAuthTask = null;
		}
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		// Reset errors.		
		nameView.setError(null);
		passwordView.setError(null);
		// Store values at the time of the login attempt
		name=nameView.getText().toString();		
		password = passwordView.getText().toString();
		if(userid==null)userid=AndroidDeviceId.getAndroidId(getActivity());
		//Log.d(TAG,userid);
		
		if (TextUtils.isEmpty(name)) {
			nameView.setError(getString(R.string.error_field_required));
			focusView = nameView;
			cancel = true;
		}else if(name.length()>32){
			nameView.setError(getString(R.string.error_name_long));
			focusView = nameView;
			cancel = true;
		}
		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancel = true;
		} else if (password.length() < 4) {
			passwordView.setError(getString(R.string.error_invalid_password));
			focusView = passwordView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.	
			if(loginHelper!=null){
				loginHelper.name=name;
				loginHelper.password=password;
				loginHelper.userid=userid;				
			}else loginHelper=new LoginHelper(name,password,userid);			
			storeUserInfo(getActivity());
			mAuthTask = new AuthTask();
			mAuthTask.execute((Void) null);
		}
	}	
}
