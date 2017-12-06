package ibs.common.ci;

import ibs.util.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

public class VersionReport {
	private final static Logger log = Logger.getLogger(VersionReport.class);
	private static final ManifestFilter DEFAULT_FILTER = new ManifestFilter() {
		public boolean accept(Manifest manifest) {
			return (manifest.getMainAttributes().getValue("Revision")!= null);
		}
	};

	private final ManifestFilter filter;

	public VersionReport( ManifestFilter aFilter ){
		this.filter = aFilter;
	}

	public VersionReport(){
		this( DEFAULT_FILTER );
	}

	public List getArtifacts() throws IOException{
		List result = new ArrayList();
		Enumeration urls = Thread.currentThread().getContextClassLoader().getResources("/META-INF/MANIFEST.MF");
		while( urls.hasMoreElements() ){
			URL manifestURL = (URL) urls.nextElement();
			Manifest manifest = getManifestFromURL(manifestURL);
			if ( manifest != null && filter.accept(manifest) ){
				ArtifactBean artifact = getArtifactFromManifest(manifest);
				result.add(artifact);						
			}
		}
		
		return result;
	}
	
	
	
	private Manifest getManifestFromURL( URL url){
		InputStream is = null;
		Manifest result = null;
		try{
			is = url.openStream();
			result = new Manifest(is);			
		}catch( IOException e){
			log.error("Exception in #getManifestFromURL",e);
		}finally{
			IOUtil.closeStream(is);
		}
		return result;
	}
	
	
	private ArtifactBean getArtifactFromManifest( Manifest manifest ){
		Attributes attrs = manifest.getMainAttributes();
		ArtifactBean result = new ArtifactBean();
		result.setName(attrs.getValue("Specification-Title") );
		result.setTag(attrs.getValue("Build-tag") );
		result.setVersion( attrs.getValue("Revision") );
		result.setBuildTime( attrs.getValue("Implementation-Version") );		
		
		return result;
	}
}
