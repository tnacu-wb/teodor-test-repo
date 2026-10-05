
package uk.co.whitbread.bart.cancellation.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CancellationResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CancellationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cancellationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refund" type="{http://bartws.micros.com/1.2}Refund" minOccurs="0"/&gt;
 *         &lt;element name="confirmationSent" type="{http://bartws.micros.com/1.2}ArrayOfconfirmationConfirmationSent" minOccurs="0"/&gt;
 *         &lt;element name="cancelBookingError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="RESERVATION_NOT_FOUND"/&gt;
 *               &lt;enumeration value="MISMATCH_OF_DATA"/&gt;
 *               &lt;enumeration value="RESERVATION_IN_USE"/&gt;
 *               &lt;enumeration value="RESERVATION_ALREADY_CANCELLED/RELEASED/ARRIVED"/&gt;
 *               &lt;enumeration value="GROUP_BOOKING"/&gt;
 *               &lt;enumeration value="DEPOSIT_ACTIVE"/&gt;
 *               &lt;enumeration value="AFTER_LATEST_TIME_FOR_CANCELLATION_ON_DAY_OF_ARRIVAL"/&gt;
 *               &lt;enumeration value="CANCELLATION_UNSUCCESSFUL"/&gt;
 *               &lt;enumeration value="EMAIL_ADDRESS_REQUIRED"/&gt;
 *               &lt;enumeration value="TOO_MANY_MATCHES_FOUND"/&gt;
 *               &lt;enumeration value="SESSION_ID_NOT_FOUND"/&gt;
 *               &lt;enumeration value="END_OF_DAY_IN_PROGRESS"/&gt;
 *               &lt;enumeration value="INVALID_CONFIRMATION_TYPE"/&gt;
 *               &lt;enumeration value="INVALID_EMAIL_ADDRESS"/&gt;
 *               &lt;enumeration value="INVALID_UK_MOBILE_NUMBER"/&gt;
 *               &lt;enumeration value="BOOKING_RULES_APPLY"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.2}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancellationResponse", propOrder = {
    "sessionID",
    "cancellationNumber",
    "refund",
    "confirmationSent",
    "cancelBookingError",
    "errorDetail"
})
public class CancellationResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected String cancellationNumber;
    protected Refund refund;
    protected ArrayOfconfirmationConfirmationSent confirmationSent;
    protected String cancelBookingError;
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
     * Gets the value of the cancellationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancellationNumber() {
        return cancellationNumber;
    }

    /**
     * Sets the value of the cancellationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancellationNumber(String value) {
        this.cancellationNumber = value;
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
     * Gets the value of the confirmationSent property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfconfirmationConfirmationSent }
     *     
     */
    public ArrayOfconfirmationConfirmationSent getConfirmationSent() {
        return confirmationSent;
    }

    /**
     * Sets the value of the confirmationSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfconfirmationConfirmationSent }
     *     
     */
    public void setConfirmationSent(ArrayOfconfirmationConfirmationSent value) {
        this.confirmationSent = value;
    }

    /**
     * Gets the value of the cancelBookingError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancelBookingError() {
        return cancelBookingError;
    }

    /**
     * Sets the value of the cancelBookingError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelBookingError(String value) {
        this.cancelBookingError = value;
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
