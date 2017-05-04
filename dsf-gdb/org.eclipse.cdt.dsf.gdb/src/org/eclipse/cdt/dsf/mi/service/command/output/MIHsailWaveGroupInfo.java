package org.eclipse.cdt.dsf.mi.service.command.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class MIHsailWaveGroupInfo extends MIInfo{

	public class WorkGroupRange {
		public String id;
		public String min;
		public String max;
	}
	
	private List<WorkGroupRange> fWGRange = null;
	HashSet<String> fSet = new HashSet<String>();

	public MIHsailWaveGroupInfo(MIOutput record) {
		super(record);
		parse();
	}

	public List<WorkGroupRange> getWorkGroups() {
		return fWGRange;
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
					if (var.equals("workgroups")) { //$NON-NLS-1$
						MIList list = (MIList)results[i].getMIValue();
						fWGRange = new ArrayList<WorkGroupRange>(list.getMIValues().length);
						for(int j = 0; j < list.getMIValues().length; j++) {
							MITuple tuple = (MITuple)list.getMIValues()[j];
							parse_entry(tuple);
						}
						if(fWGRange.size() > 0) {
							Collections.sort(fWGRange, new Comparator<WorkGroupRange>() {
								@Override
								public int compare(final WorkGroupRange obj1, final WorkGroupRange obj2) {
									return obj1.id.compareTo(obj2.id);
								}
							});
						}
					}
				}
			}
		}
	}
	
	protected void parse_entry (MITuple tuple) {
		if( fSet.contains(tuple.getField("id").toString()) ) //$NON-NLS-1$
			return;
		
		fSet.add(tuple.getField("id").toString()); //$NON-NLS-1$
		
		WorkGroupRange wgr = new WorkGroupRange();
		wgr.id = tuple.getField("id").toString(); //$NON-NLS-1$
		wgr.min = tuple.getField("min").toString(); //$NON-NLS-1$
		wgr.max = tuple.getField("max").toString(); //$NON-NLS-1$
		fWGRange.add(wgr);
	}
}

