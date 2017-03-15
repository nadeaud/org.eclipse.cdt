package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.mi.service.IMIHSAContainerDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;

public class HSAGroupVMNodeY extends HSAGroupVMNode {

	public HSAGroupVMNodeY(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session);
	}
	
	@Override
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {
		org.eclipse.jface.viewers.TreePath path = update.getElementPath();
		Object viewer = update.getViewerInput();
		final IMIHSAContainerDMContext contDmc = findDmcInPath(viewer, path, IMIHSAContainerDMContext.class);
		
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		ICommandControlService controlService = getServicesTracker().getService(ICommandControlService.class);
		
		if (processService == null || controlService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		processService.getHSAWorkGroups(controlService.getContext(), 
				"y", //$NON-NLS-1$
				contDmc,
				new ViewerDataRequestMonitor<IDMContext[]>(getExecutor(), update) {
					@Override
					public void handleCompleted() {
						if(!isSuccess()) {
							handleFailedUpdate(update);
							return;
						}
						
						if(getData() != null)
							fillUpdateWithVMCs(update, getData());
						update.done();
					}
				});
	}

	@Override
	public void buildHSAWorkGroupDelta(IDMContext dmc, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		IProcesses procService = getServicesTracker() == null ? null : getServicesTracker().getService(IProcesses.class);
		
		if(procService == null) {
			requestMonitor.done();
			return;
		}
		requestMonitor.done();	}
}
