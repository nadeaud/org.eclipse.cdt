package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIHsailWavesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIHsailWaves extends MICommand<MIHsailWavesInfo> {
	
	public MIHsailWaves (IDMContext ctx, String[] ids) {
		super(ctx, "-hsail-waves-info"); //$NON-NLS-1$

		if (ids != null)
			setParameters(ids);
	}
	
	public MIHsailWavesInfo getResult(MIOutput out) {
		return new MIHsailWavesInfo(out);
	}

}
