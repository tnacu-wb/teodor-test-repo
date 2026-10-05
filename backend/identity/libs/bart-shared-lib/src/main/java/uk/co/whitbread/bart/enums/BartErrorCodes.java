package uk.co.whitbread.bart.enums;

public enum BartErrorCodes {

	MISSING_MANDATORY_FIELD("MISSING_MANDATORY_FIELD"),
	INVALID_DATA_IN_FIELD("INVALID_DATA_IN_FIELD"),
	SESSION_NO_LONGER_VALID("SESSION_NO_LONGER_VALID"),

	NO_AVAILABILITY_AT_SPECIFIED_PROPERTY("NO_AVAILABILITY_AT_SPECIFIED_PROPERTY"),
	BOOKINGS_NOT_ACCEPTED_AT_SPECIFIED_PROPERTY("BOOKINGS_NOT_ACCEPTED_AT_SPECIFIED_PROPERTY"),

	ARRIVAL_TOO_FAR_IN_ADVANCE("ARRIVAL_TOO_FAR_IN_ADVANCE"),
	DEPARTURE_TOO_FAR_IN_ADVANCE("DEPARTURE_TOO_FAR_IN_ADVANCE"),

	INVALID_PROPERTY_CODE_SUPPLIED("INVALID_PROPERTY_CODE_SUPPLIED"),

	PREPAYMENT_NOT_REQUIRED("PREPAYMENT_NOT_REQUIRED");

	private String errorCode;

	BartErrorCodes(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public static BartErrorCodes getByCode(String errorCode) {
		for (BartErrorCodes code : values()) {
			if (code.getErrorCode().equalsIgnoreCase(errorCode) || code.name().equalsIgnoreCase(errorCode)) {
                return code;
			}
		}
		return null;
	}
}
