package r2rml.util;

public class IRISafe {

	/**
	 * Translate a string into its IRI safe value as per R2RML's steps
	 * 
	 * @param string
	 * @return
	 */
	public static String toIRISafe(String string) {
		// The IRI-safe version of a string is obtained by applying the following 
		// transformation to any character that is not in the iunreserved 
		// production in [RFC3987].
		StringBuffer sb = new StringBuffer();
		for(char c : string.toCharArray()) {
			if(inIUNRESERVED(c)) sb.append(c);
			else sb.append('%' + Integer.toHexString((int) c));
		}
		return sb.toString();
	}
	
	/**
	 *	Check whether the characters are part of iunreserved as per
	 *  https://tools.ietf.org/html/rfc3987#section-2.2
	 */
	private static boolean inIUNRESERVED(char c) {
		if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~".indexOf(c) != -1) return true;
		else if (c >= 160 && c <= 55295) return true;
		else if (c >= 63744 && c <= 64975) return true;
		else if (c >= 65008 && c <= 65519) return true;
		else if (c >= 65536 && c <= 131069) return true;
		else if (c >= 131072 && c <= 196605) return true;
		else if (c >= 196608 && c <= 262141) return true;
		else if (c >= 262144 && c <= 327677) return true;
		else if (c >= 327680 && c <= 393213) return true;
		else if (c >= 393216 && c <= 458749) return true;
		else if (c >= 458752 && c <= 524285) return true;
		else if (c >= 524288 && c <= 589821) return true;
		else if (c >= 589824 && c <= 655357) return true;
		else if (c >= 655360 && c <= 720893) return true;
		else if (c >= 720896 && c <= 786429) return true;
		else if (c >= 786432 && c <= 851965) return true;
		else if (c >= 851968 && c <= 917501) return true;
		else if (c >= 921600 && c <= 983037) return true;
		return false;
	}
	
}
