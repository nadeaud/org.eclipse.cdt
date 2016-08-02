package org.eclipse.cdt.debug.ui.breakpoints;

import java.util.ArrayList;

/**
 * @since 8.0
 */
public class RocmBreakpointUIContributionFactory {
	
	private static final String EXTENSION_POINT_NAME = "BreakpointContribution"; //$NON-NLS-1$
	
	private static RocmBreakpointUIContributionFactory instance;
	protected ArrayList<ICBreakpointsUIContribution> contributions;
	
	private RocmBreakpointUIContributionFactory() {
		contributions = new ArrayList<ICBreakpointsUIContribution>();
	}
	

}
