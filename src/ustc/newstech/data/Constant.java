package ustc.newstech.data;

public class Constant {
	public static final String host="http://222.195.78.181:8897";
	public static final String testUri="http://f01.v1.cn/group1/M00/01/73/ChQBFVQBJSmAY-GAAHLI_NrXKmY378.flv";
	
	public static final String newsHost=host+"/news/";
	public static final String searchHost=host+"/search/a?";
	public static final String clickHost=host+"/click/news?";
	public static final String suggestHost=host+"/suggest";
	public static final String pa_newsidEq="&newsid=";
	public static final String pa_useridEq="&userid=";
	public static final String pa_numEq="&num=";
	public static final String pa_mtypeEq="&mtype=";
	public static final String pa_clickEq="&click=";
	public static final String pa_ctimeEq="&ctime=";
	public static final String pa_anonymous="anonymous";
	public static final String pa_keywordsEq="&keywords=";
	public static final String pa_pageEq="&page=";
	public static final int pageNum=15;
	public static final String op_top="top?",op_refresh="refresh?",op_more="more?",op_related="related?";
//	merge_en=['newest','hot','world','domestic','society','finance','military','science','entertain','sport','ipai','other']
	public static final String[] category={"newest","hot"};	
}
