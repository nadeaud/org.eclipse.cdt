package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIHsailWorkItemsInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIHsailWorkItems extends MICommand<MIHsailWorkItemsInfo> {
	
	public MIHsailWorkItems (IDMContext ctx, String waveId) {
		super(ctx, "-hsail-work-item-list"); //$NON-NLS-1$
		
		if (waveId != null)
			setParameters(new String[] {waveId});
	}
	
	@Override
	public MIHsailWorkItemsInfo getResult(MIOutput out) {
		return new MIHsailWorkItemsInfo(out);
	}
}
