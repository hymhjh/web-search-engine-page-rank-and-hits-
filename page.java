package pagerank;

public class page {

	int id;      //id for all pages
	String url;  //the url of pages
	int length; //the size of web page
	
	public page(int i, String u, int l){
		this.id = i;
		this.url = u;
		this.length = l;
	}
	public String print(){
		String s = "";
		s = String.valueOf(this.id) + " " + this.url + " " + String.valueOf(this.length) +"\n";
		return s;
	}
}
