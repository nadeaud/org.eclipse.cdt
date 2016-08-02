package org.eclipse.cdt.debug.internal.ui.breakpoints;

import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.cdt.debug.core.model.ICRocmBreakpoint;
import org.eclipse.cdt.debug.internal.core.breakpoints.BreakpointMessages;
import org.eclipse.cdt.debug.ui.breakpoints.ICRocmBreakpointContext;
import org.eclipse.cdt.debug.ui.preferences.ReadOnlyFieldEditor;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPropertyPage;

public class CRocmBreakpointPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	private IAdaptable fElement;
	private CRocmBreakpointPreferenceStore fCRocmBreakpointPreferenceStore;
	
	class LabelFieldEditor extends ReadOnlyFieldEditor {
		private String fValue;

		public LabelFieldEditor( Composite parent, String title, String value ) {
			super(title, title, parent);
			fValue = value;
		}

		@Override
		protected void doLoad() {
			if (textField != null) {
				textField.setText(fValue);
			}
		}
		@Override
		protected void doLoadDefault() {
			// nothing
		}
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		IAdaptable element = getElement();
		if(element instanceof ICRocmBreakpointContext) {
			return ((ICRocmBreakpointContext) element).getPreferenceStore();
		}
		
		if(fCRocmBreakpointPreferenceStore == null) {
			CRocmBreakpointContext bpContext = element instanceof CRocmBreakpointContext ? (CRocmBreakpointContext)element : null;
			fCRocmBreakpointPreferenceStore = new CRocmBreakpointPreferenceStore(bpContext, null);
		}
		return fCRocmBreakpointPreferenceStore;
	}
	

	
	protected void createFileLineNumberEditor( Composite parent ) {
		
	}
	
	@Override
	public IAdaptable getElement() {
		return fElement;
	}

	@Override
	public void setElement(IAdaptable element) {
		fElement = element;		
	}

	protected ICBreakpoint getBreakpoint() {
		IAdaptable element = getElement();
		if (element instanceof ICBreakpoint) {
		    return (ICBreakpoint)element;
		} else if (element instanceof ICRocmBreakpointContext) {
		    return ((ICRocmBreakpointContext)element).getBreakpoint();
		} else {
		    return element.getAdapter(ICRocmBreakpoint.class);
		}
	}
	
	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		if (store instanceof CRocmBreakpointPreferenceStore) {
			((CRocmBreakpointPreferenceStore) store).setCanceled(false);
		}
		return super.performOk();
	}
	
	public CRocmBreakpointPropertyPage() {
		super( GRID);
		noDefaultAndApplyButton();
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub	
		ICBreakpoint breakpoint = getBreakpoint();
		createMainLabel();
	}
	
	protected FieldEditor createLabelEditor( Composite parent, String title, String value ) {
		return new LabelFieldEditor( parent, title, value );
	}
	
	private void createMainLabel() {
		ICBreakpoint breakpoint = getBreakpoint();
		addField ( createLabelEditor(
				getFieldEditorParent(),
				BreakpointMessages.getString("ROCmBreakpointPropertyPage.breakpointType_label"), //$NON-NLS-1$
				"ROCm") );
		addField( new IntegerFieldEditor("org.eclipse.rocm.lineNumber", "Line number", getFieldEditorParent()));
	}


}
