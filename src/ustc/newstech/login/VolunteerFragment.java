package ustc.newstech.login;

import ustc.newstech.R;
import ustc.newstech.login.BaseFragment.VolunteerListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VolunteerFragment extends Fragment {
	public static final String TAG="XXXVolunteerFragment";
	public static final String ARG_OBJECT = "object";	
	
	enum Volunteer {Login,Register,Logout,ChangePassword,ChangeInfo};
	public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_volunteer, container, false);
		
		if(checkLogin())showView(Volunteer.Logout);
		else showView(Volunteer.Login);
		return rootView;
	}
	private boolean checkLogin(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean isLogin=sharedPref.getString("cookie", null)!=null?true:false;
		//Log.d(TAG, String.valueOf(isLogin)+":"+sharedPref.getString("cookie", null));
		return isLogin;
	}
	private void showView(Volunteer position){
    	// update the main content by replacing fragments    
		BaseFragment fragment;
    	Bundle args;
    	FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    	switch(position){
    		case Login:
    			fragment =new LoginFragment();
    			fragment.setVolunterListener(listener);
    			args = new Bundle();
    	        args.putInt(LoginFragment.ARG_OBJECT, position.ordinal());
    	        fragment.setArguments(args);
    	        fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment).commit();
    	        break;
    		case Register:
    			fragment =new RegisterFragment();
    			fragment.setVolunterListener(listener);
    			args =new Bundle();
    			args.putInt(RegisterFragment.ARG_OBJECT, position.ordinal());
    			fragment.setArguments(args);    
    			fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment,
    					RegisterFragment.TAG).commit();
    			break;
    		case Logout:
    			fragment =new LogoutFragment();
    			fragment.setVolunterListener(listener);
    			args =new Bundle();
    			args.putInt(LogoutFragment.ARG_OBJECT, position.ordinal());
    			fragment.setArguments(args);    
    			fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment,
    					LogoutFragment.TAG).commit();
    			break;
    		case ChangePassword:
    			fragment =new ChangePasswordFragment();
    			fragment.setVolunterListener(listener);
    			args =new Bundle();
    			args.putInt(ChangePasswordFragment.ARG_OBJECT, position.ordinal());
    			fragment.setArguments(args);    
    			fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment,
    					ChangePasswordFragment.TAG).commit();
    			break;
    		case ChangeInfo:
    			fragment =new ChangeInfoFragment();
    			fragment.setVolunterListener(listener);
    			args =new Bundle();
    			args.putInt(ChangeInfoFragment.ARG_OBJECT, position.ordinal());
    			fragment.setArguments(args);    
    			fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment,
    					ChangeInfoFragment.TAG).commit();
    			break;	
    		default:
    			fragment =new LoginFragment();
    			args = new Bundle();
    	        args.putInt(LoginFragment.ARG_OBJECT, position.ordinal());
    	        fragment.setArguments(args);
    	        fragmentManager.beginTransaction().replace(R.id.volunteer_fragment_container, fragment).commit();
    	        break;
    	}   

    }
	
	private VolunteerListener listener=new VolunteerListener(){

		@Override
		public void showLogin() {
			// TODO Auto-generated method stub
			//checkLogin();
			showView(Volunteer.Login);
		}

		@Override
		public void showLogout() {
			// TODO Auto-generated method stub
			//checkLogin();
			showView(Volunteer.Logout);
		}

		@Override
		public void showRegister() {
			// TODO Auto-generated method stub
			showView(Volunteer.Register);
		}

		@Override
		public void showChangePassword() {
			// TODO Auto-generated method stub
			showView(Volunteer.ChangePassword);
		}

		@Override
		public void showChangeInfo() {
			// TODO Auto-generated method stub
			showView(Volunteer.ChangeInfo);
		}		
	};
}
