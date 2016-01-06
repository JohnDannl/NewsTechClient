package ustc.newstech.data.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class UserInfoParser {
	public static UserInfo parse(String content) throws XmlPullParserException, IOException{
		UserInfo userInfo=null;
		XmlPullParser parser = Xml.newPullParser(); 
		parser.setInput(new StringReader(content));
		int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	userInfo = new UserInfo();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("user")) {  
                    userInfo = new UserInfo();                  
                } else if (parser.getName().equals("userid")) {  
                    eventType = parser.next();  
                    userInfo.setUserId(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("name")) {  
                    eventType = parser.next();  
                    userInfo.setName(parser.getText()==null?"":parser.getText());  
                } else if(parser.getName().equals("email")){
                	eventType = parser.next();  
                    userInfo.setEmail(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("registertime")){
                	eventType = parser.next();  
                    userInfo.setRegisterTime(Long.parseLong(parser.getText()==null?"":parser.getText()));
                }            
                break;
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("user")) {  
                }  
                break;  
            }
            eventType = parser.next();  
        }
		return userInfo;
	}
}
