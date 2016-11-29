package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;


import java.util.concurrent.RejectedExecutionException;

import org.eclipse.cdt.dsf.concurrent.ConfinedToDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMEvent;
import org.eclipse.cdt.dsf.debug.service.IBreakpointsExtension.IBreakpointHitDMEvent;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IHSAWaveExecutionContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IStartedDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.ISuspendedDMEvent;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.IDMVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelAttribute;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelColumnInfo;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;

public class HSAWaveVMNode extends AbstractDMVMNode
	implements IElementLabelProvider, IElementPropertiesProvider
{
	private IElementLabelProvider fLabelProvider;
	
	public final String HSA_WAVE_NODE_VM_ID_X = "WAVE.NODE.VM.X.ID"; //$NON-NLS-1$ "hsaworkgroupvmnode.property.workgroup.id.x"; //
	public final String HSA_WAVE_NODE_VM_ID_Y = "WAVE.NODE.VM.Y.ID"; //$NON-NLS-1$
	public final String HSA_WAVE_NODE_VM_ID_Z = "WAVE.NODE.VM.Z.ID"; //$NON-NLS-1$

	public HSAWaveVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IRunControl.IHSAWaveExecutionContext.class);
		fLabelProvider = createElementLabelProvider();		
	}
	
	private IElementLabelProvider createElementLabelProvider () {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		provider.setColumnInfo(
				PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS,
				new LabelColumnInfo(new LabelAttribute[] {
						new LabelText (
								"wave ({0},{1},{2})", //$NON-NLS-1$
								new String[] { HSA_WAVE_NODE_VM_ID_X, HSA_WAVE_NODE_VM_ID_Y, HSA_WAVE_NODE_VM_ID_Z})/*,
						new LabelText (
								"Unknown wave",
								new String[] { })*/
				}));
		return provider;
	}
	
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	public void updatePropertiesInSessionThread(final IPropertiesUpdate[] updates) {
		for (final IPropertiesUpdate update : updates) {
			if(update.getElement() instanceof IDMVMContext) {
				IDMVMContext context  = (IDMVMContext)update.getElement();
				if(context.getDMContext() instanceof IHSAWaveExecutionContext) {
					IHSAWaveExecutionContext ctx = (IHSAWaveExecutionContext)context.getDMContext();
					String xId = ctx.getX();
					update.setProperty(HSA_WAVE_NODE_VM_ID_X, ctx.getX());
					update.setProperty(HSA_WAVE_NODE_VM_ID_Y, ctx.getY());
					update.setProperty(HSA_WAVE_NODE_VM_ID_Z, ctx.getZ());
				}
			}
			update.done();
		}
	}
	
	@Override
	public void update(IPropertiesUpdate[] updates) {
		try {
			getSession().getExecutor().execute(new DsfRunnable() {
				@Override
				public void run() {
					updatePropertiesInSessionThread(updates);					
				}
			});
		} catch (RejectedExecutionException e) {
			for (IPropertiesUpdate update : updates) {
				handleFailedUpdate(update);
			}
		}
	}

	@Override
	public int getDeltaFlags(Object event) {
		if (event instanceof IStartedDMEvent) {
			return IModelDelta.CONTENT;
		}
		else if (event instanceof IBreakpointHitDMEvent || event instanceof ISuspendedDMEvent) {
			return IModelDelta.CONTENT;
		}
		return IModelDelta.NO_CHANGE;
	}

	
	public void buildHSAWaveDelta (IDMContext dmc, VMDelta parent, int nodeOffSet, RequestMonitor rm) {
		IProcesses procService = getServicesTracker() == null ? null : getServicesTracker().getService(IProcesses.class);
		
		if(procService == null) {
			rm.done();
			return;
		}
		parent.addNode(procService.getHSAWaveFocus(dmc), IModelDelta.CONTENT);
		rm.done();
	}
	
	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		IDMContext dmc = event instanceof IDMEvent<?> ? ( (IDMEvent<?>)event).getDMContext() : null;
		
		if (event instanceof IBreakpointHitDMEvent || event instanceof ISuspendedDMEvent) {
			getSession().getExecutor().execute(new DsfRunnable() {
				@Override
				public void run() {
					buildHSAWaveDelta(dmc, parent, nodeOffset,requestMonitor);					
				}
			});
		} else {
			requestMonitor.done();
		}
	}

	@Override
	protected void updateElementsInSessionThread(IChildrenUpdate update) {
		IProcesses procService = getServicesTracker().getService(IProcesses.class);
		org.eclipse.jface.viewers.TreePath path = update.getElementPath();
		Object viewer = update.getViewerInput();
		final IContainerDMContext contDmc = findDmcInPath(viewer, path, IContainerDMContext.class);
		
		if (procService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		procService.getHSAWaveForParent(contDmc, 
				new ViewerDataRequestMonitor<IDMContext[]>(getSession().getExecutor(), update) {
					@Override
					public void handleCompleted() { 
						if(!isSuccess()) {
							handleFailedUpdate(update);
							return;
						}
						IDMContext[] contexts = getData();
						fillUpdateWithVMCs(update, contexts);
						update.done();
					}
				});
		
	}

	@Override
	public void update(ILabelUpdate[] updates) {
		fLabelProvider.update(updates);
	}

}
