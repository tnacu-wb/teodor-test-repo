
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingValidationResponse3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingValidationResponse3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nameDetails" type="{http://bartws.micros.com/1.31}ArrayOfnameDetailNameDetails" minOccurs="0"/&gt;
 *         &lt;element name="bookingDetails" type="{http://bartws.micros.com/1.31}BookingDetails3" minOccurs="0"/&gt;
 *         &lt;element name="upsellItemsAvailable" type="{http://bartws.micros.com/1.31}ArrayOfupsellItemUpsellItem" minOccurs="0"/&gt;
 *         &lt;element name="bookingValidationError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="RESERVATION_NOT_FOUND"/&gt;
 *               &lt;enumeration value="RESERVATION_IN_USE"/&gt;
 *               &lt;enumeration value="RESERVATION_ALREADY_CANCELLED"/&gt;
 *               &lt;enumeration value="GROUP_BOOKING"/&gt;
 *               &lt;enumeration value="DEPOSIT_ACTIVE"/&gt;
 *               &lt;enumeration value="AFTER_LATEST_TIME_FOR_CANCELLATION_ON_ARRIVAL"/&gt;
 *               &lt;enumeration value="GREATER_THAN_MAXIMUM_NIGHTS_ALLOWED_FOR_CANCELLATION/AMENDMENT"/&gt;
 *               &lt;enumeration value="BOOKING_RULES_APPLY_-_CANCELLATION/AMENDMENT_NOT_ALLOWED"/&gt;
 *               &lt;enumeration value="END_OF_DAY_IN_PROGRESS"/&gt;
 *               &lt;enumeration value="CANCELLALATION_NOT_SUCCESSFUL"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cbtDetails" type="{http://bartws.micros.com/1.31}CBTDetails" minOccurs="0"/&gt;
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
@XmlType(name = "BookingValidationResponse3", propOrder = {
    "sessionID",
    "nameDetails",
    "bookingDetails",
    "upsellItemsAvailable",
    "bookingValidationError",
    "cbtDetails",
    "errorDetail"
})
public class BookingValidationResponse3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected ArrayOfnameDetailNameDetails nameDetails;
    protected BookingDetails3 bookingDetails;
    protected ArrayOfupsellItemUpsellItem upsellItemsAvailable;
    protected String bookingValidationError;
    protected CBTDetails cbtDetails;
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
     * Gets the value of the nameDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfnameDetailNameDetails }
     *     
     */
    public ArrayOfnameDetailNameDetails getNameDetails() {
        return nameDetails;
    }

    /**
     * Sets the value of the nameDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfnameDetailNameDetails }
     *     
     */
    public void setNameDetails(ArrayOfnameDetailNameDetails value) {
        this.nameDetails = value;
    }

    /**
     * Gets the value of the bookingDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookingDetails3 }
     *     
     */
    public BookingDetails3 getBookingDetails() {
        return bookingDetails;
    }

    /**
     * Sets the value of the bookingDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingDetails3 }
     *     
     */
    public void setBookingDetails(BookingDetails3 value) {
        this.bookingDetails = value;
    }

    /**
     * Gets the value of the upsellItemsAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public ArrayOfupsellItemUpsellItem getUpsellItemsAvailable() {
        return upsellItemsAvailable;
    }

    /**
     * Sets the value of the upsellItemsAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public void setUpsellItemsAvailable(ArrayOfupsellItemUpsellItem value) {
        this.upsellItemsAvailable = value;
    }

    /**
     * Gets the value of the bookingValidationError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingValidationError() {
        return bookingValidationError;
    }

    /**
     * Sets the value of the bookingValidationError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingValidationError(String value) {
        this.bookingValidationError = value;
    }

    /**
     * Gets the value of the cbtDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CBTDetails }
     *     
     */
    public CBTDetails getCbtDetails() {
        return cbtDetails;
    }

    /**
     * Sets the value of the cbtDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBTDetails }
     *     
     */
    public void setCbtDetails(CBTDetails value) {
        this.cbtDetails = value;
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
