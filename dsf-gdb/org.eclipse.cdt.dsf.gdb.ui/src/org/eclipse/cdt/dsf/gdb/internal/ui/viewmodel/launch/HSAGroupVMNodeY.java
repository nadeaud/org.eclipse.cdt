package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IProcesses.IProcessDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcessDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;

public class HSAGroupVMNodeY extends HSAGroupVMNode {

	public HSAGroupVMNodeY(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {
		org.eclipse.jface.viewers.TreePath path = update.getElementPath();
		Object viewer = update.getViewerInput();
		final IContainerDMContext contDmc = findDmcInPath(viewer, path, IContainerDMContext.class);
		final IProcessDMContext procContext = findDmcInPath(viewer, path, IProcessDMContext.class);
		
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		ICommandControlService controlService = getServicesTracker().getService(ICommandControlService.class);
		if (processService == null || controlService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		if ( ! (contDmc instanceof IMIContainerDMContext)) {
			handleFailedUpdate(update);
			return;
		}
		
		if(procContext == null || !(procContext instanceof IMIProcessDMContext) || !((IMIProcessDMContext)procContext).getProcId().equals("hsa0")) //$NON-NLS-1$
			return;

		processService.getHSAWorkGroups(
				controlService.getContext(),
				contDmc,
				new ViewerDataRequestMonitor<IDMContext[]>(getExecutor(), update) {
					@Override
					public void handleCompleted() {
						if (!isSuccess()) {
							handleFailedUpdate(update);
							return;
						}
						if(getData() != null) 
							fillUpdateWithVMCs(update, getData());
						update.done();
					}
				});
		
	}

}
