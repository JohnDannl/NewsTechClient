package ustc.newstech.login;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LoginHelper {
	public static final String TAG="XXXLoginHelper";
	public static final String hostName="http://222.195.78.181:8897"; //222.195.78.181
	public static final String loginHost=hostName+"/login";
	public static final String dupHost=hostName+"/duplicate";
	public static final String signinHost=hostName+"/signin";
	private static final String[] loginAction={"/login","/changeinfo","/changepassword"};
	private static final String code_welcome="Welcome",
								code_submit="Submit duplication successfully",
								code_changepassword="Change password successfully",
								code_changeinfo="Change info successfully",
								code_register_failure="This device has been registered",
								code_login_failure="User authorization failure";
	
	public String cookie_para,name,email,password,userid;
	private DefaultHttpClient httpClient = new DefaultHttpClient();
	
	public LoginHelper(String name,String password,String userid){
		this.name=name;
		this.password=password;
		this.userid=userid;
		httpClient.setRedirectHandler(new RedirectHandler(){

			@Override
			public URI getLocationURI(HttpResponse arg0, HttpContext arg1)
					throws ProtocolException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isRedirectRequested(HttpResponse arg0,
					HttpContext arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}
	public void setEmail(String email){this.email=email;}
	public void setCookie(String cookie){this.cookie_para=cookie;}
	public String getEmail(){return this.email;}
	public String getCookie(){return this.cookie_para;}
	
	/**
	 * 
	 * @param newsid1
	 * @param newsid2
	 * @return "user not logins" or "Submit duplication successfully"
	 * @throws ParseException 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String submitDuplicate(String newsid1,String newsid2) throws ParseException, IOException, URISyntaxException{
		String url=dupHost+"/news?newsid1="+URLEncoder.encode(newsid1, "utf-8")+
				"&newsid2="+URLEncoder.encode(newsid2, "utf-8");
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Cookie", cookie_para);
		HttpResponse response = httpClient.execute(httpGet);	
		int httpcode=response.getStatusLine().getStatusCode();	    
	    if(httpcode==302){
	    	Header location=response.getFirstHeader("Location");
	    	String redirection=null;
	    	if(location!=null&&!TextUtils.isEmpty(location.getValue())){
	    		redirection=location.getValue();
	    		if(redirection!=null&&redirection.equals("/login/login"))return "User not logins";
	    	}		    	
	    }
	    HttpEntity entity = response.getEntity();
	    return EntityUtils.toString(entity);		
	}
	/**
	 * 
	 * @return user info or "User authorization failure" or <br>
	 * "User not exists" or "No authorization" or "Login failure"
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String login() throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(loginHost+loginAction[0]);
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userid", userid));
		nvps.add(new BasicNameValuePair("name", name));
		nvps.add(new BasicNameValuePair("password",password));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpClient.execute(httpPost);
	    int httpcode=response.getStatusLine().getStatusCode();	    
	    if(httpcode==302){
	    	Header setCookie=response.getFirstHeader("Set-Cookie");
	    	if(setCookie!=null&& !TextUtils.isEmpty(setCookie.getValue())){
	    		cookie_para=setCookie.getValue();
	    		//Log.d(TAG, cookie_para);
	    	}    	
	    	Header location=response.getFirstHeader("Location");
	    	String redirection=null;
	    	if(location!=null&&!TextUtils.isEmpty(location.getValue())){
	    		redirection=location.getValue();
	    		if(redirection.equals("/signin"))return "User not exists";
	    	}
	    	if(cookie_para!=null&&redirection!=null){
	    		redirection=hostName+redirection;
	    		HttpGet httpGet = new HttpGet(redirection);
	    		httpGet.addHeader("Cookie", cookie_para);
	    		HttpResponse response2 = httpClient.execute(httpGet);
	    		if(response2.getStatusLine().getStatusCode()==200)
	    			return EntityUtils.toString(response2.getEntity(),"utf-8");
	    	}		    	
	    }else if(httpcode==200)
	    	return EntityUtils.toString(response.getEntity());
		return "Login failure";
	}
	/**
	 * 
	 * @return "This device has been registered" or "Register successfully" or
	 *  "Register failure"
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String register() throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(signinHost);		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userid", userid));
		nvps.add(new BasicNameValuePair("name", name));
		nvps.add(new BasicNameValuePair("email", email));
		nvps.add(new BasicNameValuePair("password",password));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpClient.execute(httpPost);
		HttpResponse response2=null;
	    int httpcode=response.getStatusLine().getStatusCode();
	    String redir_loc=null;
	    if(httpcode==302){//redirect to /welcome url
	    	for(Header header:response.getAllHeaders()){
		    	if(header.getName().equals("Location")){
		    		redir_loc= header.getValue();
		    		redir_loc=hostName+redir_loc;
		    	}    	
		    }
	    	if(redir_loc!=null){	
	    		HttpGet httpGet = new HttpGet(redir_loc);
	    		response2 = httpClient.execute(httpGet);
	    		if(response2.getStatusLine().getStatusCode()==200
	    				&&EntityUtils.toString(response2.getEntity()).contains("/login/login"))
	    			return "Register successfully";
	    	}
	    }
	    else return EntityUtils.toString(response.getEntity());
	
		return "Register failure";
	}
	/**
	 * 
	 * @param newPassword
	 * @return "User authorization failure" or "Change password successfully" or<br>
	 * "User not exists"
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String changPassword(String newPassword) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(loginHost+loginAction[2]);		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userid", userid));
		nvps.add(new BasicNameValuePair("name", name));
		nvps.add(new BasicNameValuePair("password",password));
		nvps.add(new BasicNameValuePair("newpassword",newPassword));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpClient.execute(httpPost);		
		HttpResponse response2=null;
	    int httpcode=response.getStatusLine().getStatusCode();
	    String redir_loc=null;
	    if(httpcode==302){//redirect to /welcome url
	    	for(Header header:response.getAllHeaders()){
		    	if(header.getName().equals("Location")){
		    		redir_loc= header.getValue();
		    		redir_loc=hostName+redir_loc;
		    	}    	
		    }
	    	if(redir_loc!=null){	
	    		HttpGet httpGet = new HttpGet(redir_loc);
	    		response2 = httpClient.execute(httpGet);
	    		if(response2.getStatusLine().getStatusCode()==200
	    				&&EntityUtils.toString(response2.getEntity()).contains("/signin"))
	    			return "User not exists";
	    	}
	    }	
		String result=EntityUtils.toString(response.getEntity());
		return result;	
	}
	/**
	 * 
	 * @param newName
	 * @param newEmail
	 * @return "Change info successfully" or "User authorization failure" or<br>
	 * "User not exists
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String changInfo(String newName,String newEmail) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(loginHost+loginAction[1]);		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userid", userid));
		nvps.add(new BasicNameValuePair("name", name));
		nvps.add(new BasicNameValuePair("password",password));
		nvps.add(new BasicNameValuePair("newname",newName));
		nvps.add(new BasicNameValuePair("newemail",newEmail));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpClient.execute(httpPost);	
		HttpResponse response2=null;
	    int httpcode=response.getStatusLine().getStatusCode();
	    String redir_loc=null;
	    if(httpcode==302){//redirect to /welcome url
	    	for(Header header:response.getAllHeaders()){
		    	if(header.getName().equals("Location")){
		    		redir_loc= header.getValue();
		    		redir_loc=hostName+redir_loc;
		    	}    	
		    }
	    	if(redir_loc!=null){	
	    		HttpGet httpGet = new HttpGet(redir_loc);
	    		response2 = httpClient.execute(httpGet);
	    		if(response2.getStatusLine().getStatusCode()==200
	    				&&EntityUtils.toString(response2.getEntity()).contains("/signin"))
	    			return "User not exists";
	    	}
	    }	
		String result=EntityUtils.toString(response.getEntity());
		return result;		
	}
	/*public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException{
		LoginHelper helper=new LoginHelper("dannl","12345","004");
		System.out.println(helper.login());
		System.out.println(helper.getCookie());
		if(helper.submitDuplicate("123","456").contains("/login/login")){
			if(helper.login())
			System.out.println(helper.getCookie());
			System.out.println(helper.submitDuplicate("123","456"));
		}
//		System.out.println(helper.register());
	}*/
}
