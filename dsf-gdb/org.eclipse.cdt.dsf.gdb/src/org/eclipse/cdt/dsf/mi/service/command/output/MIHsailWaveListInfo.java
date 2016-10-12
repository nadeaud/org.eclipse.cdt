package org.eclipse.cdt.dsf.mi.service.command.output;

import java.util.ArrayList;
import java.util.List;

import didier.multicore.visualizer.fx.utils.model.HsailWaveModel;

public class MIHsailWaveListInfo extends MIInfo {
	
	private static String HSAIL_MI_LIST_NAME = "hsail-threads"; //$NON-NLS-1$
	private static String HSAIL_MI_SE_NAME = "stream-engine"; //$NON-NLS-1$
	private static String HSAIL_MI_CU_NAME = "compute-unit"; //$NON-NLS-1$
	private static String HSAIL_MI_WAVE_NAME = "wave"; //$NON-NLS-1$
	private static String HSAIL_MI_SIMD_NAME = "simd"; //$NON-NLS-1$
	
	private List<HsailWaveModel> fWaveList;
	
	public MIHsailWaveListInfo(MIOutput record) {
		super(record);
		parse();
	}
	
	public List<HsailWaveModel> getWaveList() {
		return fWaveList;
	}
	
	protected void parse() {
		if(isDone()) {
			MIOutput output = getMIOutput();
			MIResultRecord rr = output.getMIResultRecord();
			if( rr != null) {
				fWaveList = new ArrayList<>();
				MIResult[] results = rr.getMIResults();
				for(int i = 0; i < results.length; i++) {
					String var = results[i].getVariable();
					if (var.equals(HSAIL_MI_LIST_NAME)) {
						MIValue val = results[i].getMIValue();
						if(val instanceof MIList) {
							MIValue[] values = ((MIList)val).getMIValues();
							
							for(int j = 0; j < values.length ; j++) {
								MITuple tuple = (MITuple)values[j];
								fWaveList.add(parseTuple(tuple));
							}
						}
					}
				}
				return;
			}
		}
		fWaveList = null;
	}
	
	protected HsailWaveModel parseTuple(MITuple tuple) {
		HsailWaveModel model = new HsailWaveModel();
		MIResult[] results = tuple.getMIResults();
		
		for(int i = 0; i < results.length; i++) {
			if( results[i].getVariable().equals(HSAIL_MI_SE_NAME) ) {
				MIConst val = (MIConst)results[i].getMIValue();
				int x = Integer.parseInt(val.getCString());
				model.se_id = x;
			}else if( results[i].getVariable().equals(HSAIL_MI_CU_NAME) ) {
				MIConst val = (MIConst)results[i].getMIValue();
				int x = Integer.parseInt(val.getCString());
				model.cu_id = x;
			}else if( results[i].getVariable().equals(HSAIL_MI_SIMD_NAME)) {
				MIConst val = (MIConst)results[i].getMIValue();
				int x = Integer.parseInt(val.getCString());
				model.simd_id = x;
			}else if( results[i].getVariable().equals(HSAIL_MI_WAVE_NAME)) {
				MIConst val = (MIConst)results[i].getMIValue();
				int x = Integer.parseInt(val.getCString());
				model.wave_id = x;
			}
		}
		
		return model;		
	}

}
