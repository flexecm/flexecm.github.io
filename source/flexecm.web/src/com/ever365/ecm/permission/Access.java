package com.ever365.ecm.permission;

import java.util.ArrayList;
import java.util.List;

public enum Access {
	
	/**permissions for read*/
	READ(101),
	READ_FULL(102),
	READ_CREATED(103),
	READ_PERMISSION(104),
	READ_CHILDREN(105),
	READ_VERSION(106),
	READ_LOCK_UPDATE(107),
	
	/**for create*/
	CREATE_CONTENT(301),
	CREATE_FOLDER(302),
	
	/**for update*/
	WRITE(201),
	WRITE_FULL(202),
	WRITE_CONTENT(203),
	WRITE_CREATED(204),
	RENAME(205),
	RENAME_CREATED(212),
	ADD_VERSION(206),
	LOCK_CONTENT(207),
	LOCK_FOLDER(208),
	UNLOCK_FOLDER(209),
	UNLOCK_CONTENT(210),
	REMVOE_VERSION(211),
	
	/**for delete*/
	DELETE_CONTENT(401),
	DELETE_FOLDER(402),
	DELETE_CREATED(403),
	DELETE_UN_RECOVER(404),
	
	/**permission*/
	SET_READER(501),
	SET_PERMISSION(502),
	REMOVE_READER(503),
	REMOVE_PERMISSION(504),
	
	
	
	FULL_CONTROL(1000);
	
	private final int value;
	
	public static final List<Integer> All() {
		List<Integer> result = new ArrayList<Integer>();
		
		for (int i = 0; i < values().length; i++) {
			result.add(values()[i].value());
		}
		return result;
	}

	private Access(int value) {
		this.value = value;
	}
	
	/**
	 * Return the integer value of this status code.
	 */
	public int value() {
		return this.value;
	}

	
}
