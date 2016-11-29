package org.eclipse.cdt.dsf.mi.service.command.output;

import java.util.ArrayList;
import java.util.List;

public class MIHsailWavesInfo extends MIInfo {
	
	public class HsailWaveData {
		public String xWg;
		public String yWg;
		public String zWg;
		public String streamEngine;
		public String computeUnit;
		public String simd;
		public String waveId;
	}
	
	private List<HsailWaveData> fHsailWaves = new ArrayList<>();
	
	public List<HsailWaveData> getWavesData() {
		return fHsailWaves;
	}
	
	public MIHsailWavesInfo(MIOutput record) {
		super(record);
		parse();
	}
	
	protected void parse() {
		if(isDone()) {
			MIOutput output = getMIOutput();
			MIResultRecord rr = output.getMIResultRecord();
			
			if( rr != null) {
				MIResult[] results = rr.getMIResults();
				if(results.length == 0)
					return;
				
				if(results[0].value instanceof MIList) {
					for(int i = 0; i < ((MIList)results[0].value).getMIValues().length; i++) {
						MITuple tuple = (MITuple)((MIList)results[0].value).getMIValues()[i];
						processWaveData(tuple);
					}
				}				
			}
		}
	}
	
	protected void processWaveData(MITuple tuple) {
		HsailWaveData data = new HsailWaveData();
		MIResult[]  results = tuple.getMIResults();
		for(int i = 0; i < results.length; i++) {
			if(results[i].getVariable().equals("wgX")) { //$NON-NLS-1$
				data.xWg = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("wgY")) { //$NON-NLS-1$
				data.yWg = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("wgZ")) { //$NON-NLS-1$
				data.zWg = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("stream-engine")) { //$NON-NLS-1$
				data.streamEngine = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("compute-unit")) { //$NON-NLS-1$
				data.computeUnit = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("simdId")) {
				data.simd = results[i].getMIValue().toString();
			} else if(results[i].getVariable().equals("waveId")) {
				data.waveId = results[i].getMIValue().toString();
			}
		}
		fHsailWaves.add(data);
	}

}
