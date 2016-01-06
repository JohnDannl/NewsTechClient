package ustc.newstech.discovery;

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
import java.util.Locale;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.ViewHolder;
import ustc.newstech.BrowserActivity;
import ustc.newstech.MainActivity;
import ustc.newstech.R;
import ustc.newstech.data.Constant;
import ustc.newstech.data.parser.NewsInfo;
import ustc.newstech.data.parser.NewsInfoParser;
import ustc.utils.Network;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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

public class SearchFragment extends Fragment {
	public static final String ARG_KEYWORDS = "search_keywords";
	private String keywords=null;
//	private LinearLayout progressBarLayout;
	private List<NewsInfo> newsInfoList=new ArrayList<NewsInfo>();
	private XListView mListView=null;
	private int itemHeight=0;
	private NewsAdapter mNewsAdapter=null;	
	private OnSearchListItemClick listener=null;
	private ImageFetcher mImageFetcher;
	private SearchNewsTask newsInfoTask=null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		keywords=args.getString(ARG_KEYWORDS, "");		
        if(keywords!=null&&!keywords.equals("")){
        	if(newsInfoTask!=null)newsInfoTask.cancel(true);
        	newsInfoTask=new SearchNewsTask(); 
        	String pageStr="1";
            String reqUrl="";
			try {				
				reqUrl = Constant.searchHost+Constant.pa_keywordsEq+URLEncoder.encode(keywords,"UTF-8")
						+Constant.pa_pageEq+pageStr;
//				Log.d("XXXXXXXXXsearch", reqUrl);
	    		newsInfoTask.execute(reqUrl,pageStr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(IllegalStateException e){
				e.printStackTrace();
			}
        } 
        
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
 		itemHeight=height*1/6; 		
 		if (MainActivity.class.isInstance(getActivity())) {
	      mImageFetcher = ((MainActivity) getActivity()).getImageFetcher();
	    }
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.
		View rootView = inflater.inflate(R.layout.fragment_search, container, false);	
//		progressBarLayout=(LinearLayout)rootView.findViewById(R.id.search_list_progress);
//		progressBarLayout.setVisibility(View.GONE);
		mListView=(XListView)rootView.findViewById(R.id.search_result);
		mListView.setPullLoadEnable(true);
	    mListView.setPullRefreshEnable(false);// refresh function forbidden
	    mNewsAdapter=new NewsAdapter(newsInfoList);
	    mListView.setAdapter(mNewsAdapter);
	    mListView.setXListViewListener(new IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(keywords!=null&&!keywords.equals("")&&mNewsAdapter!=null){
					if(newsInfoTask!=null)newsInfoTask.cancel(true);
		        	newsInfoTask=new SearchNewsTask(); 
		        	String pageStr=Integer.toString(mNewsAdapter.getPage()+1);
		            String reqUrl="";
					try {				
						reqUrl = Constant.searchHost+Constant.pa_keywordsEq+URLEncoder.encode(keywords,"UTF-8")
								+Constant.pa_pageEq+pageStr;
//			            Log.d("XXXXXXXXXsearch", reqUrl);
			    		newsInfoTask.execute(reqUrl,pageStr);
					}catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}
		        }
			}
	    	
	    });
	    mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//note:this position is started from 1 rather than 0
				clickNews(position-1);
			}	    	
	    });
		return rootView;
	}
	@Override
	public void onStop(){
		super.onStop();
		if(newsInfoTask!=null)newsInfoTask.cancel(true);
	}
	private class SearchNewsTask extends AsyncTask<String,Void,List<NewsInfo>>{
		 	
		@Override
		protected void onPreExecute(){
//			if(progressBarLayout!=null)progressBarLayout.setVisibility(View.VISIBLE);
		}
		@Override
		protected List<NewsInfo> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NewsInfo> infos=null;
			try{				
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
//			if(progressBarLayout!=null)progressBarLayout.setVisibility(View.GONE);	
			if(mListView!=null)mListView.stopLoadMore();			
			if(list!=null){
//					for(NewsInfo item:list){
//						Log.d("XXXXXXXX", item.getTitle()+"->"+item.getWeb());					
//					}				
				if(list.size()>0)newsInfoList.addAll(list);
				mNewsAdapter.notifyDataSetChanged();				
			}
			else{
				if(Network.checkConnection(getActivity()))
				Toast.makeText(getActivity(), R.string.server_connection_failure, Toast.LENGTH_SHORT).show();				
			}
		}
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
		public int getPage(){
			return (int)Math.ceil(newsArray.size()/(double)Constant.pageNum);
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
	                    .inflate(R.layout.news_list_item_dup, null); 
			}
			ViewHolder holder=ViewHolder.get(convertView);
			LinearLayout newsItem=(LinearLayout)holder.getView(R.id.news_item);
		    newsItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,itemHeight));
//			    Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", newsItem.getLayoutParams().height,newsItem.getLayoutParams().width));
			ImageView thumb=(ImageView)holder.getView(R.id.news_thumb);
			mImageFetcher.loadImage(newsArray.get(position).getThumb(), thumb);			
			TextView title=(TextView)holder.getView(R.id.news_title);
			title.setText(newsArray.get(position).getTitle());
			TextView author=(TextView)holder.getView(R.id.news_author);
			author.setText(newsArray.get(position).getAuthor());
			TextView loadtime=(TextView)holder.getView(R.id.news_ldtime);
			loadtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA).format(new Date(newsArray.get(position).getCTime()*1000)));

			if(selectedIndex!= -1 && position == selectedIndex){
				title.setTextColor(getResources().getColor(R.color.dark_tangerine));
			}
			else{
				title.setTextColor(getResources().getColor(R.color.white));
			}		
			return convertView;
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
	public void setOnSearchItemClickListener(OnSearchListItemClick l){
		if(l!=null)listener=l;
	}
	public interface OnSearchListItemClick{
		public void OnItemClick(NewsInfo newsInfo);
	}
}
