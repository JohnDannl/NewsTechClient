package ustc.newstech.login;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import ustc.newstech.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordFragment extends BaseFragment {
	public static final String ARG_OBJECT = "object";	
	public static final String TAG="XXXChangePasswordFragment";
	private View cpFormView,cpStatusView;
	private EditText passwordView,newPasswordView,newPasswordView2;
	private Button btn_submit;
	private AuthTask mAuthTask=null;	
	private String newPassword;
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
	     final View rootView = inflater.inflate(R.layout.fragment_changepassword, container, false);
	     cpFormView = rootView.findViewById(R.id.changepassword_form);
		 cpStatusView = rootView.findViewById(R.id.changepassword_status);
		 passwordView=(EditText)rootView.findViewById(R.id.cppassword);
		 newPasswordView=(EditText)rootView.findViewById(R.id.newpassword);
		 newPasswordView2=(EditText)rootView.findViewById(R.id.newpassword2);
		 btn_submit=(Button)rootView.findViewById(R.id.btn_cp_submit);
		 btn_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptChangePassword();				
			}
			 
		 });
	     return rootView;
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
		editor.putString("password", newPassword);
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

			cpStatusView.setVisibility(View.VISIBLE);
			cpStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							cpStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			cpFormView.setVisibility(View.VISIBLE);
			cpFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							cpFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			cpStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			cpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
				return loginHelper.changPassword(newPassword);
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
				if(result.equals("Change password successfully")){
					Toast.makeText(getActivity(), getResources().getString(R.string.success_change), Toast.LENGTH_SHORT).show();
					storeUserInfo(getActivity());
					listener.showLogout();
				}else{					
					Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
				}
			}else{
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
	public void attemptChangePassword() {
		if (mAuthTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		// Reset errors.				
		passwordView.setError(null);
		newPasswordView.setError(null);
		newPasswordView2.setError(null);
		// Store values at the time of the login attempt
		String oldPassword=passwordView.getText().toString();
		newPassword=newPasswordView.getText().toString();
		String newPassword2=newPasswordView2.getText().toString();
				
		if (TextUtils.isEmpty(oldPassword)) {
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancel = true;
		}else if(!oldPassword.equals(password)){
			passwordView.setError(getString(R.string.error_incorrect_password));
			focusView = passwordView;
			cancel = true;
		}
		// Check for a valid password.
		if (TextUtils.isEmpty(newPassword)) {
			newPasswordView.setError(getString(R.string.error_field_required));
			focusView = newPasswordView;
			cancel = true;
		} else if (newPassword.length() < 4) {
			newPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = newPasswordView;
			cancel = true;
		}
		if (TextUtils.isEmpty(newPassword2)) {
			newPasswordView2.setError(getString(R.string.error_field_required));
			focusView = newPasswordView2;
			cancel = true;
		} else if (newPassword2.length() < 4) {
			newPasswordView2.setError(getString(R.string.error_invalid_password));
			focusView = newPasswordView2;
			cancel = true;
		}
		
		if(!TextUtils.isEmpty(newPassword)&&!TextUtils.isEmpty(newPassword2)
			&&!newPassword.equals(newPassword2)){
				newPasswordView2.setError(getString(R.string.error_newpassword_not_match));
				focusView = newPasswordView2;
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
