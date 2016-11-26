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
import org.eclipse.cdt.dsf.debug.service.IRunControl.IStartedDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.ISuspendedDMEvent;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
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

public class HSAGroupVMNode extends AbstractDMVMNode 
	implements IElementLabelProvider, IElementPropertiesProvider
{
	public final String HSA_WORKGROUP_X_ID = "hsaworkgroupvmnode.property.workgroup.id.x";
	
	private IElementLabelProvider fLabelProvider;

	public HSAGroupVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IRunControl.IContainerDMContext.class);
		fLabelProvider = createLabelProvider();
	}
	
	private IElementLabelProvider createLabelProvider () {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		provider.setColumnInfo(
				PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS,
				new LabelColumnInfo(new LabelAttribute[] {
						new LabelText (
								"HSA WorkGroup (,x,x)",
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
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		ICommandControlService controlService = getServicesTracker().getService(ICommandControlService.class);
		if (processService == null || controlService == null) {
			handleFailedUpdate(update);
			return;
		}
		
		processService.getHSAWorkGroups(
				controlService.getContext(),
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

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		IDMContext dmc = event instanceof IDMEvent<?> ? ( (IDMEvent<?>)event).getDMContext() : null;
		if (event instanceof IStartedDMEvent) {
			parent.addNode(createVMContext(dmc), IModelDelta.CONTENT);
		}
		else if (event instanceof IBreakpointHitDMEvent || event instanceof ISuspendedDMEvent) {
			parent.addNode(createVMContext(dmc), IModelDelta.CONTENT);
		}
		requestMonitor.done();
	}

	@Override
	public void update(ILabelUpdate[] updates) {
		fLabelProvider.update(updates);		
	}

	@Override
	public void update(IPropertiesUpdate[] updates) {
		try {
			getSession().getExecutor().execute(new DsfRunnable() {
				@Override
				public void run() {
					updatePropertiesInSessionThread(updates);
				}});
		} catch (RejectedExecutionException e) {
			for (IPropertiesUpdate update : updates) {
				handleFailedUpdate(update);
			}
		}
	}
	
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	public void updatePropertiesInSessionThread(final IPropertiesUpdate[] updates) {
		for (final IPropertiesUpdate update : updates) {
			if(update.getElement() instanceof IDMVMContext) {
				IDMVMContext context  = (IDMVMContext)update.getElement();
				if(context.getDMContext() instanceof IMIExecutionDMContext) {
					String wgID = ((IMIExecutionDMContext)context.getDMContext()).getThreadId();
					update.setProperty(HSA_WORKGROUP_X_ID, wgID);
				}
			}
			update.done();
		}
	}
}
