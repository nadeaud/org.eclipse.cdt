package org.eclipse.cdt.debug.internal.ui.actions.breakpoints;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDIDebugModel;
import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.cdt.debug.core.model.ICLineBreakpoint;
import org.eclipse.cdt.debug.internal.ui.IInternalCDebugUIConstants;
import org.eclipse.cdt.debug.internal.ui.breakpoints.CRocmBreakpointContext;
import org.eclipse.cdt.debug.ui.CDebugUIPlugin;
import org.eclipse.cdt.debug.ui.breakpoints.CRocmBreakpointPropertyDialogAction;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ToggleRocmBreakpointAdapter extends ToggleBreakpointAdapter{

    protected void openBreakpointPropertiesDialog(ICBreakpoint bp, IWorkbenchPart part, IResource resource,
            Map<String, Object> attributes) {
            ISelection debugContext = DebugUITools.getDebugContextManager()
                .getContextService(part.getSite().getWorkbenchWindow()).getActiveContext(part.getSite().getId());
            CRocmBreakpointContext bpContext = new CRocmBreakpointContext(bp, debugContext, resource, attributes);

            String initialPageId = null;
            if (bp.getMarker() == null) {
            	// Bug 433308 - Always show Common page initially for new breakpoints
            	initialPageId = CRocmBreakpointPropertyDialogAction.PAGE_ID_COMMON;
            }
    		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(part.getSite().getShell(), bpContext, initialPageId ,
                null, null);
            if (dialog != null) {
                dialog.open();
            }
        }

	@Override
	protected void createLineBreakpoint(boolean interactive, IWorkbenchPart part, String sourceHandle,
			IResource resource, int lineNumber) throws CoreException {
		// TODO Auto-generated method stub
		
	    if (interactive) {
	        ICLineBreakpoint lineBp = CDIDebugModel.createBlankLineBreakpoint();
	        Map<String, Object> attributes = new HashMap<String, Object>();
	        CDIDebugModel.setLineBreakpointAttributes(
	            attributes, sourceHandle, getBreakpointType(), lineNumber, true, 0, "" ); //$NON-NLS-1$
	        openBreakpointPropertiesDialog(lineBp, part, resource, attributes);
	    } else {
	        CDIDebugModel.createLineBreakpoint( sourceHandle, resource, getBreakpointType(), lineNumber, true, 0, "", true );//$NON-NLS-1$
	    }
	}

	private void updateRocmBreakpoints(boolean toggle, boolean interactive, IWorkbenchPart part, ISelection selection)
		throws CoreException {
		String errorMessage = null;
		
		if(interactive && !toggle) {
			createLineBreakpoint(true, part, null, ResourcesPlugin.getWorkspace().getRoot(), -1);
			return;
		}
		
		throw new CoreException(new Status(IStatus.ERROR, CDebugUIPlugin.getUniqueIdentifier(), 
				IInternalCDebugUIConstants.INTERNAL_ERROR, errorMessage, null));
	}
	
	/* This is not really a LineBreakpoint, it creates a ROCm breakpoint, it should maybe be renamed. */
	@Override
	public void createLineBreakpointsInteractive(IWorkbenchPart part, ISelection selection) throws CoreException {
	    updateRocmBreakpoints(false, true, part, selection);
	}
}
