package net.bioclipse.biojava.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * Used to remove the first line (until linebreak) in a string, e.g. to remove header
	 * line in a FASTA string
	 * @param content
	 * @return
	 */
	public static String removeUntilFirstNewline(String content){

		Pattern EVERYTHING_AFTER_FIRST_NEWLINE
	      = Pattern.compile( "[^\\n\\r]*[\\n\\r]+(.*)$", Pattern.DOTALL );

	        Matcher matcher = EVERYTHING_AFTER_FIRST_NEWLINE.matcher(content);
	        if ( matcher.find() ) {
	        	String ret = matcher.group(1);
	            return ret;
	        }
	        else {
//	            System.out.println("The text consists of only one line.");
	            return content;
	        }
	}
	
	
}
