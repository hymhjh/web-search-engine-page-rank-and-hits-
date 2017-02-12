package pagerank;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * This is a Java port of the parser written in C by Xiaohui Long
 * <p>
 * Java port maintained by Alek Dembowski (adembo01 at utopia.poly.edu)
 */
public class Parser {
    private interface Tags{
        static final int PTAG_B = 1;
        static final int PTAG_I = 2;
        static final int PTAG_H = 3;
        static final int PTAG_TITLE = 4;
        static final int PTAG_SCRIPT = 5;
    }

    public static void main(String[] args) {
        System.out.println("Testing: 0010024.e-xpert.co.nz/SITE_Default/SITE_ema/search.asp");
        StringBuilder builder = new StringBuilder();

        parseUrl("0010024.e-xpert.co.nz/SITE_Default/SITE_ema/search.asp", builder);
        //parseUrl("across.co.nz/Canadian%27sView.htm", builder);
        parsePage("", "This is <b> so</b> <> <b>very</b>  <H6>very <b><em>much</h6> fun</em></b>", builder);

        System.out.println(builder);
    }

    /**
     * Parses url and doc, storing result in builder. <b> Not thread safe </b>
     * <p>
     * Parsed results are in the form of
     * <ul>
     *   <li> <code> &lt;word&gt; &lt;context&gt; (line break) </code>
     *   <li> <code> &lt;word&gt; &lt;context&gt; (line break) </code>
     * </ul>
     * <p>
     * For performance, this class uses StringBuilder instead of StringBuffer. If syncronization
     * is required, provide syncronization outside of calling this class.
     *
     * @param url The url of the page being parsed
     * @param doc The page (including headers) of the page being parsed
     * @param builder The place to store the results.
     *
     */
    public static int parseDoc(String url, String doc, StringBuilder builder) {
        doc = initDoc(doc);
        if (doc == null)
            return 0;

        parseUrl(url, builder);
        parsePage(url, doc, builder);

        return 0;
    }

    /**
     * Assumes tag start with a '<' character. Back tags would then be "</"
     */
    static boolean isBackTag(String tag) {
    	if(tag.length()>1){
    		return tag.charAt(1) == '/';
    	}
        return tag.charAt(0) == '/';
    }

    static boolean isIndexable(char c) {
        return ((c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z'));
    }

    static boolean isEndTag(String tag, int i) {
        return ('>' == tag.charAt(i) || Character.isWhitespace(tag.charAt(i)));
    }

    /**
     * Assumes tag starts with a "<" and ends with either a ">" or a whitespace character
     */
    static int parseTag(String tag) {
        int i = 1;
        if(tag.length() < 2){
        	return 0;
        }
        if (isBackTag(tag)) {
            i++;
        }

        switch (tag.charAt(i)) {
            case 'b':
            case 'B':
            case 'i':
            case 'I':
                if (!isEndTag(tag, i + 1))
                    return 0;
                if ((tag.charAt(i) == 'b') || (tag.charAt(i) == 'B'))
                    return Tags.PTAG_B;
                return Tags.PTAG_I;

            case 'e':
            case 'E':
                i++;
                if (((tag.charAt(i) == 'm') || (tag.charAt(i) == 'M')) && isEndTag(tag, i + 1))
                    return Tags.PTAG_I;
                return 0;

            case 'h':
            case 'H':
                i++;
                if (((tag.charAt(i) >= '1') && (tag.charAt(i) <= '6')) && (isEndTag(tag, i + 1)))
                    return Tags.PTAG_H;
                return 0;

            case 't':
            case 'T':
                i++;
                if ("itle".equalsIgnoreCase(safeSubstring(tag, i, i + 4)) && isEndTag(tag, i + 4)) {
                    return Tags.PTAG_TITLE;
                }
                return 0;

            case 's':
            case 'S':
                i++;
                if ("trong".equalsIgnoreCase(safeSubstring(tag, i, i + 5)) && isEndTag(tag, i + 5)) {
                    return Tags.PTAG_B;
                } else
                if ("cript".equalsIgnoreCase(safeSubstring(tag, i, i + 5)) && isEndTag(tag, i + 5)) {
                    return Tags.PTAG_SCRIPT;
                }
                return 0;

            default:
                break;
        }

        return 0;
    }

    private static String safeSubstring(String str, int start, int end){
        if(end > str.length()){
            return str;
        } else {
            return str.substring(start, end);
        }
    }

    static void parseUrl(String url, StringBuilder buf){
        int index = 0;
        char urlCharacter = url.charAt(index);
        while (index < url.length()) {
            if (!isIndexable(urlCharacter)) {
                index++;
                continue;
            }

            int wordEndIndex = index;
            while (wordEndIndex < url.length() && isIndexable(url.charAt(wordEndIndex))) {
                wordEndIndex++;
            }

            if(index != wordEndIndex){
                buf.append(url.substring(index, wordEndIndex).toLowerCase()).append(" ");
            }
            index = wordEndIndex + 1;
        }
        buf.append(" ");
    }

    /**
     * @param doc
     * @return null if failed, actual document after the header if successful
     */
    static String initDoc(String doc){
        if(!"HTTP/".equalsIgnoreCase(doc.substring(0, 5))){
            return null;
        }

        int index = doc.indexOf(' ');

        if(index == -1){
            return null;
        } else if(!"200".equals(doc.substring(index+1, index + 4))){
            return null;
        }

        index = doc.indexOf("\r\n\r\n");
        if(index == -1){
            return null;
        }

        return doc.substring(index + 4);
    }

    static void parsePage(String url, String doc, StringBuilder builder) {
        int docIndex = 0;
        int wordIndex;

        HashMap<String, Integer> map = new  HashMap<String, Integer>();
        int docLength = doc.length();
        while (docIndex < docLength)
        {
            wordIndex = docIndex;

            while (docIndex < docLength && isIndexable(doc.charAt(docIndex))){
                docIndex++;
            }

            String ss = doc.substring(wordIndex, docIndex).toLowerCase();
            docIndex++;
            
            if(ss.equals("")) continue;
            if( map.containsKey(ss)){
            	int temp = map.get(ss);
            	temp++;
            	map.put(ss, temp);
            }
            else {
            	map.put(ss, 1);
            }
        }
       
        
        Set<String> keySet = map.keySet();
        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){
        	String key = iterator.next();  
        	int value = map.get(key);
            builder.append(key+" "+value);
            builder.append("\n");
            //System.out.println(key+"="+value);
        }
    }
}
