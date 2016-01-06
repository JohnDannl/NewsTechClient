package ustc.newstech.about;

import ustc.newstech.R;
import ustc.utils.update.UpdateManager;
import ustc.utils.update.VersionDetector;
import ustc.utils.update.UpdateManager.NoUpdateReminder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutFragment extends Fragment {
	public static final String TAG = "XXXAboutFragment";
	public static final String ARG_OBJECT = "ustc.newstech.aboutfragment.arg";
	UpdateManager mUpdateManager;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mUpdateManager = new UpdateManager(getActivity());
        mUpdateManager.setNoUpdateReminder(new NoUpdateReminder(){

			@Override
			public void remind() {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(),R.string.update_already_newews, Toast.LENGTH_SHORT).show();
			}
        	
        });        
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);	
		TextView versionName=(TextView)rootView.findViewById(R.id.version_name);
		VersionDetector vDetector=new VersionDetector(getActivity());
		String text = String.format(getResources().getString(R.string.pref_about_long),
				getResources().getString(R.string.app_name),vDetector.getVersionName());
		versionName.setText(text);
		Button btn_update=(Button)rootView.findViewById(R.id.btn_chk_update);
		btn_update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUpdateManager.checkUpdateInfo();
			}			
		});
		Button btn_contact=(Button)rootView.findViewById(R.id.btn_contact_us);
		btn_contact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getActivity(), "Contact", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(getActivity(),ContactActivity.class);
				startActivity(intent);
			}			
		});
		return rootView;
	}
}
