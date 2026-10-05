package uk.co.whitbread.bart.enums;

public enum BartBookingChannelCode {

	/**
	 * Mobile application
	 */
	MOBILE("MOBILE"),

	/**
	 * Desktop "rebuild" website
	 */
	WEB("WEB2014"),
	
	/**
	 * Desktop "rebuild" DE website
	 */
	WEB_DE("WEB_de"),

	/**
	 * Business Booker a.k.a CBT
	 */
	CBT("CBT");


	private String value;


	private BartBookingChannelCode(String value) {
		this.value = value;
	}


	public String getValue() {
		return value;
	}
}
