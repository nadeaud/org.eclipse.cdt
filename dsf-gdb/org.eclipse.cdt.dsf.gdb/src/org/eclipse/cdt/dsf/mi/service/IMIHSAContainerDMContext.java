package org.eclipse.cdt.dsf.mi.service;

/**
 * A container that represent an ensemble of work-group.
 * 
 * @author Didier Nadeau
 *
 */
public interface IMIHSAContainerDMContext extends IMIContainerDMContext {
	
	/* Return the axis (x,y or z) for the work-group. */
	public String getAxis();
	
	public IMIHSAContainerDMContext getHSAParent();

}
