package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;

public class MIAddFilterThread extends MICommand<MIInfo>{

	public MIAddFilterThread(IDMContext ctx, String id) {
		super(ctx, "-processes-selection"); //$NON-NLS-1$
		
		String params[] = new String[2];
		params[0] = "-e"; //$NON-NLS-1$
		params[1] = id;
		
		setParameters(params);
	}
	
	public MIAddFilterThread(IDMContext ctx, String[] ids) {
		super(ctx, "-processes-selection"); //$NON-NLS-1$
		
		String[] array = new String[ids.length*2];
		int i = 0;
		
		for(String id : ids) {
			array[i++] = "-e"; //$NON-NLS-1$
			array[i++] = id;
		}
		
		setParameters(array);
	}

}
