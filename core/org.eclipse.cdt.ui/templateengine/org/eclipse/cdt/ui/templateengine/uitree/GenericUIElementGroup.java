/*******************************************************************************
 * Copyright (c) 2007 Symbian Software Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Bala Torati (Symbian) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.templateengine.uitree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.ui.templateengine.uitree.uiwidgets.UIComposite;

/**
 * The GenericUIElementGroup extends UIElement, implements the default behaviour
 * expected from UIElementGroup. This gives behaviour expected for PAGES-ONLY
 * type. Any other type of UIElement groups can override the definitions given
 * to methods in this class.
 * 
 * @since 4.0
 */
public class GenericUIElementGroup extends UIElement {
	/**
	 * @deprecated use {@value UIGroupTypeEnum#PAGES_ONLY}
	 */
	public static String PAGES_ONLY = UIGroupTypeEnum.PAGES_ONLY.getId();
	/**
	 * @deprecated use {@value UIGroupTypeEnum#PAGES_ONLY}
	 */
	public static String PAGES_TAB = UIGroupTypeEnum.PAGES_TAB.getId();
	/**
	 * @deprecated
	 */
	public static String LOGTYPE = "UIElement"; //$NON-NLS-1$

	UIGroupTypeEnum type = null;

	/**
	 * child list for this UIElement
	 */
	private List<UIElement> childList;

	/**
	 * Call UIElement constructor by passing Attributes as param.
	 * 
	 * @param attribute
	 */
	public GenericUIElementGroup(UIGroupTypeEnum type, UIAttributes/*<String, String>*/ attribute) {
		super(attribute);
		this.type = type;
		this.childList = new ArrayList<UIElement>();
	}

	/*
	 * @see org.eclipse.cdt.ui.templateengine.uitree.UIElement#setValues(java.util.Map)
	 */
	public void setValues(Map<String,String> valueMap) {
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			getChild(i).setValues(valueMap);
		}
	}

	/*
	 * @see org.eclipse.cdt.ui.templateengine.uitree.UIElement#getValues()
	 */
	public Map<String, String> getValues() {
		HashMap<String, String> valueMap = new HashMap<String, String>();
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			valueMap.putAll(getChild(i).getValues());
		}

		return valueMap;
	}

	/*
	 * @see org.eclipse.cdt.ui.templateengine.uitree.UIElement#createWidgets(org.eclipse.cdt.ui.templateengine.uitree.uiwidgets.UIComposite)
	 */
	public void createWidgets(UIComposite uiComposite) {
		int childCount = getChildCount();

		// call createWidgets on all the contained
		// UI widgets.
		if (uiComposite != null) {
			for (int i = 0; i < childCount; i++) {
				getChild(i).createWidgets(uiComposite);
			}
			uiComposite.setData(".uid", getAttributes().get(UIElement.ID)); //$NON-NLS-1$
		}
	}

	/**
	 * dispose the Widget, releasing any resources occupied by this widget. The
	 * same is called on the child list.
	 * 
	 * @see UIElement
	 */
	public void disposeWidget() {
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++)
			getChild(i).disposeWidget();
	}

	/**
	 * getThe child UIElement at the given index. This method throws
	 * SimpleElementException, if invoked on a InputUIElement.
	 * 
	 * @see UIElement
	 * @param index
	 * @return child uiElement
	 */
	public UIElement getChild(int index) {
		return childList.get(index);
	}

	/**
	 * add the given UIElement to the childList. This method throws
	 * SimpleElementException, if invoked on a InputUIElement.
	 * 
	 * @see UIElement
	 * @param aUIElement
	 */
	public void addToChildList(UIElement aUIElement) {
		childList.add(aUIElement);
	}

	/**
	 * returns the child count of UIElement. This method throws
	 * SimpleElementException, if invoked on a InputUIElement.
	 * 
	 * @see UIElement
	 * @return the child count of UIElement
	 */
	public int getChildCount() {
		return childList.size();
	}

	/**
	 * gets the type of this group. This is not used as of now. but can be used
	 * during UIPage construction.
	 */
	public UIGroupTypeEnum getType() {
		return type;
	}

	/*
	 * @see org.eclipse.cdt.ui.templateengine.uitree.UIElement#isValid()
	 */
	public boolean isValid() {
		boolean retVal = true;

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (!getChild(i).isValid()) {
				retVal = false;
				break;
			}
		}

		return retVal;
	}
}
