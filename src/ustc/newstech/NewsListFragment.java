package ustc.newstech;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.ViewHolder;
import ustc.newstech.data.Constant;
import ustc.newstech.data.parser.NewsInfo;
import ustc.newstech.data.parser.NewsInfoParser;
import ustc.newstech.database.TableDuplicate;
import ustc.newstech.database.NewsTechDBHelper;
import ustc.newstech.duplicate.DuplicateActivity;
import ustc.utils.Network;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsListFragment extends Fragment{
	public static final String TAG = "XXXNewslistfragment";
	public static final String ARG_TYPE = "ustc.newstech.optype";
	private String mtype=null;
	private List<NewsInfo> newsInfoList=new ArrayList<NewsInfo>();
	private XListView mListView=null;
	private ImageFetcher mImageFetcher;
	private int screenHeight=1080;
	private NewsAdapter mNewsAdapter=null;	
	private Map<String,Integer> dupMap=new HashMap<String,Integer>();
	private NewsTechDBHelper dbHelper;
	private DBTask dbTask=null;
	private boolean isVolunteer=false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        
		Bundle args = getArguments();
		mtype=args.getString(ARG_TYPE, Constant.category[0]);
		GetNewsInfo newsInfoTask=new GetNewsInfo();
        String reqUrl=Constant.newsHost+Constant.op_top+Constant.pa_numEq+"10"
		+Constant.pa_mtypeEq+mtype;
		newsInfoTask.execute(reqUrl,Constant.op_top);
		if (MainActivity.class.isInstance(getActivity())) {
	       mImageFetcher = ((MainActivity) getActivity()).getImageFetcher();
	    }
		// Initialize the volunteer module
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		isVolunteer=sharedPref.getBoolean("volunteer", false);		
	}
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.
		 View rootView = inflater.inflate(R.layout.fragment_news_list, container, false);		    
	     mListView=(XListView)rootView.findViewById(R.id.c_listview);
	     mListView.setPullLoadEnable(true);
	     mListView.setPullRefreshEnable(true);
	     dbHelper=new NewsTechDBHelper(getActivity());
	     if(dbTask==null){	    	 	
				dbTask=new DBTask();
				dbTask.execute();
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
				GetNewsInfo newsInfoTask=new GetNewsInfo();
				if(newsInfoList!=null&&newsInfoList.size()>0){
					NewsInfo nInfo=newsInfoList.get(0);
			        String reqUrl="";
					try {
						reqUrl = Constant.newsHost+Constant.op_refresh
								+Constant.pa_mtypeEq+mtype+Constant.pa_numEq+"10"
								+Constant.pa_ctimeEq+String.valueOf(nInfo.getCTime())
								+Constant.pa_newsidEq+URLEncoder.encode(nInfo.getNewsid(),"utf-8")								
								+Constant.pa_clickEq+nInfo.getClick();
	//					Log.d("XXXXXXXXXXXX", reqUrl);
						newsInfoTask.execute(reqUrl,Constant.op_refresh);
					}catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}		
				}
				else{
					String reqUrl=Constant.newsHost+Constant.op_top+
							Constant.pa_numEq+"10"+Constant.pa_mtypeEq+mtype;
					try{
						newsInfoTask.execute(reqUrl,Constant.op_top);
					}catch(IllegalStateException e){
						e.printStackTrace();
					}					
				}
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				GetNewsInfo newsInfoTask=new GetNewsInfo();
				if(newsInfoList!=null&&newsInfoList.size()>0){
					NewsInfo nInfo=newsInfoList.get(newsInfoList.size()-1);
					String reqUrl="";
					try {					
						reqUrl = Constant.newsHost+Constant.op_more
								+Constant.pa_mtypeEq+mtype+Constant.pa_numEq+"10"
								+Constant.pa_ctimeEq+String.valueOf(nInfo.getCTime())
								+Constant.pa_newsidEq+URLEncoder.encode(nInfo.getNewsid(),"utf-8")								
								+Constant.pa_clickEq+nInfo.getClick();
//						Log.d("XXXXXXXXXXXX", reqUrl);
						newsInfoTask.execute(reqUrl,Constant.op_more);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}
				}
				else{
					String reqUrl=Constant.newsHost+Constant.op_top
							+Constant.pa_numEq+"10"+Constant.pa_mtypeEq+mtype;
					try{
						newsInfoTask.execute(reqUrl,Constant.op_top);
					}catch(IllegalStateException e){
						e.printStackTrace();
					}					
				}
					
			}});	     
	     return rootView;
	 }		
	 /**
	  * params[0]:web,params[1]:num
	  *
	  * @author JohnDannl
	  *
	  */
	 private class GetNewsInfo extends AsyncTask<String,Void,List<NewsInfo>>{
		 	private String operation=null;
//		 	private Dialog loadingDialog=null;
		 	
			@Override
			protected void onPreExecute(){	
//				loadingDialog = MyProgressBar.createLoadingDialog(getActivity(), "");
//				loadingDialog.show();
			}
			@Override
			protected List<NewsInfo> doInBackground(String... params) {
				// TODO Auto-generated method stub
				List<NewsInfo> infos=null;
				try{
					operation=params[1];
					URL url=new URL(params[0]); 
		        	HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		        	conn.setConnectTimeout(8*1000);
					conn.setDoInput(true);
					conn.connect();
					InputStream is=conn.getInputStream();  	  
		             
		            infos = NewsInfoParser.parse(is);
			        is.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				return infos;			
			}
			@Override
			protected void onPostExecute(List<NewsInfo> list){
//				if(loadingDialog.isShowing())loadingDialog.dismiss();	
				if(list!=null){
					/*for(NewsInfo item:list){
						Log.d("XXXXXXXX", item.getTitle()+"->"+item.getSource());					
					}*/
					if(operation.equals(Constant.op_refresh)){
						if(list.size()>0){
							for(NewsInfo item:list){
//								Because the refresh operation returns an inverse order list 
								newsInfoList.add(0, item);
							}
						}
						String timeStr="";
						Date date = new Date(); 
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						timeStr=sdf.format(date);
						mListView.setRefreshTime(timeStr);
						mListView.stopRefresh();
					}else if(operation.equals(Constant.op_more)){
						if(list.size()>0)newsInfoList.addAll(list);	
						mListView.stopLoadMore();
					}else{
						if(list.size()>0)newsInfoList.addAll(list);
						//if size of newsInfoList is zero,triple operations are possible
						mListView.stopRefresh();
						mListView.stopLoadMore();
					}
					mNewsAdapter.notifyDataSetChanged();
				}
				else{
					if(Network.checkConnection(getActivity()))
					Toast.makeText(getActivity(), R.string.server_connection_failure, Toast.LENGTH_SHORT).show();
					//if size of newsInfoList is zero,triple operations are possible
					if(mListView!=null){
						mListView.stopRefresh();
						mListView.stopLoadMore();
					}
				}
			}
		}
	 private void clickNews(int position){
		 Intent intent=new Intent(getActivity(),BrowserActivity.class);
		 intent.putExtra(BrowserActivity.ARG_URL,newsInfoList.get(position).getUrl());
		 intent.putExtra(BrowserActivity.ARG_NEWSID, newsInfoList.get(position).getNewsid());
		 intent.putExtra(BrowserActivity.ARG_TITLE,newsInfoList.get(position).getTitle());
		 intent.putExtra(BrowserActivity.ARG_CTIME,newsInfoList.get(position).getCTime());
		 getActivity().startActivity(intent);
	 }
	 private class NewsAdapter extends BaseAdapter{
			private int selectedIndex;
			public NewsAdapter(){
				selectedIndex = -1;
			}		
			public void recordSubmitDup(){
			}
			public void setSelectedIndex(int index)
		    {
		        selectedIndex = index;
		        //notifyDataSetChanged();
		    }
			public int getSelectedIndex(){
				return selectedIndex;
			}
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
		                    .inflate(R.layout.news_list_item_dup, null); 
				}
				ViewHolder holder=ViewHolder.get(convertView);
				LinearLayout newsItem=(LinearLayout)holder.getView(R.id.news_item);
			    newsItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenHeight*1/6));
