/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.communityhub.apiv1;

import javaxt.rss.Item;

/**
 * Model holder for alert feed items 
 * 
 * @author Jakob Henriksson 
 *
 */
public class FeedItem {

	private Item item;
	
	/**
	 * Constructor 
	 * 
	 * @param item
	 */
	public FeedItem(Item item) {
		this.item = item;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

}
