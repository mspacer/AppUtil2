package ibs.common.access;

import ibs.common.access.servlet.CheckContext;
import ibs.common.servlet.ServletUtils;
import ibs.si.logging.managers.EventManagerHandler;
import ibs.util.access.AccessChecker;
import ibs.util.db.DataBaseHelper;

import javax.servlet.http.HttpServletRequest;

public class DefaultAccessChecker extends AccessChecker {
	private static final String MSG_PREFIX = "Запрошен доступ к модификации объекта хранения ";
	private static final String MSG_START = "Выполнение операции невозможно.\nОтсутствуют права доступа к данным следующих категорий: ";
	private static final String MSG_END = ".\nДля предоставления доступа необходимо подать заявку по форме А.1 в соответствии с Регламентом подключения пользователей к услугам, предоставляемым в рамках ЕИСПД.";
	private final String messageStart;
	private final String messageEnd;
	
	protected DefaultAccessChecker(DataBaseHelper dataBaseHelper, String system, String messageStart, String messageEnd) {
		super(dataBaseHelper, system);
		this.messageStart = messageStart;
		this.messageEnd = messageEnd;
	}

	public DefaultAccessChecker(DataBaseHelper dataBaseHelper, String system) {
		this(dataBaseHelper, system
			, MSG_START
			, MSG_END);
	}

	public final boolean checkUnlessAccessible(String objectType, String objectId, StringBuffer message) throws Exception {
		boolean result = super.checkUnlessAccessible(objectType, objectId, message);
		if(result) {
			onDenied(objectType, objectId, message.toString());
			message.insert(0, messageStart).append(messageEnd);
		} else {
			onGranted(objectType, objectId, null);
		}
		return result;
	}
	
	protected void addEvent(String objectType, String objectId, String result) {
		addEvent(objectType, objectId, result, null);
	}

	protected void addEvent(String objectType, String objectId, String result, String msgPostfix) {
		HttpServletRequest request = CheckContext.getContext().getRequest();
		StringBuffer message = new StringBuffer(MSG_PREFIX).append(objectType);
		StringBuffer objectName = new StringBuffer(objectType);
		if(null != objectId) {
			message.append(" с идентификатором ").append(objectId);
			objectName.append('[').append(objectId).append(']');
		}
		if (null != msgPostfix) {
			message.append(msgPostfix);
		}
		EventManagerHandler.getInstance().addEvent(
			getSystem(),
			"CheckEditAccess",
			EventManagerHandler.hostname,
			message.toString(),
			ServletUtils.getUserDN(request),
			ServletUtils.getUserIP(request),
			result,
			objectType,
			objectName.toString(),
			System.currentTimeMillis()
		);
	}

	protected void onDenied(String objectType, String objectId, String categoryIds) {
		addEvent(objectType, objectId, "AccessDenied", "\n" + MSG_START + categoryIds);
	}

	protected void onGranted(String objectType, String objectId, String categoryIds) {
		addEvent(objectType, objectId, "AccessGranted", categoryIds);
	}
}
