package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;


import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMEvent;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.debug.service.IBreakpointsExtension.IBreakpointHitDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IStartedDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.ISuspendedDMEvent;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelAttribute;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelColumnInfo;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;

public class HSAWaveVMNode extends AbstractDMVMNode
	implements IElementLabelProvider
{
	private IElementLabelProvider fLabelProvider;

	public HSAWaveVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IRunControl.IExecutionDMContext.class);
		fLabelProvider = createElementLabelProvider();		
	}
	
	private IElementLabelProvider createElementLabelProvider () {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		provider.setColumnInfo(
				PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS,
				new LabelColumnInfo(new LabelAttribute[] {
						new LabelText (
								"HSA Wave",
								new String[] {  })
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
		IDMContext dmc = event instanceof IDMEvent<?> ? ( (IDMEvent<?>)event).getDMContext() : null;
		if (event instanceof IStartedDMEvent) {
			parent.addNode(createVMContext(dmc), IModelDelta.CONTENT | IModelDelta.EXPAND);
		}
		else if (event instanceof IBreakpointHitDMEvent || event instanceof ISuspendedDMEvent) {
			parent.addNode(createVMContext(dmc), IModelDelta.CONTENT | IModelDelta.EXPAND);
		}
		requestMonitor.done();		
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
