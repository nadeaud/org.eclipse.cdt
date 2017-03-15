package org.eclipse.cdt.dsf.mi.service.command.output;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MIHsailWaveGroupInfo extends MIInfo{

	private List<String> fIds = null;

	private String[] fUniqueIds = null;

	public MIHsailWaveGroupInfo(MIOutput record) {
		super(record);
		// TODO Auto-generated constructor stub
		parse();
	}

	public String[] getWorkGroups() {
		return fUniqueIds;
	}

	protected void parse() {
		if(isDone()) {
			MIOutput output = getMIOutput();
			MIResultRecord rr = output.getMIResultRecord();

			if( rr != null) {
				MIResult[] results = rr.getMIResults();
				if (results.length == 0)
					return;

				for (int i = 0; i < results.length; i++) {
					String var = results[i].getVariable();
					if (var.equals("workgroups")) {
						MIList list = (MIList)results[i].getMIValue();
						fIds = new ArrayList<String>(list.getMIValues().length);
						for(int j = 0; j < list.getMIValues().length; j++) {
							MIValue val = list.getMIValues()[j];
							fIds.add(((MITuple)val).getField("id").toString());
						}
						HashSet<String> set = new HashSet<>(fIds);
						fUniqueIds = set.toArray(new String[0]);							
					}
				}
			}

		}
	}
}

