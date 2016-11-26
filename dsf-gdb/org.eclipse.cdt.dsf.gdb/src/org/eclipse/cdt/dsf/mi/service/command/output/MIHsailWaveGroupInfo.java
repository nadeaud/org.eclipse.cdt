package org.eclipse.cdt.dsf.mi.service.command.output;

public class MIHsailWaveGroupInfo extends MIInfo{
	
	private String[] fWorkGroupIds = null;

	public MIHsailWaveGroupInfo(MIOutput record) {
		super(record);
		// TODO Auto-generated constructor stub
		parse();
	}
	
	public String[] getWorkGroups() {
		return fWorkGroupIds;
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
						fWorkGroupIds = new String[list.getMIValues().length];
						for(int j = 0; j < list.getMIValues().length; j++) {
							MIValue val = list.getMIValues()[j];
							fWorkGroupIds[j] = ((MITuple)val).getField("id").toString();
						}
					}
					
				}

			}
		}
	}

}
