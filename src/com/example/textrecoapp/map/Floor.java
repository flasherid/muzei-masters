/*
 * Copyright (C) 2014 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

public class Floor {

	private String floorId;
	private String pictureFilename;
	private int floorLevel;
	private boolean isDefault;

	public Floor(String floorId, String pictureFilename, int floorLevel,
			boolean isDefault) {
		this.floorId = floorId;
		this.pictureFilename = pictureFilename;
		this.floorLevel = floorLevel;
		this.isDefault = isDefault;
	}

	public String getFloorId() {
		return floorId;
	}

	public String getPictureFilename() {
		return pictureFilename;
	}

	public int getFloorLevel() {
		return floorLevel;
	}

	public boolean isDefault() {
		return isDefault;
	}

}
