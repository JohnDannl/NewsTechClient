package ustc.newstech.data;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static String testUri="http://f01.v1.cn/group1/M00/01/73/ChQBFVQBJSmAY-GAAHLI_NrXKmY378.flv";
	
	public static String newsHost="http://222.195.78.181:8897/news/";
	public static String searchHost="http://222.195.78.181:8897/search/a?";
	public static String dupHost="http://222.195.78.181:8897/duplicate/news?";
	public static String clickHost="http://222.195.78.181:8897/click/news?";
	public static String pa_newsidEq="&newsid=";
	public static String pa_numEq="&num=";
	public static String pa_mtypeEq="&mtype=";
	public static String pa_clickEq="&click=";
	public static String pa_ctimeEq="&ctime=";
	public static String pa_useridEq="&userid=";
	public static String pa_modeEq="&mode=";
	public static String pa_anonymous="anonymous";
	public static String pa_keywordsEq="&keywords=";
	public static String pa_pageEq="&page=";
	public static String mode_brief="brief";
	public static String mode_detail="detail";
	public static int pageNum=15;
	public static String op_top="top?",op_refresh="refresh?",op_more="more?",op_related="related?";
//	merge_en=['newest','hot','world','domestic','society','finance','military','science','entertain','sport','ipai','other']
	public static String[] category={"newest","hot"};	
}
