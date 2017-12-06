package ibs.common.access.servlet;

import javax.servlet.http.HttpServletRequest;

public final class CheckContext {
	private static final ThreadLocal localContext = new ThreadLocal();
	private final HttpServletRequest request;

	private CheckContext(HttpServletRequest request) {
		this.request = request;
	}

	public static CheckContext getContext() {
		return (CheckContext) localContext.get();
	}

	protected static CheckContext createContext(HttpServletRequest request) {
		CheckContext context = new CheckContext(request);
		localContext.set(context);
		return context;
	}

	protected void remove() {
		localContext.set(null);
	}

	public HttpServletRequest getRequest() {
		return request;
	}
}
