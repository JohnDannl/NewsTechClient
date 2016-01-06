package ustc.newstech.history;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryNews {
	private String newsid,title,url;
	private long ctime,rtime;
	public HistoryNews(String newsid,String title,String url,long ctime,long rtime){
		this.newsid=newsid;
		this.title=title;
		this.url=url;
		this.ctime=ctime;
		this.rtime=rtime;
	}
	public String getNewsid(){return this.newsid;}
	public String getTitle(){return this.title;}
	public String getUrl(){return this.url;}
	/** 
	 * @return in seconds
	 */
	public long getCTime(){return this.ctime;}	
	/** 
	 * @return in milliseconds
	 */
	public long getRTime(){return this.rtime;}
	public static String getTimeStamp(long milliseconds){
		Date date = new Date(milliseconds); 
		//DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-EEEE",Locale.CHINA);
		DateFormat sdf = new SimpleDateFormat("EEEE MM-dd-H:mm",Locale.CHINA);
		return sdf.format(date);
	}
	public static long getPreViousDayMS(int i){
		Date date= new Date(); 
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		calendar.set(Calendar.HOUR_OF_DAY, -i*24); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
}
