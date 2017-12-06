package ibs.common.access.servlet;

import ibs.util.access.AccessChecker;
import ibs.util.db.DataBaseHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class AccessCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AccessCheckServlet.class);
	private AccessChecker accessChecker;
	
	protected final String getRequiredParam(ServletConfig config, String paramName) throws ServletException {
		String result = config.getInitParameter(paramName);
		if(null == result) {
			throw new ServletException(new StringBuffer("Init param `").append(paramName)
				.append("' is required for servlet ").append(getClass().getName()).toString());
		}
		return result;
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String databaseProperties = getRequiredParam(config, "database-properties");
		DataBaseHelper dataBaseHelper = new DataBaseHelper(databaseProperties, "");
		String system = getRequiredParam(config, "system");
		String checkerClassName = getRequiredParam(config, "checker-class");
		Class checkerClass;
		try {
			checkerClass = Thread.currentThread().getContextClassLoader().loadClass(checkerClassName);
		} catch (ClassNotFoundException e) {
			throw new ServletException(new StringBuffer("Checker class `").append(checkerClassName).append("' not found").toString(), e);
		}
		Object checkerObject;
		try {
			checkerObject = checkerClass.getConstructor(new Class[] {DataBaseHelper.class, String.class}).newInstance(new Object[]{dataBaseHelper, system});
		} catch (Throwable t) {
			throw new ServletException(new StringBuffer("Checker class `").append(checkerClassName).append("' can not be instantiated").toString(), t);
		}
		if(!(checkerObject instanceof AccessChecker)) {
			throw new ServletException(new StringBuffer("Checker class `").append(checkerClassName).append("' is not instance of ").append(AccessChecker.class.getName()).toString());
		}
		accessChecker = (AccessChecker) checkerObject;
	}

	protected static final class ResultBean {
		private final boolean result;
		private final String message;
		public ResultBean(boolean result, String message) {
			this.result = result;
			this.message = message;
		}
		public boolean isResult() {
			return result;
		}
		public String getMessage() {
			return message;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final StringBuffer message = new StringBuffer();
		request.setCharacterEncoding("UTF-8");
		String objectType = getObjectType(request);
		boolean result = null != objectType;
		if(result) {
			CheckContext checkContext = CheckContext.createContext(request);
			try {
				result = !accessChecker.checkUnlessAccessible(objectType, request.getParameter("objectId"), message);
			} catch (Exception e) {
				logger.error("#doPost error", e);
				result = false;
				message.append("Произошла ошибка: ").append(e.getMessage()).append('.');
			} finally {
				checkContext.remove();
			}
		} else {
			message.append("Параметр `objectType' является обязательным для проверки доступа.");
		}
		
		String jsonResult = new JSONObject(new ResultBean(result, message.toString())).toString();
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		try {
			writer.write(jsonResult);
		} finally {
			writer.close();
		}
	}

	protected String getObjectType(HttpServletRequest req) throws UnsupportedEncodingException {
		return req.getParameter("objectType");
	}
}
