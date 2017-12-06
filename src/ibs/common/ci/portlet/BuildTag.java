package ibs.common.ci.portlet;

import ibs.common.ci.BuildTagBase;
import ru.simpleit.portlet.xwork.PortletActionContext;


public final class BuildTag extends BuildTagBase {
	private static BuildTag instance = new BuildTag();

	private BuildTag() {
		super(PortletActionContext.getPortletConfig().getContext());
	}

	public static BuildTag getInstance(){
		return instance;		
	}
}
