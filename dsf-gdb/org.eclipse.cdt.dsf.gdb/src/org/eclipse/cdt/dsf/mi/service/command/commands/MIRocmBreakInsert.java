package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IBreakpoints.IBreakpointsTargetDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIBreakInsertInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIRocmBreakInsertInfo;

/**
 * @since 5.1
 */
public class MIRocmBreakInsert extends MICommand<MIBreakInsertInfo> {

	public MIRocmBreakInsert(IDMContext ctx, String operation) {
		super(ctx, operation);
		// TODO Auto-generated constructor stub
	}
	
	public MIRocmBreakInsert(IBreakpointsTargetDMContext ctx, int lineNumber) {
		super(ctx, "-rocm-break-insert"); //$NON-NLS-1$
		
		String[] opts = new String[2];
		
		opts[0] = "-l"; //$NON-NLS-1$
		opts[1] = Integer.toString(lineNumber);
		
		setOptions(opts);
	}
	
	@Override public MIRocmBreakInsertInfo getResult(MIOutput output) {
		return new MIRocmBreakInsertInfo(output);
	}
	

}
