package org.eclipse.cdt.debug.mi.core;

import org.eclipse.cdt.debug.mi.core.output.MIResultRecord;

/**
 */
public class MIDataReadMemoryInfo extends MIInfo {

	public class Memory {
		int addr;
		int [] data;
		String ascii;
	}

	public MIDataReadMemoryInfo(MIResultRecord rr) {
		super(rr);
	}

	int getAddress() {
		return 0;
	}

	int getBytesNumber() {
		return 0;
	}

	int getTotalBytes() {
		return 0;
	}

	int getNextRow() {
		return 0;
	}

	int getPreviousRow() {
		return 0;
	}

	int getNextPage() {
		return 0;
	}

	int getPreviousPage() {
		return 0;
	}

	Memory[] getMemories() {
		return null;
	}
}
