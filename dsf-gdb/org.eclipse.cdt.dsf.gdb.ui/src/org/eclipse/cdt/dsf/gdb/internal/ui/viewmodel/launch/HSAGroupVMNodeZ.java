package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_0;
import org.eclipse.cdt.dsf.mi.service.IMIHSAContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcessDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;

public class HSAGroupVMNodeZ extends HSAGroupVMNode {

	public HSAGroupVMNodeZ(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session);
	}

	@Override
	public void buildHSAWorkGroupDelta (IDMContext dmc, VMDelta parent, int nodeOffSet, RequestMonitor rm) {
		IProcesses procService = getServicesTracker() == null ? null : getServicesTracker().getService(IProcesses.class);

		if(procService == null || !(procService instanceof GDBProcesses_7_0)) {
			rm.done();
			return;
		}
		rm.done();
	}
	
	@Override
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {
		org.eclipse.jface.viewers.TreePath path = update.getElementPath();
		Object viewer = update.getViewerInput();
		final IMIHSAContainerDMContext contDmc = findDmcInPath(viewer, path, IMIHSAContainerDMContext.class);
		final IMIProcessDMContext processCont = findDmcInPath(viewer, path, IMIProcessDMContext.class);
		
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		ICommandControlService controlService = getServicesTracker().getService(ICommandControlService.class);
		if (processService == null || controlService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		processService.getHSAWorkGroups(controlService.getContext(),
				"z", //$NON-NLS-1$
				contDmc, 
				new ViewerDataRequestMonitor<IDMContext[]>(getExecutor(), update) {
					@Override
					public void handleCompleted() {
						if(!isSuccess()) {
							handleFailedUpdate(update);
							return;
						}
						if(getData() != null && getData().length != 0)
							fillUpdateWithVMCs(update, getData());
						
						if(getData() == null || getData().length == 0) {
							IMIHSAContainerDMContext emptyCont = 
									((GDBProcesses_7_0)processService).createHSAContainerContext(
											processCont, 
											contDmc, 
											"-1",  //$NON-NLS-1$
											"z", //$NON-NLS-1$
											"0", //$NON-NLS-1$
											"0"); //$NON-NLS-1$
							fillUpdateWithVMCs(update, new IDMContext[]{emptyCont});
						}
						
						update.done();
					}
				});
	}
}
