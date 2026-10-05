
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingValidationRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingValidationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bookingValidationDetails" type="{http://bartws.micros.com/1.31}BookingValidationDetails" minOccurs="0"/&gt;
 *         &lt;element name="bookingGuestHistoryDetails" type="{http://bartws.micros.com/1.31}BookingGuestHistoryDetails" minOccurs="0"/&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="route" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="AMEND"/&gt;
 *               &lt;enumeration value="CANCEL"/&gt;
 *               &lt;enumeration value="CHECK"/&gt;
 *               &lt;enumeration value="CIOL"/&gt;
 *               &lt;enumeration value="CAR"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingValidationRequest", propOrder = {
    "bookingValidationDetails",
    "bookingGuestHistoryDetails",
    "sessionID",
    "route"
})
public class BookingValidationRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected BookingValidationDetails bookingValidationDetails;
    protected BookingGuestHistoryDetails bookingGuestHistoryDetails;
    protected String sessionID;
    protected String route;

    /**
     * Gets the value of the bookingValidationDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookingValidationDetails }
     *     
     */
    public BookingValidationDetails getBookingValidationDetails() {
        return bookingValidationDetails;
    }

    /**
     * Sets the value of the bookingValidationDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingValidationDetails }
     *     
     */
    public void setBookingValidationDetails(BookingValidationDetails value) {
        this.bookingValidationDetails = value;
    }

    /**
     * Gets the value of the bookingGuestHistoryDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookingGuestHistoryDetails }
     *     
     */
    public BookingGuestHistoryDetails getBookingGuestHistoryDetails() {
        return bookingGuestHistoryDetails;
    }

    /**
     * Sets the value of the bookingGuestHistoryDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingGuestHistoryDetails }
     *     
     */
    public void setBookingGuestHistoryDetails(BookingGuestHistoryDetails value) {
        this.bookingGuestHistoryDetails = value;
    }

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
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoute(String value) {
        this.route = value;
    }

}
