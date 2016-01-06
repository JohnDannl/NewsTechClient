package ustc.newstech;

import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ObjectFragment extends Fragment  {
	public static final String ARG_OBJECT = "object";	
	private Drawable[] imageS= new Drawable[5];
	
	@Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.		
	     View rootView = inflater.inflate(R.layout.fragment_object, container, false);
		 initPageContent();
	     Bundle args = getArguments();
	     ImageView imageView =(ImageView)rootView.findViewById(R.id.imageId);
			imageView.setImageDrawable(imageS[args.getInt(ARG_OBJECT)]);
	     ((TextView) rootView.findViewById(R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
	     return rootView;
	 }
	
	public void initPageContent(){
    	imageS[0] = getResources().getDrawable(R.drawable.ic_menu_home);
    	imageS[1] = getResources().getDrawable(R.drawable.ic_menu_search);
    	imageS[2] = getResources().getDrawable(R.drawable.ic_menu_star);
    	imageS[3] = getResources().getDrawable(R.drawable.ic_menu_recent_history);
    	imageS[4] = getResources().getDrawable(R.drawable.ic_menu_info_details);
    }
}
