
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RoomUpdateError complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RoomUpdateError"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="roomID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guestNumber" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="bookerError" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="field" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="ROOMID"/&gt;
 *               &lt;enumeration value="TITLE"/&gt;
 *               &lt;enumeration value="FIRSTNAME"/&gt;
 *               &lt;enumeration value="LASTNAME"/&gt;
 *               &lt;enumeration value="TELEPHONENUMBER"/&gt;
 *               &lt;enumeration value="MOBILENUMBER"/&gt;
 *               &lt;enumeration value="EMAILADDRESS"/&gt;
 *               &lt;enumeration value="ADDRESSLINE1"/&gt;
 *               &lt;enumeration value="ADDRESSLINE2"/&gt;
 *               &lt;enumeration value="ADDRESSLINE3"/&gt;
 *               &lt;enumeration value="ADDRESSLINE4"/&gt;
 *               &lt;enumeration value="ADDRESSLINE5"/&gt;
 *               &lt;enumeration value="COUNTRYCODE"/&gt;
 *               &lt;enumeration value="POSTCODE"/&gt;
 *               &lt;enumeration value="COMPANYNAME"/&gt;
 *               &lt;enumeration value="COMPANYID"/&gt;
 *               &lt;enumeration value="GUESTHISTORYNUMBER"/&gt;
 *               &lt;enumeration value="CARREGISTRATION"/&gt;
 *               &lt;enumeration value="NATIONALITY"/&gt;
 *               &lt;enumeration value="PASSPORTNUMBER"/&gt;
 *               &lt;enumeration value="PLACEOFISSUE"/&gt;
 *               &lt;enumeration value="NEXTDESTINATION"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="guestUpdateError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="SITE_END_OF_DAY_IN_PROGRESS"/&gt;
 *               &lt;enumeration value="INVALID_MESSAGE_SEQUENCE"/&gt;
 *               &lt;enumeration value="INVALID_CONFIRMATION_NUMBER"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.0}ErrorDetails" minOccurs="0"/&gt;
 *         &lt;element name="displayToGuest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoomUpdateError", propOrder = {
    "roomID",
    "guestNumber",
    "bookerError",
    "field",
    "guestUpdateError",
    "errorDetail",
    "displayToGuest"
})
public class RoomUpdateError
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String roomID;
    protected Long guestNumber;
    protected Boolean bookerError;
    protected String field;
    protected String guestUpdateError;
    protected ErrorDetails errorDetail;
    protected Boolean displayToGuest;

    /**
     * Gets the value of the roomID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * Sets the value of the roomID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomID(String value) {
        this.roomID = value;
    }

    /**
     * Gets the value of the guestNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getGuestNumber() {
        return guestNumber;
    }

    /**
     * Sets the value of the guestNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setGuestNumber(Long value) {
        this.guestNumber = value;
    }

    /**
     * Gets the value of the bookerError property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBookerError() {
        return bookerError;
    }

    /**
     * Sets the value of the bookerError property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBookerError(Boolean value) {
        this.bookerError = value;
    }

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField(String value) {
        this.field = value;
    }

    /**
     * Gets the value of the guestUpdateError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestUpdateError() {
        return guestUpdateError;
    }

    /**
     * Sets the value of the guestUpdateError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestUpdateError(String value) {
        this.guestUpdateError = value;
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

    /**
     * Gets the value of the displayToGuest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisplayToGuest() {
        return displayToGuest;
    }

    /**
     * Sets the value of the displayToGuest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisplayToGuest(Boolean value) {
        this.displayToGuest = value;
    }

}
