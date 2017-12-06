package ibs.common.ci;

import ibs.util.io.IOUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class BuildTagBase {
	private final static Logger log = Logger.getLogger(BuildTagBase.class);
	private String tag;

	protected BuildTagBase(ServletContext servletContext){
		InputStream is =null;
		try{
			String manifestPath = servletContext.getRealPath("/META-INF/MANIFEST.MF");
			is = new FileInputStream(manifestPath);
			Manifest manifest = new Manifest(is);
			tag = manifest.getMainAttributes().getValue("Build-tag");
		}catch( Exception e ){
			log.error("Exception in #<init>",e);
		}finally{
			IOUtil.closeStream(is);
		}
		if(null == tag) {
			tag = "Debug-"+System.currentTimeMillis();
		}
	}

	public String getTag(){
		return tag;
	}
}
