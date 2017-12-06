package ibs.common.servlet;

import ibs.wmm.common.UserFacadeFactory;
import ibs.wmm.common.WMMFacadeException;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public final class ServletUtils {
	private static final Logger logger = Logger.getLogger(ServletUtils.class);

	private ServletUtils() {
		// do nothing
	}

	public static String getUserIP(HttpServletRequest request) {
		String result = request.getHeader("X-Forwarded-For");
		if (null == result || 0 == result.length()) {
			result = request.getRemoteAddr();
 		}
		return result;
	}

	public static String getUserDN(HttpServletRequest request) {
		String result = null;
		Principal userPrincipal = request.getUserPrincipal();
		if(null != userPrincipal) {
			try {
				result = UserFacadeFactory.getFacade().getUserByAccountName(userPrincipal.getName()).getDN();
			} catch (WMMFacadeException e) {
				logger.error("#userDN", e);
			}
		}
		return result;
	}
}
