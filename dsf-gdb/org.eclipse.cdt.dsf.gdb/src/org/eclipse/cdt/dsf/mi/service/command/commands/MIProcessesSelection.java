package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIProcessesSelectionInfo;

public class MIProcessesSelection extends MICommand<MIProcessesSelectionInfo>{

	public MIProcessesSelection(IDMContext ctx, String[] params) {
		super(ctx, "-processes-selection");
		
		setParameters(params);
	}

}
