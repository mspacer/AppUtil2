package ibs.common.logging;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/** 
 *  ���������� ������ ��� ���������� �������� �� �����.
 *  ��� ��� ������ � WEB-INF/web.xml ���������� �������� ��������� ������:
 * <pre>
 * 	&lt;filter&gt;
 *		&lt;filter-name&gt;log4j&lt;/filter-name&gt;
 *		&lt;display-name&gt;log4j&lt;/display-name&gt;
 *		&lt;filter-class&gt;{@link ibs.common.logging.MdcServletFilter}&lt;/filter-class&gt;
 *		&lt;init-param&gt;
 *			&lt;param-name&gt;<i>����_MDC</i>&lt;/param-name&gt;
 *			&lt;param-value&gt;<i>����_������</i>&lt;/param-value&gt;
 *		&lt;/init-param&gt;
 *		&lt;!-- ����� ���������� �������������� ���������� � ������� --&gt;
 *	&lt;/filter&gt;
 *	&lt;filter-mapping&gt;
 *		&lt;filter-name&gt;log4j&lt;/filter-name&gt;
 *		&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *	&lt;/filter-mapping&gt;
 * </pre>
 * ���:<ul>
 * <li><code><i>����_MDC</i></code> - ����, �� �������� ����� ����� ��������
 * ��������������� �������� � ���-����;</li>
 * <li><code><i>����_������</i></code> - ����, �� �������� �������� �����
 * �������� �� ������.</li></ul>
 * ������ �������� �� ������ ����� ������������ ���������
 * ��������� ��� ���� <code><i>����_������</i></code>:
 * <ul>
 * <li><code><i>threadId</i></code> - �������� {@link Thread#hashCode()}
 * ��� {@link Thread#currentThread()};</li>
 * <li><code><i>userName</i></code> - �������� {@link Principal#getName()}
 * ��� {@link HttpServletRequest#getUserPrincipal()};</li>
 * <li><code><i>sessionId</i></code> - �������� {@link HttpSession#getId()}
 * ��� {@link HttpServletRequest#getSession()}.</li> 
 * </ul>
 * ��� ��������� �������� � ���-���� ���������� ��������� �������� ��������� �������:
 * <pre>
 * log4j.appender.<i>���_���������</i>.layout.ConversionPattern=...%X{<i>����_MDC</i>}...
 * </pre>
 * @author mkarajani
 */
public class MdcServletFilter implements Filter {
	private static final Logger logger = Logger.getLogger(MdcServletFilter.class);
	private final Map params = new HashMap();
	
	public void init(FilterConfig config) throws ServletException {
		Enumeration names = config.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			params.put(name, config.getInitParameter(name));
		}
		if(logger.isDebugEnabled()) {
			logger.debug("#init: params: "+params);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			try {
				if(request instanceof HttpServletRequest) {
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					String userName = null;
					Principal userPrincipal = httpRequest.getUserPrincipal();
					if(null != userPrincipal) {
						userName = userPrincipal.getName();
					}
					HttpSession session = httpRequest.getSession();
					String sessionId = session.getId();
					if(logger.isDebugEnabled()) {
						logger.debug("#doFilter: userPrincipal: "+userPrincipal+", userName: "+userName);
						logger.debug("#doFilter: sessionId: "+sessionId+", session: "+session);
						Enumeration attributeNames = session.getAttributeNames();
						while (attributeNames.hasMoreElements()) {
							String attributeName = (String) attributeNames.nextElement();
							logger.debug("#doFilter: session["+attributeName+"]: "+session.getAttribute(attributeName));
						}
					}
					Iterator iterator = params.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry entry = (Entry) iterator.next();
						String key = (String) entry.getKey();
						String value = (String) entry.getValue();
						if("threadId".equals(value)) {
							MDC.put(key, String.valueOf(Thread.currentThread().hashCode()));
						} else if("userName".equals(value)) {
							putMDC(key, userName);
						} else if("sessionId".equals(value)) {
							putMDC(key, sessionId);
						} else {
							putMDC(key, session.getAttribute(value));
						}
					}
				} else {
					logger.warn("#doFilter request is "+request.getClass().getName());
				}
			} catch (Throwable t) {
				logger.error("#doFilter: error: ", t);
			}
	        chain.doFilter(request, response);
		} finally {
			Iterator iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				MDC.remove((String) iterator.next());
			}
		}
	}

	private static void putMDC(String key, Object value) {
		if(null != value) {
			MDC.put(key, value);
		}
	}

	public void destroy() {
		// do nothing
	}
}
