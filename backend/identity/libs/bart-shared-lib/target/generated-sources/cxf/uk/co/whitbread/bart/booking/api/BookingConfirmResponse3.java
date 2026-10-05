
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingConfirmResponse3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingConfirmResponse3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="totalCost" type="{http://bartws.micros.com/1.31}Price3"/&gt;
 *         &lt;element name="vatRate" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="guestHistoryNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="registerGuest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="readbackMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="prepayment" type="{http://bartws.micros.com/1.31}Prepayment3" minOccurs="0"/&gt;
 *         &lt;element name="refund" type="{http://bartws.micros.com/1.31}Refund" minOccurs="0"/&gt;
 *         &lt;element name="checkInOnline" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carData" type="{http://bartws.micros.com/1.31}CarData" minOccurs="0"/&gt;
 *         &lt;element name="bookingConfirmError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_DIGITS_IN_CREDIT_CARD_NUMBER"/&gt;
 *               &lt;enumeration value="INVALID_LUHN_CHECK_DIGIT"/&gt;
 *               &lt;enumeration value="CARD_TYPE_NOT_ACCEPTED_AT_PROPERTY"/&gt;
 *               &lt;enumeration value="CARD_TYPE_CANNOT_BE_ESTABLISHED"/&gt;
 *               &lt;enumeration value="CARD_NOT_VALID_ON_DAY_OF_ARRIVAL"/&gt;
 *               &lt;enumeration value="CARD_NOT_VALID_ON_DAY_OF_DEPARTURE"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="SITE_END_OF_DAY_IN_PROGRESS"/&gt;
 *               &lt;enumeration value="BUSINESS_ACCOUNT_DATA_NOT_ALLOWED_FOR_NON_BUSINESS_ACCOUNT_CARD_TYPE"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_USER_NAME_NOT_FOUND"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_INCORRECT_PASSWORD"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_USER_NAME_NOT_AUTHORIZED_FOR_THIS_CARD"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_AGENCY_NOT_CONFIGURED_FOR_CNP_TRANSACTIONS"/&gt;
 *               &lt;enumeration value="BREAKFAST_NOT_AVAILABLE_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="DINNER_NOT_AVAILABLE_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="ALCOHOL_ALLOWED_FIELD_PASSED_WITHOUT_DINNER_ALLOWANCE"/&gt;
 *               &lt;enumeration value="PREPAYMENT_REQUESTED_BUT_NOT_AVAILABLE_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="PREPAYMENT_REQUESTED_BUT_3D_SECURE_CHECK_OF_CARDNUMBER_FAILED"/&gt;
 *               &lt;enumeration value="CHARITABLE_DONATIONS_NOT_ALLOWED_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="DINNER_DATES_OUTSIDE_STAY_DATES"/&gt;
 *               &lt;enumeration value="REQUESTED_BREAKFAST_NOT_AVAILABLE"/&gt;
 *               &lt;enumeration value="SESSION_NO_LONGER_VALID_BOOKING"/&gt;
 *               &lt;enumeration value="SESSION_NO_LONGER_VALID_TIMEOUT"/&gt;
 *               &lt;enumeration value="PREPAYMENT_FAILED"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_ACCOUNT_ON_HOLD"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_AUTHENTICATION_UNAVAILABLE"/&gt;
 *               &lt;enumeration value="PREPAYMENT_NOT_ALLOWED_FOR_THIS_CARD_TYPE"/&gt;
 *               &lt;enumeration value="NO_REGISTERED_CARD_ON_FILE"/&gt;
 *               &lt;enumeration value="GUEST_HISTORY_NOT_FOUND"/&gt;
 *               &lt;enumeration value="CARD_TYPE_NOT_AVAILBLE_FOR_GUARANTEE"/&gt;
 *               &lt;enumeration value="PREPAYMENT_REQUIRED"/&gt;
 *               &lt;enumeration value="FRAUD_CHECK_FAILED"/&gt;
 *               &lt;enumeration value="BOOKING_AMEND_FAILED"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_NO_ACCOUNT_ASSOCIATED_WITH_THESE_USER_DETAILS"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_THIS_CARD_HAS_NOT_BEEN_REGISTERED"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_CARD_ON_STOP"/&gt;
 *               &lt;enumeration value="3DSECUREv2_AUTH_FAILED"/&gt;
 *               &lt;enumeration value="AUTH_DECLINED"/&gt;
 *               &lt;enumeration value="AUTH_FAILED"/&gt;
 *               &lt;enumeration value="MISSING_SCA_AUTH_DATE_TIME"/&gt;
 *               &lt;enumeration value="INVALID_SCA_AMOUNT"/&gt;
 *               &lt;enumeration value="INVALID_CURRENCY"/&gt;
 *               &lt;enumeration value="INVALID_3DS_VERSION"/&gt;
 *               &lt;enumeration value="MISSING_SCA_OUTCOME"/&gt;
 *               &lt;enumeration value="MISSING_SCATRANSREF"/&gt;
 *               &lt;enumeration value="SCHEME_CARD_TYPE_MISMATCH"/&gt;
 *               &lt;enumeration value="MISSING_TRANSACTION_DATA"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.31}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingConfirmResponse3", propOrder = {
    "sessionID",
    "confirmationNumber",
    "totalCost",
    "vatRate",
    "guestHistoryNumber",
    "registerGuest",
    "readbackMessage",
    "prepayment",
    "refund",
    "checkInOnline",
    "carData",
    "bookingConfirmError",
    "errorDetail"
})
public class BookingConfirmResponse3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String confirmationNumber;
    @XmlElement(required = true)
    protected Price3 totalCost;
    protected double vatRate;
    protected String guestHistoryNumber;
    protected Boolean registerGuest;
    protected String readbackMessage;
    protected Prepayment3 prepayment;
    protected Refund refund;
    protected Boolean checkInOnline;
    protected CarData carData;
    protected String bookingConfirmError;
    protected ErrorDetails errorDetail;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
    }

    /**
     * Gets the value of the totalCost property.
     * 
     * @return
     *     possible object is
     *     {@link Price3 }
     *     
     */
    public Price3 getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the value of the totalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price3 }
     *     
     */
    public void setTotalCost(Price3 value) {
        this.totalCost = value;
    }

    /**
     * Gets the value of the vatRate property.
     * 
     */
    public double getVatRate() {
        return vatRate;
    }

    /**
     * Sets the value of the vatRate property.
     * 
     */
    public void setVatRate(double value) {
        this.vatRate = value;
    }

    /**
     * Gets the value of the guestHistoryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestHistoryNumber() {
        return guestHistoryNumber;
    }

    /**
     * Sets the value of the guestHistoryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestHistoryNumber(String value) {
        this.guestHistoryNumber = value;
    }

    /**
     * Gets the value of the registerGuest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRegisterGuest() {
        return registerGuest;
    }

    /**
     * Sets the value of the registerGuest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRegisterGuest(Boolean value) {
        this.registerGuest = value;
    }

    /**
     * Gets the value of the readbackMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReadbackMessage() {
        return readbackMessage;
    }

    /**
     * Sets the value of the readbackMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReadbackMessage(String value) {
        this.readbackMessage = value;
    }

    /**
     * Gets the value of the prepayment property.
     * 
     * @return
     *     possible object is
     *     {@link Prepayment3 }
     *     
     */
    public Prepayment3 getPrepayment() {
        return prepayment;
    }

    /**
     * Sets the value of the prepayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Prepayment3 }
     *     
     */
    public void setPrepayment(Prepayment3 value) {
        this.prepayment = value;
    }

    /**
     * Gets the value of the refund property.
     * 
     * @return
     *     possible object is
     *     {@link Refund }
     *     
     */
    public Refund getRefund() {
        return refund;
    }

    /**
     * Sets the value of the refund property.
     * 
     * @param value
     *     allowed object is
     *     {@link Refund }
     *     
     */
    public void setRefund(Refund value) {
        this.refund = value;
    }

    /**
     * Gets the value of the checkInOnline property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckInOnline() {
        return checkInOnline;
    }

    /**
     * Sets the value of the checkInOnline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckInOnline(Boolean value) {
        this.checkInOnline = value;
    }

    /**
     * Gets the value of the carData property.
     * 
     * @return
     *     possible object is
     *     {@link CarData }
     *     
     */
    public CarData getCarData() {
        return carData;
    }

    /**
     * Sets the value of the carData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarData }
     *     
     */
    public void setCarData(CarData value) {
        this.carData = value;
    }

    /**
     * Gets the value of the bookingConfirmError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingConfirmError() {
        return bookingConfirmError;
    }

    /**
     * Sets the value of the bookingConfirmError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingConfirmError(String value) {
        this.bookingConfirmError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetail(ErrorDetails value) {
        this.errorDetail = value;
    }

}
