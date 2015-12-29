package ustc.newstech.login;

import ustc.newstech.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LogoutFragment extends BaseFragment{
	public static final String ARG_OBJECT = "object";	
	public static final String TAG="XXXLogoutFragment";
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
	     final View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
	     TextView nameView=(TextView)rootView.findViewById(R.id.user_name);
	     nameView.setText(name);
	     TextView emailView=(TextView)rootView.findViewById(R.id.user_email);
	     emailView.setText(email);
	     Button btn_changepassword=(Button)rootView.findViewById(R.id.btn_changepassword);
	     btn_changepassword.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.showChangePassword();
			}
	    	 
	     });
	     Button btn_changeinfo=(Button)rootView.findViewById(R.id.btn_changeinfo);
		 btn_changeinfo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				listener.showChangeInfo();
			}
			 
		 });
	     Button btn_logout=(Button)rootView.findViewById(R.id.btn_logout);
		 btn_logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				clearLoginInfo(getActivity());
				listener.showLogin();
			}
			 
		 });
		 
	     return rootView;
	 }
	private void clearLoginInfo(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor=sharedPref.edit();
		editor.putString("cookie", null);
		editor.commit();
	}	
}
