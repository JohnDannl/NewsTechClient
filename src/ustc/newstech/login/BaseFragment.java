package ustc.newstech.login;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	protected LoginHelper loginHelper=null;
	protected String name,password,email,userid,cookie;
	protected VolunteerListener listener;
	public void setVolunterListener(VolunteerListener l){
		if(l!=null)listener=l;
	}
	public interface VolunteerListener{
		public void showLogin();
		public void showLogout();
		public void showRegister();
		public void showChangePassword();
		public void showChangeInfo();
	}
	protected void initializeLoginInfo(){
		SharedPreferences sharedPref = PreferenceManager.
        		getDefaultSharedPreferences(getActivity());
        name=sharedPref.getString("name", null);
		password=sharedPref.getString("password", null);
		email=sharedPref.getString("email", null);
		userid=sharedPref.getString("userid", null);
		cookie=sharedPref.getString("cookie", null);
		if(name==null||password ==null||userid==null)loginHelper=null;
		else loginHelper=new LoginHelper(name,password,userid);	
		if(loginHelper!=null&&cookie!=null)loginHelper.setCookie(cookie);
	}
}
