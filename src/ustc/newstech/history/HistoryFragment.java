package ustc.newstech.history;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import ustc.custom.widget.ViewHolder;
import ustc.newstech.BrowserActivity;
import ustc.newstech.MainActivity;
import ustc.newstech.R;
import ustc.newstech.database.NewsTechDBHelper;
import ustc.newstech.database.TableHistory;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryFragment extends Fragment{
	public static final String TAG = "XXXHistoryFragment";
	public static final String ARG_OBJECT = "ustc.newstech.historyfragment.arg";
	private List<HistoryNews> newsInfoList=new ArrayList<HistoryNews>();
	private XListView mListView=null;
	private NewsTechDBHelper dbHelper;
	private GetNewsTask getTask=null;
	private NewsAdapter mNewsAdapter=null;	
	private ClearTask clearTask=null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setHasOptionsMenu(true);
		((MainActivity)getActivity()).setOnBtnClearListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showClearDialog();
			}			
		});
	}
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.fragment_history, container, false);		    
	     mListView=(XListView)rootView.findViewById(R.id.history_listview);
	     mListView.setPullLoadEnable(true);
	     mListView.setPullRefreshEnable(false);
	     dbHelper=new NewsTechDBHelper(getActivity());
	     if(getTask==null){	    	 	
				getTask=new GetNewsTask();
				getTask.execute();
			}
	     mNewsAdapter=new NewsAdapter();
	     mListView.setAdapter(mNewsAdapter);
	     mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "Selected:"+Integer.toString(position), Toast.LENGTH_SHORT).show();
				//note:this position is started from 1 rather than 0
				int selectedIndex=position-1;
				clickNews(selectedIndex);				
			}
	     });
	     mListView.setXListViewListener(new IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				 if(getTask==null){	    	 	
					getTask=new GetNewsTask();
					getTask.execute();
				 }
			}
	    	 
	     });
	     return rootView;
	}
	/*@Override
	public void onPrepareOptionsMenu(Menu menu) {
	    menu.findItem(R.id.action_clear).setVisible(true);
	    super.onPrepareOptionsMenu(menu);
	    Log.d(TAG,"onPrepareOptionMenu");
	}*/
	 private void clickNews(int position){
		 Intent intent=new Intent(getActivity(),BrowserActivity.class);
		 intent.putExtra(BrowserActivity.ARG_URL,newsInfoList.get(position).getUrl());
		 intent.putExtra(BrowserActivity.ARG_NEWSID, newsInfoList.get(position).getNewsid());
		 intent.putExtra(BrowserActivity.ARG_TITLE,newsInfoList.get(position).getTitle());
		 intent.putExtra(BrowserActivity.ARG_CTIME,newsInfoList.get(position).getCTime());
		 getActivity().startActivity(intent);
	 }
	 private class NewsAdapter extends BaseAdapter{			
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return newsInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return newsInfoList.get(position).getTitle();
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
				convertView = LayoutInflater.from(getActivity())  
	                    .inflate(R.layout.news_list_item_history, null); 
			}
			ViewHolder holder=ViewHolder.get(convertView);
			/*LinearLayout newsItem=(LinearLayout)holder.getView(R.id.news_item);
		    newsItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenHeight*1/6));*/
					
			TextView scanTime=(TextView)holder.getView(R.id.scan_time);
			scanTime.setText(HistoryNews.getTimeStamp(newsInfoList.get(position).getRTime()));
			TextView title=(TextView)holder.getView(R.id.history_news_title);
			title.setText(newsInfoList.get(position).getTitle());			
			return convertView;
		}		 
	 }
	private class GetNewsTask extends AsyncTask<Void,Void,ArrayList<HistoryNews>>{

		@Override
		protected ArrayList<HistoryNews> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 7 days ago
			ArrayList<HistoryNews> result=null;
			if(newsInfoList.size()==0)result= TableHistory.selectItemsTop(dbHelper, 15);			
			else {
				HistoryNews news=newsInfoList.get(newsInfoList.size()-1);
				result= TableHistory.selectItemsMore(dbHelper, news.getRTime(),
						news.getNewsid(), 15);
			}
			/*for(Entry<String,Integer> entry:dupMap.entrySet()){
				Log.d(TAG,entry.getKey()+":"+entry.getValue());
			}*/
			return result;
		}
		@Override
		protected void onPostExecute(ArrayList<HistoryNews> result){
			if(result!=null){
				newsInfoList.addAll(result);
				if(mNewsAdapter!=null)mNewsAdapter.notifyDataSetChanged();
			}			
			mListView.stopLoadMore();
			getTask=null;
		}
	 }
	private class ClearTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TableHistory.clear(dbHelper);					
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			newsInfoList.clear();
			if(mNewsAdapter!=null)mNewsAdapter.notifyDataSetChanged();
			mListView.stopLoadMore();
			clearTask=null;
			Toast.makeText(getActivity(), R.string.success_clear, Toast.LENGTH_SHORT).show();
		}
	 }
	 @Override
	  public void onResume() {
	     super.onResume();
	     //Log.d(TAG, "onResume HistoryFragment");	  
	     if(getTask==null){	    	 	
	    	 	newsInfoList.clear();
				getTask=new GetNewsTask();
				getTask.execute();
			}
	  }

	  @Override
	  public void onPause() {
	     super.onPause();
	     //Log.d(TAG, "OnPause NewsListFragment");
	  }
	  private void showClearDialog(){
			 AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Dialog));
			 builder.setTitle(R.string.clear_history_make_sure)
			 .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(clearTask==null){
						clearTask=new ClearTask();
						clearTask.execute();
					}
				}

			 }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			}).create().show();		 		 
		 }	 
}
