package ustc.newstech.data.parser;

public class UserInfo {
	private String userid,name,email;
	private long registertime;
	public UserInfo(){}
	public UserInfo(String userid,String name,String email,long registertime){
		this.userid=userid;
		this.name=name;
		this.email=email;
		this.registertime=registertime;
	}
	public void setUserId(String userid){this.userid=userid;}
	public void setName(String name){this.name=name;}
	public void setEmail(String email){this.email=email;}
	public void setRegisterTime(long registertime){this.registertime=registertime;}
	
	public String getUserId(){return this.userid;}
	public String getName(){return this.name;}
	public String getEmail(){return this.email;}
	public long getRegisterTime(){return this.registertime;}
	
}
