package ustc.newstech.login;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import ustc.newstech.R;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeInfoFragment extends BaseFragment {
	public static final String ARG_OBJECT = "object";	
	public static final String TAG="XXXChangeInfoFragment";
	private View changeInfoFormView;
	private View changeInfoStatusView;
	private EditText newNameView,passwordView,newEmailView;
	private Button btn_submit;
	private AuthTask mAuthTask=null;	
	private String newName,newEmail;
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
	     final View rootView = inflater.inflate(R.layout.fragment_changeinfo, container, false);
	     changeInfoFormView = rootView.findViewById(R.id.changeinfo_form);
		 changeInfoStatusView = rootView.findViewById(R.id.changeinfo_status);
		 newNameView=(EditText)rootView.findViewById(R.id.newname);
		 newEmailView=(EditText)rootView.findViewById(R.id.newemail);
		 passwordView=(EditText)rootView.findViewById(R.id.password);
		 
		 btn_submit=(Button)rootView.findViewById(R.id.btn_changeinfo);
		 btn_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptChangeInfo();				
			}
			 
		 });
	     return rootView;
	 }
	private void storeUserInfo(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor=sharedPref.edit();	
		editor.putString("name", newName);
		editor.putString("email", newEmail);
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

			changeInfoStatusView.setVisibility(View.VISIBLE);
			changeInfoStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							changeInfoStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			changeInfoFormView.setVisibility(View.VISIBLE);
			changeInfoFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							changeInfoFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			changeInfoStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			changeInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
				return loginHelper.changInfo(newName, newEmail);
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
				if(result.equals("Change info successfully")){
					Toast.makeText(getActivity(), getResources().getString(R.string.success_change), Toast.LENGTH_SHORT).show();
					storeUserInfo(getActivity());
					listener.showLogout();
				}else if(result.equals("User not exists")){
					Toast.makeText(getActivity(), getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
					listener.showRegister();
				}else{					
					Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
				}
			}else{
				if(Network.checkConnection(getActivity()))
					Toast.makeText(getActivity(), getResources().getString(R.string.failure_change), Toast.LENGTH_SHORT).show();
				}				
			mAuthTask = null;
		}
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	public void attemptChangeInfo() {
		if (mAuthTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		// Reset errors.		
		newNameView.setError(null);
		newEmailView.setError(null);
		passwordView.setError(null);
		// Store values at the time of the login attempt
		newName=newNameView.getText().toString();	
		newEmail=newEmailView.getText().toString();
		String passwordIn = passwordView.getText().toString();
		if(userid==null)userid=AndroidDeviceId.getAndroidId(getActivity());
		//Log.d(TAG,userid);
		
		if (TextUtils.isEmpty(newName)) {
			newNameView.setError(getString(R.string.error_field_required));
			focusView = newNameView;
			cancel = true;
		}else if(newName.length()>32){
			newNameView.setError(getString(R.string.error_name_long));
			focusView = newNameView;
			cancel = true;
		}
		// Check for a valid password.
		if (TextUtils.isEmpty(passwordIn)) {
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancel = true;
		} else if (passwordIn.length() < 4) {
			passwordView.setError(getString(R.string.error_invalid_password));
			focusView = passwordView;
			cancel = true;
		} else if (!passwordIn.equals(password)){
			passwordView.setError(getString(R.string.error_incorrect_password));
			focusView = passwordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(newEmail)) {
			newEmailView.setError(getString(R.string.error_field_required));
			focusView = newEmailView;
			cancel = true;
		} else if (!newEmail.contains("@")) {
			newEmailView.setError(getString(R.string.error_invalid_email));
			focusView = newEmailView;
			cancel = true;
		}
		// check if the info is the same
		if(!TextUtils.isEmpty(newName)&&!TextUtils.isEmpty(newEmail)
				&&newName.equals(name)&&newEmail.equals(email)){
			newEmailView.setError(getString(R.string.error_the_same_info));
			focusView = newEmailView;
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
			mAuthTask = new AuthTask();
			mAuthTask.execute((Void) null);
		}
	}	
}
