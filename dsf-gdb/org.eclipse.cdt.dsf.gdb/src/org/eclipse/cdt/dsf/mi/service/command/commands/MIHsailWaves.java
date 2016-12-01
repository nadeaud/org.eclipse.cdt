package org.eclipse.cdt.dsf.mi.service.command.commands;

import java.util.ArrayList;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIHsailWavesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIHsailWaves extends MICommand<MIHsailWavesInfo>{
	
	public MIHsailWaves (IDMContext ctx, String id) {
		super(ctx, "-hsail-waves-info"); //$NON-NLS-1$
		
		final ArrayList<String> args = new ArrayList<String>();
		if(id != null)
			args.add(id);
		
		setParameters(args.toArray(new String[0]));
	}
	
	public MIHsailWaves (IDMContext ctx) {
		super(ctx, "-hsail-waves-info"); //$NON-NLS-1$
	}
	
	@Override
	public MIHsailWavesInfo getResult(MIOutput out) {
		return new MIHsailWavesInfo(out);
	}
}
