package org.eclipse.cdt.dsf.mi.service.command.output;

import java.util.ArrayList;
import java.util.List;

public class MIHsailWorkItemsInfo extends MIInfo {

	public class HsailWorkItem {
		public String xId;
		public String yId;
		public String zId;
		public String absXId;
		public String absYId;
		public String absZId;
	}
	
	private List<HsailWorkItem> fWorkItems = new ArrayList<>();
	
	public MIHsailWorkItemsInfo(MIOutput record) {
		super(record);
		parse();
	}
	
	public List<HsailWorkItem> getItems () {
		return fWorkItems;
	}
	
	protected void parse () {
		if (isDone()) {
			MIOutput output = getMIOutput();
			MIResultRecord rr = output.getMIResultRecord();
			
			if( rr != null) {
				MIResult[] results = rr.getMIResults();
				if(results.length == 0)
					return;
				
				if(results[0].value instanceof MIList) {
					for(int i = 0; i < ((MIList)results[0].value).getMIValues().length; i++) {
						MITuple tuple = (MITuple)((MIList)results[0].value).getMIValues()[i];
						processWorkItem(tuple);
					}
				}				
			}
		}
	}
	
	protected void processWorkItem(MITuple tuple) {
		HsailWorkItem data = new HsailWorkItem();
		MIResult[]  results = tuple.getMIResults();
		for(int i = 0; i < results.length; i++) {
			if(results[i].getVariable().equals("xId")) { //$NON-NLS-1$
				data.xId = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("yId")) { //$NON-NLS-1$
				data.yId = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("zId")) { //$NON-NLS-1$
				data.zId = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("absXId")) { //$NON-NLS-1$
				data.absXId = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("absYId")) { //$NON-NLS-1$
				data.absYId = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("absZId")) { //$NON-NLS-1$
				data.absZId = results[i].getMIValue().toString();
			}
		}
		fWorkItems.add(data);
	}
}
