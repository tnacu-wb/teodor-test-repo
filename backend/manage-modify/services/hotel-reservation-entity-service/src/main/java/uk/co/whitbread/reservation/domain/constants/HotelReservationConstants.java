package uk.co.whitbread.reservation.domain.constants;

public class HotelReservationConstants {

  private HotelReservationConstants() {
  }

  public static final String EMPLOYEE = "Employee";
  public static final String TRAVEL_INDUSTRY_RATE = "FCDNLR30";
  public static final String RESERVED_BOOKING_STATUS = "RESERVED";
  public static final String UPCOMING_BOOKING_STATUS = "Upcoming";
  public static final String ARRIVED_BOOKING_STATUS = "ARRIVED";
  public static final String PAST_BOOKING_STATUS = "Past";
  public static final String UNARRIVED_BOOKING_STATUS = "UNARRIVED";
  public static final String CANCELLED_BOOKING_STATUS = "CANCELLED";
  public static final String UNDEFINED_BOOKING_STATUS = "Undefined";
  public static final String CHECKED_IN_BOOKING_STATUS = "Checked-In";
  public static final String NO_SHOW = "NOSHOW";
  public static final String CHECKED_OUT_STATUS = "CHECKEDOUT";
  public static final String ACCOMMODATION_ALLOWANCE_NAME = "accommodation";
  public static final String BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE = "ALLOWANCE";
  public static final String BUSINESS_ALLOWANCE_RULE_PACKAGE_TYPE = "PACKAGE";
  public static final String UNKNOWN_CARD_TYPE = "";
  public static final String PIBA_CARD_TYPE_AEM_ID = "piba";
  public static final String CREDIT_CARD_TYPE_AEM_ID = "card";
  public static final String PIBA_UK_CARD_TYPE = "BU";
  public static final String PIBA_EURO_CARD_TYPE = "BD";
  public static final String CASH_PAYMENT_METHOD_OPERA_CODE = "CA";
  public static final String DIRECT_SETTLEMENT_PAYMENT_METHOD_OPERA_CODE = "DS";
  public static final String A2C_GUARANTEE_OPERA_CODE = "CO";
  public static final String DEPOSIT_RECEIVED_GUARANTEE_OPERA_CODE = "DRV";
  public static final String CREDIT_CARD_GUARANTEED_OPERA_CODE = "CC";
  public static final String NON_GUARANTEED_OPERA_CODE = "NON";
  public static final String ALLOWED = "Yes";

  public static final String CCUI_BOOKING_CHANNEL = "CCUI";
  public static final String BB_BOOKING_CHANNEL = "BB";
  public static final String DISTR_BOOKING_CHANNEL = "DISTR";
  public static final String PI_BOOKING_CHANNEL = "PI";
  public static final String FRONT_DESK_BOOKING_CHANNEL = "FD";

  public static final String COUNTRY_CODE_GB = "GB";
  public static final String COUNTRY_CODE_DE = "DE";
  public static final String LANGUAGE_EN = "en";
  public static final String COUNTRY_GB = "gb";
  public static final String RELEASED_BOOKING_STATUS = "RELEASED";
  public static final String INHOUSE_BOOKING_STATUS = "INHOUSE";
  public static final String DUEOUT_BOOKING_STATUS =  "DUEOUT";
  public static final String PENDINGCHECKOUT_BOOKING_STATUS = "PENDINGCHECKOUT";
  public static final String CHECKEDIN_BOOKING_STATUS =  "CHECKEDIN";
  public static final String REQUESTED_BOOKING_STATUS =  "REQUESTED";
  public static final String WAITLISTED_BOOKING_STATUS =  "WAITLISTED";
  public static final String PAST_BOOK_STATUS = "PAST";
  public static final String PREPAID_BOOKING_STATUS  = "PREPAID";
  public static final String DUEIN_BOOKING_STATUS = "DUEIN";
  public static final String FUTURE_BOOK_STATUS =  "FUTURE";
  public static final String RESERVATION_CACHE = "ReservationCache";
  public static final String UDFC20 = "UDFC20";
  public static final String BU = "BU";
  public static final String BD = "BD";
  public static final int FOLIO_VIEW_CP = 1;
  public static final int FOLIO_VIEW_CNP = 2;
  public static final String ID_CONTEXT = "3rd Party";

  public static final String CNP_ALERT_CODE = "ECNP";
  public static final String CNP_ALERT_AREA = "CHECKIN";
  public static final String CNP_ALERT_DESCRIPTION = """
      Payment Check Required - Complete checks before requesting payment directly from the guest:
      • Review Window 2 for existing card details
      • Check Booking Notes for existing payment authorisation
      • Check Routings tab for existing routings""";

}
