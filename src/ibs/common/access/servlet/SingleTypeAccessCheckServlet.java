package ibs.common.access.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public final class SingleTypeAccessCheckServlet extends AccessCheckServlet {
	private static final long serialVersionUID = 1L;
	private String objectType;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		objectType = getRequiredParam(config, "object-type");
	}

	protected String getObjectType(HttpServletRequest req) {
		return objectType;
	}
}
