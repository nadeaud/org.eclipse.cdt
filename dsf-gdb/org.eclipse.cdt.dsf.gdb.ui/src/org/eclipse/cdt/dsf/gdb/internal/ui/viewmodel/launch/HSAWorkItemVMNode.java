package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IBreakpointsExtension.IBreakpointHitDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IMIHSAWaveExecutionContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IMIHSAWorkItemContext;
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

public class HSAWorkItemVMNode extends AbstractDMVMNode
	implements IElementLabelProvider, IElementPropertiesProvider{
	
	public final String HSA_WORKITEM_NODE_VM_ID_X = "WORKITEM.NODE.VM.X.ID"; //$NON-NLS-1$ "hsaworkgroupvmnode.property.workgroup.id.x"; //
	public final String HSA_WORKITEM_NODE_VM_ID_Y = "WORKITEM.NODE.VM.Y.ID"; //$NON-NLS-1$ "hsaworkgroupvmnode.property.workgroup.id.x"; //
	public final String HSA_WORKITEM_NODE_VM_ID_Z = "WORKITEM.NODE.VM.Z.ID"; //$NON-NLS-1$ "hsaworkgroupvmnode.property.workgroup.id.x"; //

	private IElementLabelProvider fLabelProvider;

	public HSAWorkItemVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IMIHSAWorkItemContext.class);
		fLabelProvider = createElementLabelProvider();
	}
	
	protected IElementLabelProvider createElementLabelProvider () {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		provider.setColumnInfo(
				PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS,
				new LabelColumnInfo(new LabelAttribute[] {
						new LabelText (
								"WI ({0},{1},{2})", //$NON-NLS-1$
								new String[] {HSA_WORKITEM_NODE_VM_ID_X, HSA_WORKITEM_NODE_VM_ID_Y, HSA_WORKITEM_NODE_VM_ID_Z}),
						new LabelText (
								"Unknown work-item", //$NON-NLS-1$
								new String[] { })
				}));
		return provider;
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

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		requestMonitor.done();
	}

	@Override
	public void update(IPropertiesUpdate[] updates) {
		for (final IPropertiesUpdate update : updates) {
			if (update.getElement() instanceof IDMVMContext) {
				IDMVMContext ctx = (IDMVMContext)update.getElement();
				if (ctx.getDMContext() instanceof IMIHSAWorkItemContext) {
					IMIHSAWorkItemContext wCtx = (IMIHSAWorkItemContext)ctx.getDMContext();
					update.setProperty(HSA_WORKITEM_NODE_VM_ID_X, wCtx.getAbsX());
					update.setProperty(HSA_WORKITEM_NODE_VM_ID_Y, wCtx.getAbsY());
					update.setProperty(HSA_WORKITEM_NODE_VM_ID_Z, wCtx.getAbsZ());
				}
			}
			update.done();
		}
	}

	@Override
	public void update(ILabelUpdate[] updates) {
		fLabelProvider.update(updates);
		
	}

	@Override
	protected void updateElementsInSessionThread(IChildrenUpdate update) {
		IProcesses procService = getServicesTracker().getService(IProcesses.class);
		org.eclipse.jface.viewers.TreePath path = update.getElementPath();
		Object viewer = update.getViewerInput();
		final IContainerDMContext contDmc = findDmcInPath(viewer, path, IContainerDMContext.class);
		final IMIHSAWaveExecutionContext hsaDmc = findDmcInPath(viewer, path, IMIHSAWaveExecutionContext.class);

		if (procService == null || contDmc == null) {
			handleFailedUpdate(update);
			return;
		}
		
		procService.getHSAWorkItems(contDmc, 
				hsaDmc,
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

}
