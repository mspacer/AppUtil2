package ibs.common.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public final class ServletBackFilter implements Filter {
	private Pattern servletPattern;

	public void init(FilterConfig filterConfig) throws ServletException {
		// WAS 5.1 issue, not required in newer versions
		String servletPatternParam = filterConfig.getInitParameter("servlet-pattern");
		if(null != servletPatternParam) {
			servletPattern = Pattern.compile(servletPatternParam);
		}
	}

	public void destroy() {
		// do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		boolean result = request instanceof HttpServletRequest;
		if(result) {
			String servletPath = ((HttpServletRequest) request).getServletPath();
			result = null != servletPath && 0 != servletPath.length();
			if(result) {
				result = null == servletPattern || servletPattern.matcher(servletPath).matches();
				if(result) {
					request.getRequestDispatcher(servletPath).forward(request, response);
				}
			}
		}
		if(!result) {
			filterChain.doFilter(request, response);
		}
	}
}
