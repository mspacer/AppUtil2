package ibs.common.ci;

import java.util.jar.Manifest;

public interface ManifestFilter {
	boolean accept( Manifest manifest );
}
