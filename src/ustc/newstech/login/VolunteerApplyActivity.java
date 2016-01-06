package ustc.newstech.login;

import java.util.Calendar;
import java.util.Map.Entry;

import ustc.newstech.R;
import ustc.newstech.R.layout;
import ustc.newstech.R.menu;
import ustc.newstech.database.TableDuplicate;
import ustc.newstech.database.NewsTechDBHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class VolunteerApplyActivity extends Activity {
	private DBTask dbTask=null;
	private NewsTechDBHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volunteer_apply);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true); 
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(VolunteerApplyActivity.this);
		boolean isVolunteer=sharedPref.getBoolean("volunteer", false);
		CheckBox applyBox=(CheckBox)findViewById(R.id.check_volunteer);
		if(isVolunteer)applyBox.setChecked(true);
		else applyBox.setChecked(false);
		applyBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(VolunteerApplyActivity.this);
				SharedPreferences.Editor editor=sharedPref.edit();
				editor.putBoolean("volunteer", isChecked);				
				editor.commit();
			}
			
		});
		dbHelper=new NewsTechDBHelper(this);
		Button btn_cleardup=(Button)findViewById(R.id.btn_clearduplicate);
		btn_cleardup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dbTask==null){
					dbTask=new DBTask();
					dbTask.execute();
				}
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.volunteer_apply, menu);
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
	private class DBTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 7 days ago
			long ctime=Calendar.getInstance().getTimeInMillis()-7*24*3600*1000;
			if(dbHelper!=null)TableDuplicate.clear(dbHelper);		
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			dbTask=null;
			Toast.makeText(VolunteerApplyActivity.this, R.string.success_clear, Toast.LENGTH_SHORT).show();
		}
	 }
}
