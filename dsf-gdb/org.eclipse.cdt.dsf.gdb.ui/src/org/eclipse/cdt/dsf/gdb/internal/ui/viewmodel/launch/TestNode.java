package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.Immutable;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.AbstractDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.internal.ui.DsfUIPlugin;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.AbstractVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.ModelProxyInstalledEvent;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.IDMVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;

public class TestNode implements IVMNode, IElementLabelProvider, IElementPropertiesProvider {
	
	public class TestVMContext extends AbstractDMContext {
		
		protected String fId;
		
		public TestVMContext(String id) {
			super(DsfSession.getActiveSessions()[0], new IDMContext[0]);
			fId = id;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof TestVMContext) {
				return fId.equals(((TestVMContext)obj).fId);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return baseToString() + ".testNode." + fId;
		}

		@Override
		public int hashCode() {
			 return baseHashCode() ^ (fId == null ? 0 : fId.hashCode()); 
		}
		
	}
	
	@Immutable
    protected class DMVMContext extends AbstractVMContext implements IDMVMContext {
        private final IDMContext fDmc;
        
        public DMVMContext(IDMContext dmc) {
            super(TestNode.this);
            assert dmc != null;
            fDmc = dmc;
        }
        
        @Override
		public IDMContext getDMContext() { return fDmc; }
        
        /**
         * The IAdaptable implementation.  If the adapter is the DM context, 
         * return the context, otherwise delegate to IDMContext.getAdapter().
         */
        @SuppressWarnings("unchecked")
		@Override
        public <T> T getAdapter(Class<T> adapter) {
            T superAdapter = super.getAdapter(adapter);
            if (superAdapter != null) {
                return superAdapter;
            } else {
                // Delegate to the Data Model to find the context.
                if (adapter.isInstance(fDmc)) {
                    return (T)fDmc;
                } else {
                    return fDmc.getAdapter(adapter);
                }
            }
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TestNode.DMVMContext)) return false;
            DMVMContext otherVmc = (DMVMContext)other;
            return TestNode.this.equals(otherVmc.getVMNode()) &&
                   fDmc.equals(otherVmc.fDmc);
        }
        
        @Override
        public int hashCode() {
            return TestNode.this.hashCode() + fDmc.hashCode(); 
        }
     
        @Override
        public String toString() {
            return fDmc.toString();
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
			update.setHasChilren(false);
			update.done();
		}
		
	}

	@Override
	public void update(IPropertiesUpdate[] updates) {
		/* Behavior of ThreadVMNode :
		 * For each update, find the corresponding Context.
		 * Retrieve the execution data from dsf
		 * 		-
		 */
		
	}

	@Override
	public void update(ILabelUpdate[] updates) {
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
		//IDMContext dmc = event instanceof IDMEvent<?> ? ((IDMEvent<?>)event).getDMContext() : null;
		
		if(event instanceof ModelProxyInstalledEvent) {
		
		}
		parent.addNode(new DMVMContext(new TestVMContext("0")), IModelDelta.CONTENT | IModelDelta.SELECT | IModelDelta.REVEAL); //$NON-NLS-1$
		requestMonitor.done();
		
	}

	@SuppressWarnings("restriction")
	@Override
	public void getContextsForEvent(VMDelta parentDelta, Object event, DataRequestMonitor<IVMContext[]> rm) {
        rm.setStatus(new Status(IStatus.ERROR, DsfUIPlugin.PLUGIN_ID, IDsfStatusConstants.NOT_SUPPORTED, "", null)); //$NON-NLS-1$
        rm.done();
        /*
		if(event instanceof ModelProxyInstalledEvent) {
			TestVMContext context = new TestVMContext("0"); 
			rm.setData(new IDMContext[] { new DMVMContext(new TestVMContext("0")) });
		}
		rm.done();
		*/
	}

	@Override
	public void dispose() {
		fServiceTracker.dispose();
	}

}
