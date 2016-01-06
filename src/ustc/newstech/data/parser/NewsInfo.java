package ustc.newstech.data.parser;

import java.util.List;

public class NewsInfo{
	
	public static String noImage="http://222.195.78.181:8889/static/news.ico";
	private String newsid,title,url,thumb,brief,source,author,mtype,description;
	private int click;
	private long ctime;
	private List<NewsInfo> related;
	public NewsInfo(){}
	public NewsInfo(String newsid,String title,String url,String brief,long ctime,String source){
		this.newsid=newsid;
		this.title=title;
		this.url=url;
		this.brief=brief;
		this.ctime=ctime;
		this.source=source;
	}
	public String getNewsid(){return this.newsid;}
	public String getTitle(){return this.title;}
	public String getUrl(){return this.url;}
	public String getThumb(){return this.thumb;}
	public String getBrief(){return this.brief;}
	public String getSource(){return this.source;}
	public String getAuthor(){return this.author;}
	public long getCTime(){return this.ctime;}
	public String getmType(){return this.mtype;}
	public String getDesc(){return this.description;}
	public int getClick(){return this.click;}
	public List<NewsInfo> getRelated(){return this.related;}
	
	public void setNewsid(String newsid){this.newsid=newsid;}
	public void setTitle(String title){this.title=title;}
	public void setUrl(String url){this.url=url;}
	public void setThumb(String thumb){this.thumb=thumb;}
	public void setBrief(String brief){this.brief=brief;}
	public void setSource(String source){this.source=source;}
	public void setCTime(long ctime){this.ctime=ctime;}
	public void setmType(String mtype){this.mtype=mtype;}
	public void setDesc(String description){this.description=description;}
	public void setAuthor(String author){this.author=author;}
	public void setClick(int click){this.click=click;}
	public void setRelated(List<NewsInfo> related){this.related=related;}

}
