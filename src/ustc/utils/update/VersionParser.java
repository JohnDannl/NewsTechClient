package ustc.utils.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.util.Xml;

public class VersionParser {
	
	public VersionParser(){}	
	
	
	public static ServerVersionInfo parse(InputStream is){  
	    XmlPullParser  parser = Xml.newPullParser();    
	    ServerVersionInfo info = new ServerVersionInfo();
	    try {
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();  		    
		    while(type != XmlPullParser.END_DOCUMENT ){  
		        switch (type) {  
		        case XmlPullParser.START_TAG:  
		        	if("versionCode".equals(parser.getName())){
		        		info.setVersionCode(Integer.parseInt(parser.nextText()));
//		        		Log.d("XXXXXXXXXX", String.valueOf(info.getVersionCode()));
		        	}
		            if("versionName".equals(parser.getName())){  
		                info.setVersionName(parser.nextText()); 
		            }else if ("apkUrl".equals(parser.getName())){  
		                info.setApkUrl(parser.nextText()); 
		            }else if ("description".equals(parser.getName())){  
		                info.setDescription(parser.nextText()); 
		            }  
		            break;  
		        }  
		        type = parser.next();  
		    }  
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return info;  
	} 	
}
