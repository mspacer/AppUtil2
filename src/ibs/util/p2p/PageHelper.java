package ibs.util.p2p;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import ru.simpleit.portlet.xwork.PortletActionContext;
import ru.simpleit.util.portal.NavigationHelper;
import ru.simpleit.util.portal.PortalPage;

public abstract class PageHelper {
	private static final Logger logger = Logger.getLogger(PageHelper.class);
	private static PageHelper instance;

	private PageHelper() {
		// do nothing
	}

	public abstract String toUrl(String key, String value, String defaultResult);

	public String toUrl(String key, String value) {
		return toUrl(key, value, null);
	}

	public String toUrl(String key) {
		return toUrl(key, null);
	}

	public static String getBackNode() {
		return new NavigationHelper(PortletActionContext.getPortletRequest()).getCurrentPage().getId();
	}

	public static String nodeToUrl(String node, String defaultResult) {
		PortalPage page = null;
		if (null != node) {
			try {
				NavigationHelper navHelper = new NavigationHelper(PortletActionContext.getPortletRequest());
				page = navHelper.getPage(node);		
			} catch( Throwable t ) {
				logger.error("#nodeToUrl", t);
			}
		}
		return page == null ? defaultResult : page.getUri();
	}
	
	public static String nodeToUrl(String node) {
		return nodeToUrl(node, null);
	}

	public static synchronized PageHelper getInstance() {
		if(null == instance) {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("p2p.properties");
			boolean oldMode = null == inputStream;
			if(!oldMode) {
				try {
					final Properties properties = new Properties();
					try {
						properties.load(inputStream);
						String mode = properties.getProperty("mode", "page-bean");
						if("url-context".equals(mode)) {
							instance = new PageHelper() {
								public String toUrl(String key, String value, String defaultResult) {
									String result;
									if(properties.containsKey(key)) {
										result = new StringBuffer(properties.getProperty("base-url", "/wps/myportal"))
											.append(properties.getProperty(key)).toString(); 
									} else {
										result = defaultResult;
									}
									return result;
								}
							};
						}
					} catch (IOException e) {
						logger.error("#getInstance: load", e);
					}
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.error("#getInstance: close", e);
					}
				}
			}
			if(null == instance) {
				instance = new PageHelper() {
					public String toUrl(String key, String value, String defaultResult) {
						return nodeToUrl(value, defaultResult);
					}
				};
			}
		}
		return instance;
	}
}
