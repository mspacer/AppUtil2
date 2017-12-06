package ibs.common.ci.servlet;

import ibs.common.ci.BuildTagBase;

import org.apache.struts2.ServletActionContext;


public final class BuildTag extends BuildTagBase {
	private static BuildTag instance = new BuildTag();

	private BuildTag() {
		super(ServletActionContext.getServletContext());
	}

	public static BuildTag getInstance(){
		return instance;		
	}
}
