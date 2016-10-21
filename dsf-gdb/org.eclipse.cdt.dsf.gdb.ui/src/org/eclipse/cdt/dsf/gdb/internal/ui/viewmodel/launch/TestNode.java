package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMEvent;
import org.eclipse.cdt.dsf.internal.ui.DsfUIPlugin;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMAdapter;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.ModelProxyInstalledEvent;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;

public class TestNode implements IVMNode, IElementLabelProvider, IElementPropertiesProvider {
	
	public class TestVMContext implements IVMContext {
		protected final IVMNode fNode;
		private IDMContext fContext;

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			// TODO Auto-generated method stub
			if(adapter.isInstance(this)) {
				return (T)this;
			}
			IVMProvider provider = getVMNode().getVMProvider();
			IVMAdapter vmAdapter = provider.getVMAdapter();
			if(adapter.isInstance(vmAdapter)) {
				return (T)vmAdapter;
			}else if (adapter.isInstance(provider)) {
				return (T)provider;
			}else if (adapter.isInstance(getVMNode())) {
				return (T)getVMNode();
			}
			return Platform.getAdapterManager().getAdapter(this, adapter);
		}

		@Override
		public IVMNode getVMNode() {
			// TODO Auto-generated method stub
			return fNode;
		}
		public TestVMContext(IVMNode node, IDMContext context) {
			fNode = node;
			fContext = context;
		}
	}
	
	private DsfSession fSession;
	private AbstractDMVMProvider fProvider;
	private DsfServicesTracker fServiceTracker;
	private IElementLabelProvider fLabelProvider;

	public TestNode(AbstractDMVMProvider provider, DsfSession session)
	{
		fProvider = provider;
		fSession = session;
		fServiceTracker = new DsfServicesTracker(DsfUIPlugin.getBundleContext(), session.getId());
		fLabelProvider = createLabelProvider();
	}
	
	private IElementLabelProvider createLabelProvider() {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();
		
		return provider;
	}

	@Override
	public void update(IChildrenCountUpdate[] updates) {
		/* Behavior of ThreadVMNode :
		 * for updates :
		 * 		for update.elements :
		 * 			if element instanceof IDMVMContext 
		 * 				if context.session alive
		 * 					continue
		 * 				else
		 * 					update is invalid, cancel
		 * tldr : check if the contexts' session is alive for every update. If not, cancel update
		 */
		for(IChildrenCountUpdate update : updates) {
			update.done();
		}
		
	}

	@Override
	public void update(IChildrenUpdate[] updates) {
		/* Behavior of ThreadVMNode :
		 * check if the session if alive for every IDMVMContext in updates. If not, call updateElementsInSessionThread
		 * This function call getProcessesBeingDebugged
		 *		-retourne un IDMContext par thread, utilisé pour créé un VMContext par thread et l'ajouter à l'update
		 */
		for(IChildrenUpdate update : updates) {
			update.done();
		}
		
	}

	@Override
	public void update(IHasChildrenUpdate[] updates) {
		/* Behavior of ThreadVMNode :
		 * fail update if there is a DMCMContext not alive in one of the updates' elements
		 * Called by TreeModelProvider
		 */
		for(IHasChildrenUpdate update : updates) {
			update.done();
		}
		
	}

	@Override
	public void update(IPropertiesUpdate[] updates) {
		// TODO Auto-generated method stub
		/* Behavior of ThreadVMNode :
		 * For each update, find the corresponding Context.
		 * Retrieve the execution data from dsf
		 * 		-
		 */
		
	}

	@Override
	public void update(ILabelUpdate[] updates) {
		// TODO Auto-generated method stub
		// Call fLabelProvider's update (PropertiesBasedLabelProvider
		fLabelProvider.update(updates);
	}

	@Override
	public IVMProvider getVMProvider() {
		return fProvider;
	}

	@Override
	public int getDeltaFlags(Object event) {
		
		// Indicate if the Node needs to create a delta for the event
		if (event instanceof ModelProxyInstalledEvent) {
			return IModelDelta.CONTENT;
		}
		return IModelDelta.NO_CHANGE;
	}

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {
		// Generates delta from events
		IDMContext dmc = event instanceof IDMEvent<?> ? ((IDMEvent<?>)event).getDMContext() : null;
		
		if(event instanceof ModelProxyInstalledEvent) {
		
			parent.addNode(new TestVMContext(TestNode.this, dmc), IModelDelta.CONTENT);
		}
		requestMonitor.done();
		
	}

	@Override
	public void getContextsForEvent(VMDelta parentDelta, Object event, DataRequestMonitor<IVMContext[]> rm) {
		// TODO Auto-generated method stub
		if(event instanceof ModelProxyInstalledEvent) {
			TestVMContext context = new TestVMContext(this, null); 
			rm.setData(new IVMContext[] { context });
		}
		rm.done();
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stud
		fServiceTracker.dispose();
	}

}
