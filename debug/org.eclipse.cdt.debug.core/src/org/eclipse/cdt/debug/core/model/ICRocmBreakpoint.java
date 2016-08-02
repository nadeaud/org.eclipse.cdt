package org.eclipse.cdt.debug.core.model;

import org.eclipse.debug.core.model.ILineBreakpoint;

/**
 * @since 8.0
 */
public interface ICRocmBreakpoint extends ICBreakpoint, ILineBreakpoint {
	
	public static final String C_ROCM_BREAKPOINT_MARKER = "org.eclipse.cdt.debug.core.cLineBreakpointMarker"; //$NON-NLS-1$

}
