package org.eclipse.cdt.debug.ui.breakpoints;

import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.ui.contexts.IDebugContextProvider;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @since 8.0
 */
public interface ICRocmBreakpointContext extends IDebugContextProvider {
	
	public ICBreakpoint getBreakpoint();
	
	public IResource getResource();
	
	public IPreferenceStore getPreferenceStore();

}
