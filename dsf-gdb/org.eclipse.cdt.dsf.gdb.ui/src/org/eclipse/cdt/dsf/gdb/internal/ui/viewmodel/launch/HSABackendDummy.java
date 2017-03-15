package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses.IProcessDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService.ICommandControlDMContext;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_0;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;

public class HSABackendDummy {

	private static HSABackendDummy instance;
	
	private HSABackendDummy () {
		
	}
	
	public static HSABackendDummy getInstance () {
		if (instance == null)
		{
			synchronized (HSABackendDummy.class) {
				if (instance == null) {
					instance = new HSABackendDummy();
				}				
			}
		}
		return instance;
	}
	
	public IDMContext buildDelta (IDMContext dmc, GDBProcesses_7_0 procService) {		
		final ICommandControlDMContext controlDmc = DMContexts.getAncestorOfType(dmc, ICommandControlDMContext.class);
		IProcessDMContext processDmc = procService.createProcessContext(controlDmc, "hsa0");
		return procService.createContainerContext(processDmc, "x-0");
	}
	
	public void getHSAWorkGroups (GDBProcesses_7_0 procService, IDMContext dmc, DataRequestMonitor<IDMContext []> rm) {
		final ICommandControlDMContext controlDmc = DMContexts.getAncestorOfType(dmc, ICommandControlDMContext.class);

		IProcessDMContext processDmc = procService.createProcessContext(controlDmc, "hsa0");
		
		IMIContainerDMContext[] context = new IMIContainerDMContext[5];
		
		for (int i = 0; i < 5; i++) {
			context[i] = procService.createContainerContext(processDmc, "x-" + Integer.toString(i));
		}
		rm.setData(context);
		rm.done();
		return;
	}
}
