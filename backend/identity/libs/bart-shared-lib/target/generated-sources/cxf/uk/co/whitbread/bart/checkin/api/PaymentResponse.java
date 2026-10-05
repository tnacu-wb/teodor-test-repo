
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PaymentResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PaymentResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkInComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="checkInReadback" type="{http://bartws.micros.com/1.0}Readback" minOccurs="0"/&gt;
 *         &lt;element name="nextMessage" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="ThreeDSecureRequest"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="threeDSecure" type="{http://bartws.micros.com/1.0}ThreeDSecure" minOccurs="0"/&gt;
 *         &lt;element name="paymentDetailsError" minOccurs="0"&gt;
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
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_ACCOUNT_ON_HOLD"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_VALIDATION_UNAVAILABLE"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_DINNER_AND_MEALDEAL"/&gt;
 *               &lt;enumeration value="CNP_AUTHORIZATION_REQUIRED"/&gt;
 *               &lt;enumeration value="BREAKFAST_NOT_AVAILABLE_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="DINNER_NOT_AVAILABLE_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="ALCOHOL_ALLOWED_FIELD_PASSED_WITHOUT_DINNER_ALLOWANCE"/&gt;
 *               &lt;enumeration value="CHARITABLE_DONATIONS_NOT_ALLOWED_IN_THIS_HOTEL"/&gt;
 *               &lt;enumeration value="REQUESTED_BREAKFAST_NOT_AVAILABLE"/&gt;
 *               &lt;enumeration value="SESSION_NO_LONGER_VALID"/&gt;
 *               &lt;enumeration value="PREPAYMENT_FAILED_DECLINE"/&gt;
 *               &lt;enumeration value="PREPAYMENT_FAILED_SYSTEM"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_ACCOUNT_ON_HOLD"/&gt;
 *               &lt;enumeration value="CNP_VALIDATION_FAILED_AUTHENTICATION_UNAVAILABLE"/&gt;
 *               &lt;enumeration value="PREPAYMENT_NOT_ALLOWED_FOR_THIS_CARD_TYPE"/&gt;
 *               &lt;enumeration value="FRAUD_CHECK_FAILED"/&gt;
 *               &lt;enumeration value="INVALID_MESSAGE_SEQUENCE"/&gt;
 *               &lt;enumeration value="CHANGES_TO_SETTLEMENT_METHOD_NOT_ALLOWED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.0}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentResponse", propOrder = {
    "sessionID",
    "checkInComplete",
    "checkInReadback",
    "nextMessage",
    "threeDSecure",
    "paymentDetailsError",
    "errorDetail"
})
public class PaymentResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String sessionID;
    protected Boolean checkInComplete;
    protected Readback checkInReadback;
    protected String nextMessage;
    protected ThreeDSecure threeDSecure;
    protected String paymentDetailsError;
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
     * Gets the value of the checkInComplete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckInComplete() {
        return checkInComplete;
    }

    /**
     * Sets the value of the checkInComplete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckInComplete(Boolean value) {
        this.checkInComplete = value;
    }

    /**
     * Gets the value of the checkInReadback property.
     * 
     * @return
     *     possible object is
     *     {@link Readback }
     *     
     */
    public Readback getCheckInReadback() {
        return checkInReadback;
    }

    /**
     * Sets the value of the checkInReadback property.
     * 
     * @param value
     *     allowed object is
     *     {@link Readback }
     *     
     */
    public void setCheckInReadback(Readback value) {
        this.checkInReadback = value;
    }

    /**
     * Gets the value of the nextMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextMessage() {
        return nextMessage;
    }

    /**
     * Sets the value of the nextMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextMessage(String value) {
        this.nextMessage = value;
    }

    /**
     * Gets the value of the threeDSecure property.
     * 
     * @return
     *     possible object is
     *     {@link ThreeDSecure }
     *     
     */
    public ThreeDSecure getThreeDSecure() {
        return threeDSecure;
    }

    /**
     * Sets the value of the threeDSecure property.
     * 
     * @param value
     *     allowed object is
     *     {@link ThreeDSecure }
     *     
     */
    public void setThreeDSecure(ThreeDSecure value) {
        this.threeDSecure = value;
    }

    /**
     * Gets the value of the paymentDetailsError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentDetailsError() {
        return paymentDetailsError;
    }

    /**
     * Sets the value of the paymentDetailsError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentDetailsError(String value) {
        this.paymentDetailsError = value;
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
