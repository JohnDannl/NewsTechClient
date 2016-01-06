package ustc.newstech;

import android.app.Application;

public class MyApplication extends Application {
	
    public static boolean start_launch=true;
    
	public MyApplication(){
		// this method fires only once just when application starts. 
        // getApplicationContext returns null here
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
        // this method fires once as the constructor 
        // but also application has context here
		start_launch=true;
	}
}
