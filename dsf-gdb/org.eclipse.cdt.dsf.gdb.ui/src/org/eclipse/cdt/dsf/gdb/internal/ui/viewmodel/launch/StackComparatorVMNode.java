package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.AbstractDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerSuspendedDMEvent;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;

public class StackComparatorVMNode extends AbstractDMVMNode implements IElementLabelProvider {

	public class StackDMContext extends AbstractDMContext {
		
		protected String fId;

		public StackDMContext(DsfSession session, IDMContext[] parents) {
			super(session, parents);
			fId = ""; //$NON-NLS-1$
		}
		
		public StackDMContext(DsfSession session, String Id) {
			super(session, new IDMContext[0]);
			fId = Id;
		}
		
		public String getId() {
			return fId;
		}

		@Override
		public boolean equals(Object obj) {
			
			if (obj instanceof StackComparatorVMNode) {
				return fId.equals(((StackDMContext)obj).getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return baseHashCode() ^ (fId == null ? 0 : fId.hashCode());
		}

	}
	
	public StackComparatorVMNode(AbstractDMVMProvider provider, DsfSession session,
			Class<? extends IDMContext> dmcClassType) {
		super(provider, session, dmcClassType);
		// TODO Auto-generated constructor stub
	}
	
	public void mergeStack() {
		
	}
	
	public void getCallStacks() {
		IProcesses processService = getServicesTracker().getService(IProcesses.class);
		
		
	}
	
	@Override
    protected void updateHasElementsInSessionThread(IHasChildrenUpdate update) {
		
	}

	@Override
	public int getDeltaFlags(Object event) {
		
		if (event instanceof IContainerSuspendedDMEvent) {
			return IModelDelta.CONTENT | IModelDelta.EXPAND;
		}
		return IModelDelta.NO_CHANGE;
	}

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		
		if (event instanceof IContainerSuspendedDMEvent) {
			parent.setFlags(parent.getFlags() | IModelDelta.EXPAND);
			//Object obj =
		}
		
	}

	@Override
	public void update(ILabelUpdate[] updates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateElementsInSessionThread(IChildrenUpdate update) {
		// TODO Auto-generated method stub
		
	}

}
