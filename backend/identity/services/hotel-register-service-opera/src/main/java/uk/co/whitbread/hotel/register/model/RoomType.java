package uk.co.whitbread.hotel.register.model;

public enum RoomType {

	/**
	 * Family
	 */
	FAM,

	/**
	 * Double
	 */
	DB,

	/**
	 * Twin
	 */
	TWIN, 

	/**
	 * Disabled
	 */
	DIS, 

	/**
	 * Single
	 */
	SB;

	public static RoomType forName(String name) {
		for (RoomType rt : values()) {
			if (rt.name().equalsIgnoreCase(name)) {
				return rt;
			}
		}
		return null;
	}
}
