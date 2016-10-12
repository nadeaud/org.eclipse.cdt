package org.eclipse.cdt.dsf.mi.service.command.commands;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIHsailWaveListInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIHsailWaveList extends MICommand<MIHsailWaveListInfo>{

	public MIHsailWaveList(IDMContext ctx) {
		super(ctx, "-hsail-thread-info");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MIHsailWaveListInfo getResult(MIOutput output) {
		return new MIHsailWaveListInfo(output);
	}

}