//			    Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", newsItem.getLayoutParams().height,newsItem.getLayoutParams().width));
				ImageView thumb=(ImageView)holder.getView(R.id.news_thumb);
				mImageFetcher.loadImage(newsInfoList.get(position).getThumb(), thumb);			
				TextView title=(TextView)holder.getView(R.id.news_title);
				title.setText(newsInfoList.get(position).getTitle());
				TextView author=(TextView)holder.getView(R.id.news_author);
				author.setText(newsInfoList.get(position).getAuthor());
				TextView loadtime=(TextView)holder.getView(R.id.news_ldtime);
				Date date = new Date(newsInfoList.get(position).getCTime()*1000); 
				//DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
				loadtime.setText(sdf.format(date));
				
				CheckBox chkBox=(CheckBox)holder.getView(R.id.dup_check);
				if(isVolunteer){		
					chkBox.setVisibility(View.VISIBLE);
					if(dupMap.containsKey(newsInfoList.get(position).getNewsid())){
						chkBox.setText(String.valueOf(dupMap.get(newsInfoList.get(position).getNewsid())));
					}else
						chkBox.setText("0");
					// must update the listener before change its state
					// otherwise the converView will use the old listener with old position value
					chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if(isChecked){
								//Toast.makeText(getActivity(), "check:"+position, Toast.LENGTH_SHORT).show();
								if(selectedIndex==-1)selectedIndex=position;
								else if(position!=selectedIndex){
									//submitDuplicate(selectedIndex,position);
									showDuplicateActivity(selectedIndex,position);
									selectedIndex=-1;
									notifyDataSetChanged();
								    }
							}else{
								//Toast.makeText(getActivity(), "uncheck:"+position, Toast.LENGTH_SHORT).show();
								if(selectedIndex==position)selectedIndex=-1;
							}
						}
						
					});					
					if(position==selectedIndex)chkBox.setChecked(true);
					else chkBox.setChecked(false);
				}else{
					chkBox.setVisibility(View.GONE);
				}
				/*if(selectedIndex!= -1 && position == selectedIndex){
					title.setTextColor(getResources().getColor(R.color.dark_tangerine));
				}
				else{
					title.setTextColor(getResources().getColor(R.color.white));
				}*/
				return convertView;
			}
			 
		 }
	 private void submitDuplicate(int position1,int position2){
		 //Toast.makeText(getActivity(), "Submit:"+position1+":"+position2, Toast.LENGTH_SHORT).show();
		 String[] newsTitles=new String[2];
		 newsTitles[0]=newsInfoList.get(position1).getTitle();
		 newsTitles[1]=newsInfoList.get(position2).getTitle();
		 ArrayList<Integer> selectedItems=new ArrayList<Integer>();
		 selectedItems.add(0);
		 selectedItems.add(1);
		 showDuplicateDialog(newsTitles,selectedItems);
	 }
	 private void showDuplicateActivity(int position1,int position2){
		 Intent intent=new Intent(getActivity(),DuplicateActivity.class);
		 String[] data=new String[4];
		 data[0]=newsInfoList.get(position1).getTitle();
		 data[1]=newsInfoList.get(position2).getTitle();
		 data[2]=newsInfoList.get(position1).getNewsid();
		 data[3]=newsInfoList.get(position2).getNewsid();
		 intent.putExtra(DuplicateActivity.DUPINFO, data);
		 getActivity().startActivity(intent);
	 }
	 private void showDuplicateDialog(String[] items,final ArrayList<Integer> selectedItems){
		 boolean[] checkedItems={true,true};		 
		 //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		 //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Dialog));
		 AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
		 builder.setTitle(R.string.title_duplication_dialog).setMultiChoiceItems(items, checkedItems, 
				 new DialogInterface.OnMultiChoiceClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked){
							selectedItems.add(which);
						}else if(selectedItems.contains(which)){
							selectedItems.remove(Integer.valueOf(which));
						}
					}
			 
		 }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), ""+selectedItems.size(), Toast.LENGTH_SHORT).show();
			}

		 }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.dup_submit_cancel, Toast.LENGTH_SHORT).show();
			}
		}).create().show();		 		 
	 }	 
	 private class DBTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 7 days ago
			long ctime=Calendar.getInstance().getTimeInMillis()-7*24*3600*1000;
			if(dbHelper!=null)dupMap=TableDuplicate.selectItems(dbHelper, ctime);
			/*for(Entry<String,Integer> entry:dupMap.entrySet()){
				Log.d(TAG,entry.getKey()+":"+entry.getValue());
			}*/
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			dbTask=null;
			if(mNewsAdapter!=null)mNewsAdapter.notifyDataSetChanged();
		}
	 }
	 @Override
	  public void onResume() {
	     super.onResume();
	     //Log.d(TAG, "onResume NewsListFragment");
	     if(dbTask==null){	    	 	
				dbTask=new DBTask();
				dbTask.execute();
			}
	     if(isVolunteer!=isVolunteer()){
	    	 isVolunteer=isVolunteer();
	    	 if(mNewsAdapter!=null)mNewsAdapter.notifyDataSetChanged();
	     }
	  }

	  @Override
	  public void onPause() {
	     super.onPause();
	     //Log.d(TAG, "OnPause NewsListFragment");
	  }
	  private boolean isVolunteer(){
	    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			return sharedPref.getBoolean("volunteer", false);
	    }
}
