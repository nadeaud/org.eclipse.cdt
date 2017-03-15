package org.eclipse.cdt.dsf.mi.service.command.commands;

import java.util.ArrayList;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIHsailWaveGroupInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIHsailWaveGroup extends MICommand<MIHsailWaveGroupInfo> {

	public MIHsailWaveGroup(IDMContext ctx, String[] ids) {
		super(ctx, "-hsail-wave-group"); //$NON-NLS-1$
		
		final ArrayList<String> arguments = new ArrayList<String>();
		if (ids != null )
		{
			for(String id : ids)
				arguments.add(id);
		}
		
		setParameters(arguments.toArray(new String[0]));
	}
	
	public MIHsailWaveGroupInfo getResult(MIOutput out) {
		return new MIHsailWaveGroupInfo(out);
	}

}
