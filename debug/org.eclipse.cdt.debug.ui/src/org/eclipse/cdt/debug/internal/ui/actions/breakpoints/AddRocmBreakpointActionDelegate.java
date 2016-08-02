package org.eclipse.cdt.debug.internal.ui.actions.breakpoints;

import org.eclipse.cdt.debug.internal.ui.actions.ActionMessages;
import org.eclipse.cdt.debug.ui.CDebugUIPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

public class AddRocmBreakpointActionDelegate extends ActionDelegate implements IViewActionDelegate {

	private IViewPart fView;
	private ISelection fSelection;
	// Change to interface ?
	private ToggleRocmBreakpointAdapter fDefaultToggleTarget = new ToggleRocmBreakpointAdapter();
	
	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		setView( view);
	}
	
	private void setView(IViewPart view) {
		fView = view;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	    fSelection = selection;
	}
	
	@Override
	public void run( IAction action) {
		
		try {
			fDefaultToggleTarget.createLineBreakpointsInteractive(fView, fSelection);
		} catch(CoreException e)
		{
			CDebugUIPlugin.errorDialog( ActionMessages.getString( "AddRocmBreakpointActionDelegate.0"), e); //$NON-NLS-1$
		}
	}

}
