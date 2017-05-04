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
import org.eclipse.cdt.dsf.debug.service.IRunControl.IMIHSAWaveExecutionContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IStartedDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.ISuspendedDMEvent;
import org.eclipse.cdt.dsf.mi.service.IMIHSAContainerDMContext;
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
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelImage;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;

public class HSAWaveVMNode extends AbstractDMVMNode
	implements IElementLabelProvider, IElementPropertiesProvider
{
	private IElementLabelProvider fLabelProvider;
	
	public final String HSA_WAVE_NODE_VM_ID_X = "WAVE.NODE.VM.X.ID"; //$NON-NLS-1$ "hsaworkgroupvmnode.property.workgroup.id.x"; //
	public final String HSA_WAVE_NODE_VM_ID_Y = "WAVE.NODE.VM.Y.ID"; //$NON-NLS-1$
	public final String HSA_WAVE_NODE_VM_ID_Z = "WAVE.NODE.VM.Z.ID"; //$NON-NLS-1$
	public final String HSA_WAVE_NODE_VM_ID = "WAVE.NODE.VM.WAVE.ID"; //$NON-NLS-1$
	public final String HSA_WAVE_NODE_VM_PC = "WAVE.NODE.VM.WAVE.PC"; //$NON-NLS-1$

	public HSAWaveVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IRunControl.IMIHSAWaveExecutionContext.class);
		fLabelProvider = createElementLabelProvider();		
	}
	
	private IElementLabelProvider createElementLabelProvider () {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		provider.setColumnInfo(
				PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS,
				new LabelColumnInfo(new LabelAttribute[] {
						new LabelText (
								"Wave {0} at PC={1} in Work-Group ({2},{3},{4})", //$NON-NLS-1$
								new String[] {HSA_WAVE_NODE_VM_ID, HSA_WAVE_NODE_VM_PC,
										HSA_WAVE_NODE_VM_ID_X, HSA_WAVE_NODE_VM_ID_Y, 
										HSA_WAVE_NODE_VM_ID_Z}),
						new LabelText (
								"Unknown wave", //$NON-NLS-1$
								new String[] { }
								),
						 new LabelImage(DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_SUSPENDED))
				}));
		return provider;
	}
	
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	public void updatePropertiesInSessionThread(final IPropertiesUpdate[] updates) {
		for (final IPropertiesUpdate update : updates) {
			if(update.getElement() instanceof IDMVMContext) {
				IDMVMContext context  = (IDMVMContext)update.getElement();
				if(context.getDMContext() instanceof IMIHSAWaveExecutionContext) {
					IMIHSAWaveExecutionContext ctx = (IMIHSAWaveExecutionContext)context.getDMContext();
					update.setProperty(HSA_WAVE_NODE_VM_ID_X, ctx.getX());
					update.setProperty(HSA_WAVE_NODE_VM_ID_Y, ctx.getY());
					update.setProperty(HSA_WAVE_NODE_VM_ID_Z, ctx.getZ());
					update.setProperty(HSA_WAVE_NODE_VM_ID, ctx.getId());
					update.setProperty(HSA_WAVE_NODE_VM_PC, ctx.getPC());
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
		final IMIHSAContainerDMContext hsaDmc = findDmcInPath(viewer, path, IMIHSAContainerDMContext.class);

		if (procService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		procService.getHSAWaveForParent(contDmc, 
				hsaDmc,
				new ViewerDataRequestMonitor<IDMContext[]>(getSession().getExecutor(), update) {
					@Override
					public void handleCompleted() { 
						if(!isSuccess()) {
							handleFailedUpdate(update);
							return;
						}
						IDMContext[] contexts = getData();
						if(contexts == null) {
							handleFailedUpdate(update);
							return;
						}
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
