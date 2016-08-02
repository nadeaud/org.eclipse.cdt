package org.eclipse.cdt.debug.internal.ui.breakpoints;

import java.util.Map;

import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.cdt.debug.ui.breakpoints.ICRocmBreakpointContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

public class CRocmBreakpointContext extends PlatformObject implements ICRocmBreakpointContext {
	
	static {
		Platform.getAdapterManager().registerAdapters(new CBreakpointContextAdapterFactory(), CBreakpointContext.class);
	}
	
    /**
     * Breakpoint object held by this context.
     */
    private final ICBreakpoint fBreakpoint;
    
    /**
     * The resource that the breakpoint is to be created for.
     */
    private final IResource fResource;
    
    /**
     * The active debug context held by this context.
     */
    private final ISelection fDebugContext;
    
    /**
     * Associated preference store.
     */
    private final CRocmBreakpointPreferenceStore fPreferenceStore;
	
	public CRocmBreakpointContext(ICBreakpoint breakpoint, ISelection debugContext) {
        this (breakpoint, debugContext, null, null);
    }
	
	public CRocmBreakpointContext(ICBreakpoint breakpoint, ISelection debugContext, IResource resource, Map<String, Object> attributes) {
        fBreakpoint = breakpoint;
        fResource = resource;
        fDebugContext = debugContext;
        fPreferenceStore = new CRocmBreakpointPreferenceStore(this, attributes);
    }

	@Override
	public void addDebugContextListener(IDebugContextListener listener) {}

	@Override
	public void removeDebugContextListener(IDebugContextListener listener) {}
	
	@Override
	public IWorkbenchPart getPart() {return null; }

	@Override
	public ISelection getActiveContext() {
		return fDebugContext;
	}

	@Override
	public ICBreakpoint getBreakpoint() {
		return fBreakpoint;
	}

	@Override
	public IResource getResource() {
		return fResource;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return fPreferenceStore;
	}

}
