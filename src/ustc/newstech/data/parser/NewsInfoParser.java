package ustc.newstech.data.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class NewsInfoParser {
	public static List<NewsInfo> parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		List<NewsInfo> vods = null;  
		List<NewsInfo> relateds = null;
        NewsInfo vod = null;
        
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");               
        
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
                vods = new ArrayList<NewsInfo>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("item")) {  
                    vod = new NewsInfo();                  
                } else if (parser.getName().equals("newsid")) {  
                    eventType = parser.next();  
                    vod.setNewsid(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("title")) {  
                    eventType = parser.next();  
                    vod.setTitle(parser.getText()==null?"":parser.getText());  
                } else if(parser.getName().equals("url")){
                	eventType = parser.next();  
                    vod.setUrl(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("thumb")){
                	eventType = parser.next();  
                    vod.setThumb(parser.getText()==null?NewsInfo.noImage:parser.getText());
                }else if(parser.getName().equals("brief")){
                	eventType = parser.next();  
                    vod.setBrief(parser.getText()==null?"":parser.getText());                
                }else if(parser.getName().equals("source")){
                	eventType = parser.next();  
                    vod.setSource(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("ctime")){
                	eventType = parser.next();  
                    vod.setCTime(Long.parseLong(parser.getText()==null?"0":parser.getText()));
                } else if (parser.getName().equals("author")) {  
                    eventType = parser.next();  
                    vod.setAuthor(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("description")) {  
                    eventType = parser.next();  
                    vod.setDesc(parser.getText()==null?"":parser.getText()); 
                }else if(parser.getName().equals("mtype")){
                	eventType = parser.next();  
                    vod.setmType(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("click")){
                	eventType = parser.next();  
                    vod.setClick(Integer.parseInt(parser.getText()==null?"0":parser.getText()));
                }else if(parser.getName().equals("related")){
                	relateds=parseRelateds(parser);
                	vod.setRelated(relateds);
                }                 
                break;
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("item")) {  
                    vods.add(vod);  
                    vod = null;      
                }  
                break;  
            }
            eventType = parser.next();  
        }
        
		return vods;
	}
	public static ArrayList<NewsInfo> parseRelateds(XmlPullParser parser) throws XmlPullParserException, IOException{
		ArrayList<NewsInfo> vods=new ArrayList<NewsInfo>();
		NewsInfo vod=null;
		int eventType = parser.next();
		boolean relend=false;
		while (true&&!relend) {  
            switch (eventType) {              
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("ritem")) {  
                    vod = new NewsInfo();                  
                } else if (parser.getName().equals("rnewsid")) {  
                    eventType = parser.next();  
                    vod.setNewsid(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("rtitle")) {  
                    eventType = parser.next();  
                    vod.setTitle(parser.getText()==null?"":parser.getText());  
                } else if(parser.getName().equals("rurl")){
                	eventType = parser.next();  
                    vod.setUrl(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("rthumb")){
                	eventType = parser.next();  
                    vod.setThumb(parser.getText()==null?NewsInfo.noImage:parser.getText());
                }else if(parser.getName().equals("rbrief")){
                	eventType = parser.next();  
                    vod.setBrief(parser.getText()==null?"":parser.getText());                
                }else if(parser.getName().equals("rsource")){
                	eventType = parser.next();  
                    vod.setSource(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("rctime")){
                	eventType = parser.next();  
                    vod.setCTime(Long.parseLong(parser.getText()==null?"0":parser.getText()));
                } else if (parser.getName().equals("rauthor")) {  
                    eventType = parser.next();  
                    vod.setAuthor(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("rdescription")) {  
                    eventType = parser.next();  
                    vod.setDesc(parser.getText()==null?"":parser.getText()); 
                }else if(parser.getName().equals("rmtype")){
                	eventType = parser.next();  
                    vod.setmType(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("rclick")){
                	eventType = parser.next();  
                    vod.setClick(Integer.parseInt(parser.getText()==null?"0":parser.getText()));
                }else if(parser.getName().equals("rrelated")){
                	eventType = parser.next();  
                    vod.setRelated(new ArrayList<NewsInfo>());
                }                 
                break;
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("ritem")) {  
                    vods.add(vod);  
                    vod = null;      
                }else if(parser.getName().equals("related")){
                	relend=true;
                }  
                break;  
            }
            if(!relend)eventType = parser.next();  
        }
		return vods;
	}
}
