package org.eclipse.cdt.debug.ui.breakpoints;

import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.cdt.debug.internal.ui.breakpoints.CRocmBreakpointContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.debug.ui.contexts.IDebugContextProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * @since 8.0
 */
public class CRocmBreakpointPropertyDialogAction extends SelectionProviderAction {

	private IShellProvider fShellProvider;
	
	private IDebugContextProvider fDebugContextProvider;
	
	public static final String PAGE_ID_COMMON = "org.eclipse.cdt.debug.ui.propertypages.breakpoint.rocm"; //$NON-NLS-1$
	
	protected CRocmBreakpointPropertyDialogAction(IShellProvider shell, ISelectionProvider selectionProvider, IDebugContextProvider debugContextProvider) {
		super(selectionProvider, WorkbenchMessages.PropertyDialog_text);
		// TODO Auto-generated constructor stub
		Assert.isNotNull(shell);
		fDebugContextProvider = debugContextProvider;
		fShellProvider = shell;
		setToolTipText(WorkbenchMessages.PropertyDialog_toolTip);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IWorkbenchHelpContextIds.PROPERTY_DIALOG_ACTION);
	}	

	protected ISelection getDebugContext() {
		return fDebugContextProvider.getActiveContext();
	}
	
    public void run() {
        CRocmBreakpointContext bpContext = getCRocmBreakpointContext();
        if (bpContext != null) {
            PreferenceDialog dialog = createDialog(bpContext);
            
            if (dialog != null) {
                TreeViewer viewer = dialog.getTreeViewer();
                if (viewer != null) {
                    viewer.setComparator(new ViewerComparator() {
                        @Override
                        public int category(Object element) {
                            if (element instanceof IPreferenceNode) {
                                IPreferenceNode node = (IPreferenceNode)element;
                                if ( PAGE_ID_COMMON.equals(node.getId()) ) {
                                    return 0;
                                } else if (node.getSubNodes() == null || node.getSubNodes().length == 0) {
                                    // Pages without children (not categories)
                                    return super.category(element) + 1;
                                }
                            }
                            // Categories last.
                            return super.category(element) + 2;
                        }
                    });
                    // Expand all categories
                    viewer.expandToLevel(TreeViewer.ALL_LEVELS);
                }
                
                dialog.open();
            }
            
        }
    }
    
    private CRocmBreakpointContext getCRocmBreakpointContext() {
        IStructuredSelection ss = getStructuredSelection();
        if (ss.size() >= 1 && ss.getFirstElement() instanceof ICBreakpoint) {
            return new CRocmBreakpointContext((ICBreakpoint)ss.getFirstElement(), fDebugContextProvider.getActiveContext());
        }
        return null;
    }
    
    
    /**
     * Create the dialog for the receiver. If no pages are found, an informative
     * message dialog is presented instead.
     * 
     * @return PreferenceDialog or <code>null</code> if no applicable pages
     *         are found.
     */
   protected PreferenceDialog createDialog(CRocmBreakpointContext bpContext) {
       IStructuredSelection ss = getStructuredSelection();
       if (ss.isEmpty())
           return null;
       
       String initialPageId = null;
       if (bpContext.getBreakpoint().getMarker() == null) {
       	// Bug 433308 - Always show Common page initially for new breakpoints
       	initialPageId = PAGE_ID_COMMON;
       }
       return PreferencesUtil.createPropertyDialogOn(fShellProvider.getShell(), bpContext, initialPageId, null, null);
   }
}
