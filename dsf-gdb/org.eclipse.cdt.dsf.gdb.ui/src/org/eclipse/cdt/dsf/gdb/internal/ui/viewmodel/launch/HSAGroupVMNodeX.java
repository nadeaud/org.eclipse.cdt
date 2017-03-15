package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_0;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;

public class HSAGroupVMNodeX extends HSAGroupVMNode {

	public final String HSA_WG_X_ID = "hsagroupvmnodex.property.workgroup.id.x"; //$NON-NLS-1$

	public HSAGroupVMNodeX(AbstractDMVMProvider provider, DsfSession session) {
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
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		ICommandControlService controlService = getServicesTracker().getService(ICommandControlService.class);
		
		if(controlService == null || processService == null || !(processService instanceof GDBProcesses_7_0)) {
			handleFailedUpdate(update);
			return;
		}
		
		processService.getHSAWorkGroups(controlService.getContext(),
				"x", //$NON-NLS-1$
				null, 
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
}
