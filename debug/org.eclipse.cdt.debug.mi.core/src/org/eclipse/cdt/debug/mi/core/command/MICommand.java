/*
 *(c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 * 
 */

package org.eclipse.cdt.debug.mi.core.command;

import org.eclipse.cdt.debug.mi.core.MIInfo;
import org.eclipse.cdt.debug.mi.core.output.MIResultRecord;

/**
 * 
 * Represents a MI command.
 * 
 * @author Mikhail Khodjaiants
 * @since Jul 11, 2002
 */
public class MICommand extends Command
{
	final String[] empty = new String[0];
	String[] options = empty;
	String[] parameters = empty;
	String operation = "";
	String token;

	public MICommand(String oper) {
		this.operation = oper;
	}

	public MICommand(String oper, String[] param) {
		this.operation = oper;
		this.parameters = param;
	}

	public MICommand(String oper, String[] opt, String[] param) {
		this.operation = oper;
		this.options = opt;
		this.parameters = param;
	}

	/**
	 * Returns the operation of this command.
	 * 
	 * @return the operation of this command
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * Returns an array of command's options. An empty collection is 
	 * returned if there are no options.
	 * 
	 * @return an array of command's options
	 */
	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] opt) {
		options = opt;
	}
	
	/**
	 * Returns an array of command's parameters. An empty collection is 
	 * returned if there are no parameters.
	 * 
	 * @return an array of command's parameters
	 */
	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] p) {
		parameters = p;
	}

	public String toString() {
		String command =  getToken() + "-" + getOperation(); 
		if (options != null && options.length > 0) {
			for (int i = 0; i < options.length; i++) {
				command += " " + options[i];
			}
		}
		if (parameters != null && parameters.length > 0) {
			if (options != null && options.length > 0) {
				command += " --";
			}
			for (int i = 0; i < parameters.length; i++) {
				command += " " + parameters[i];
			}
		}
		return command;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String t) {
		token = t;
	}

	public MIInfo getInfo (MIResultRecord rr) {
		return new MIInfo(rr);
	}
}
