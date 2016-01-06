package ustc.newstech.duplicate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.ParseException;

import ustc.custom.widget.ViewHolder;
import ustc.newstech.R;
import ustc.newstech.data.parser.NewsInfo;
import ustc.newstech.database.TableDuplicate;
import ustc.newstech.database.NewsTechDBHelper;
import ustc.newstech.login.LoginHelper;
import ustc.utils.Network;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class DuplicateActivity extends Activity{
	public static final String TAG="XXXDuplicateActivity";
	public static final String DUPINFO ="ustc.newstech.duplicate.dupinfo";
	private String[] data=null;
	private ArrayList<Integer> selectedItems=new ArrayList<Integer>();
	private ProgressBar progressBar;
	private LoginHelper loginHelper=null;
	private DuplicateSubmit sumitTask=null;
	private NewsTechDBHelper dbHelper=new NewsTechDBHelper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_duplicate);
		Intent intent=getIntent();
		data=intent.getStringArrayExtra(DUPINFO);
		selectedItems.add(0);
		selectedItems.add(1);
		ListView listView=(ListView)findViewById(R.id.duplicate_news_list);
		listView.setAdapter(new DuplicateAdapter());
		initializeLoginInfo();
		progressBar=(ProgressBar)findViewById(R.id.duplicate_submit_progress);
		Button btn_ok=(Button)findViewById(R.id.btn_duplicate_sumbit);
		btn_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectedItems.size()==2){
					if(sumitTask==null){
						sumitTask=new DuplicateSubmit();
						sumitTask.execute(data[2],data[3]);
					}
				}
				else Toast.makeText(DuplicateActivity.this, 
						getResources().getString(R.string.must_select_two_items), Toast.LENGTH_SHORT).show();
			}
			
		});
		Button btn_cancel=(Button)findViewById(R.id.btn_duplicate_cancel);
		btn_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sumitTask!=null)sumitTask.cancel(true);
				finish();
			}
			
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}*/
	protected void initializeLoginInfo(){
		SharedPreferences sharedPref = PreferenceManager.
        		getDefaultSharedPreferences(this);
        String name=sharedPref.getString("name", null);
		String password=sharedPref.getString("password", null);
		String email=sharedPref.getString("email", null);
		String userid=sharedPref.getString("userid", null);
		String cookie=sharedPref.getString("cookie", null);
		if(name==null||password ==null||userid==null)loginHelper=null;
		else loginHelper=new LoginHelper(name,password,userid);	
		if(loginHelper!=null&&cookie!=null)loginHelper.setCookie(cookie);
		if(loginHelper!=null&&email!=null)loginHelper.setEmail(email);
	}
	private class DuplicateSubmit extends AsyncTask<String,Void,String>{
		@Override
		protected void onPreExecute(){
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result=null;
			try {
				if(loginHelper!=null)
				result=loginHelper.submitDuplicate(params[0], params[1]);
				if(result.equals("Submit duplication successfully")&&dbHelper!=null){
					long ctime=Calendar.getInstance().getTimeInMillis();
					TableDuplicate.insertItem(dbHelper, params[0], ctime);
					TableDuplicate.insertItem(dbHelper, params[1], ctime);
					/*Map<String,Integer> records=TableDuplicate.selectItems(dbHelper, ctime/2);
					for(Entry<String,Integer> entry:records.entrySet()){
						Log.d(TAG,entry.getKey()+":"+entry.getValue());
					}*/
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result){
			progressBar.setVisibility(View.GONE);
			if(result!=null){
				if(result.equals("Submit duplication successfully")){
					Toast.makeText(DuplicateActivity.this, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
					finish();
				}else if(result.equals("User not logins")){
					Toast.makeText(DuplicateActivity.this, getResources().getString(R.string.user_not_login), Toast.LENGTH_SHORT).show();
				}else{					
					Toast.makeText(DuplicateActivity.this, result, Toast.LENGTH_SHORT).show();
				}
			}else{
				if(Network.checkConnection(DuplicateActivity.this))
					Toast.makeText(DuplicateActivity.this, getResources().getString(R.string.failure_retry), Toast.LENGTH_SHORT).show();
			}				
			sumitTask = null;
		}
		@Override
		protected void onCancelled(){
			progressBar.setVisibility(View.GONE);
			sumitTask=null;
		}
	}
	private class DuplicateAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length/2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView=LayoutInflater.from(DuplicateActivity.this)
						.inflate(R.layout.duplicate_news_list_item,null);
			}
			ViewHolder holder=ViewHolder.get(convertView);
			TextView title=(TextView)holder.getView(R.id.duplicate_item_title);
			title.setText(data[position]);
			CheckBox chkBox=(CheckBox)holder.getView(R.id.duplicate_item_chk);
			chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						if(!selectedItems.contains(position))
							selectedItems.add(position);
						//Toast.makeText(DuplicateActivity.this, "selected:"+position, Toast.LENGTH_SHORT).show();
					}else{
						if(selectedItems.contains(position))
						selectedItems.remove(Integer.valueOf(position));
						//Toast.makeText(DuplicateActivity.this, "unselected:"+position, Toast.LENGTH_SHORT).show();
					}					
				}
				
			});
			
			return convertView;
		}		
	}
}

