package ustc.newstech;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.ViewHolder;
import ustc.newstech.data.Constant;
import ustc.newstech.data.parser.NewsInfo;
import ustc.newstech.data.parser.NewsInfoParser;
import ustc.utils.Network;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsListFragment extends Fragment{
	public static final String ARG_CONTENT = "ustc.newstech.content";
	private String mtype=null;
	private List<NewsInfo> newsInfoList=new ArrayList<NewsInfo>();
	private XListView mListView=null;
	private ImageFetcher mImageFetcher;
	private int screenHeight=1080;
	private NewsAdapter mNewsAdapter=null;	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        
		Bundle args = getArguments();
		mtype=args.getString(ARG_CONTENT, Constant.category[0]);
		GetNewsInfo newsInfoTask=new GetNewsInfo();
        String reqUrl=Constant.newsHost+Constant.op_top+Constant.pa_numEq+"10"
		+Constant.pa_mtypeEq+mtype;
		newsInfoTask.execute(reqUrl,Constant.op_top);
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
	     mNewsAdapter=new NewsAdapter(newsInfoList);
	     mListView.setAdapter(mNewsAdapter);
	     mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "Selected:"+Integer.toString(position), Toast.LENGTH_SHORT).show();
				//note:this position is started from 1 rather than 0
				int selectedIndex=position-1;
				clickNews(selectedIndex,Constant.mode_detail);				
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
								+Constant.pa_ctimeEq+String.valueOf(nInfo.getcTime())
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
								+Constant.pa_ctimeEq+String.valueOf(nInfo.getcTime())
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
	 @Override
   public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);
       // Execute after onCreateView()
       // Use the parent activity to load the image asynchronously into the ImageView (so a single
       // cache can be used over all pages in the ViewPager
       if (MainActivity.class.isInstance(getActivity())) {
           mImageFetcher = ((MainActivity) getActivity()).getImageFetcher();
       }
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
	 private void clickNews(int position,String click_mode){
		 Intent intent=new Intent(getActivity(),BrowserActivity.class);
		 intent.putExtra(BrowserActivity.ARG_URL,newsInfoList.get(position).getUrl());
		 getActivity().startActivity(intent);
	 }
	 private class NewsAdapter extends BaseAdapter{
			private List<NewsInfo> newsArray=null;
			private int selectedIndex;
			public NewsAdapter(List<NewsInfo> newsInfoList){
				this.newsArray=newsInfoList;
				selectedIndex = -1;
			}		
			
			public void setSelectedIndex(int index)
		    {
		        selectedIndex = index;
		        notifyDataSetChanged();
		    }
			public int getSelectedIndex(){
				return selectedIndex;
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return newsArray.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return newsArray.get(position).getTitle();
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
		                    .inflate(R.layout.news_list_item, null); 
				}
				ViewHolder holder=ViewHolder.get(convertView);
				LinearLayout newsItem=(LinearLayout)holder.getView(R.id.news_item);
			    newsItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenHeight*1/6));
//			    Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", newsItem.getLayoutParams().height,newsItem.getLayoutParams().width));
				ImageView thumb=(ImageView)holder.getView(R.id.news_thumb);
				mImageFetcher.loadImage(newsArray.get(position).getThumb(), thumb);			
				TextView title=(TextView)holder.getView(R.id.news_title);
				title.setText(newsArray.get(position).getTitle());
				TextView author=(TextView)holder.getView(R.id.news_author);
				author.setText(newsArray.get(position).getAuthor());
				TextView loadtime=(TextView)holder.getView(R.id.news_ldtime);
				Date date = new Date(newsArray.get(position).getcTime()*1000); 
				//DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				loadtime.setText(sdf.format(date));
				
				if(selectedIndex!= -1 && position == selectedIndex){
					title.setTextColor(getResources().getColor(R.color.dark_tangerine));
				}
				else{
					title.setTextColor(getResources().getColor(R.color.white));
				}
				return convertView;
			}
			 
		 }
}
