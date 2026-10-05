
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Room complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Room"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="roomType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="DB"/&gt;
 *               &lt;enumeration value="TWIN"/&gt;
 *               &lt;enumeration value="FAM"/&gt;
 *               &lt;enumeration value="DIS"/&gt;
 *               &lt;enumeration value="SB"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="roomID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guestName" type="{http://bartws.micros.com/1.31}GuestName"/&gt;
 *         &lt;element name="adults" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="children" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="cot" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guestDetails" type="{http://bartws.micros.com/1.31}CheckInGuest" minOccurs="0"/&gt;
 *         &lt;element name="carDataPresent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carDetails" type="{http://bartws.micros.com/1.31}CarDetails" minOccurs="0"/&gt;
 *         &lt;element name="bookingStatus"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="UNARRIVED"/&gt;
 *               &lt;enumeration value="ARRIVED"/&gt;
 *               &lt;enumeration value="CANCELLED"/&gt;
 *               &lt;enumeration value="RELEASED"/&gt;
 *               &lt;enumeration value="CHECKEDIN"/&gt;
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
@XmlType(name = "Room", propOrder = {
    "roomType",
    "roomID",
    "guestName",
    "adults",
    "children",
    "cot",
    "guestDetails",
    "carDataPresent",
    "carDetails",
    "bookingStatus"
})
public class Room
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String roomType;
    protected String roomID;
    @XmlElement(required = true)
    protected GuestName guestName;
    protected long adults;
    protected Long children;
    protected Boolean cot;
    protected CheckInGuest guestDetails;
    protected Boolean carDataPresent;
    protected CarDetails carDetails;
    @XmlElement(required = true)
    protected String bookingStatus;

    /**
     * Gets the value of the roomType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Sets the value of the roomType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomType(String value) {
        this.roomType = value;
    }

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
     * Gets the value of the guestName property.
     * 
     * @return
     *     possible object is
     *     {@link GuestName }
     *     
     */
    public GuestName getGuestName() {
        return guestName;
    }

    /**
     * Sets the value of the guestName property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuestName }
     *     
     */
    public void setGuestName(GuestName value) {
        this.guestName = value;
    }

    /**
     * Gets the value of the adults property.
     * 
     */
    public long getAdults() {
        return adults;
    }

    /**
     * Sets the value of the adults property.
     * 
     */
    public void setAdults(long value) {
        this.adults = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setChildren(Long value) {
        this.children = value;
    }

    /**
     * Gets the value of the cot property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCot() {
        return cot;
    }

    /**
     * Sets the value of the cot property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCot(Boolean value) {
        this.cot = value;
    }

    /**
     * Gets the value of the guestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CheckInGuest }
     *     
     */
    public CheckInGuest getGuestDetails() {
        return guestDetails;
    }

    /**
     * Sets the value of the guestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckInGuest }
     *     
     */
    public void setGuestDetails(CheckInGuest value) {
        this.guestDetails = value;
    }

    /**
     * Gets the value of the carDataPresent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCarDataPresent() {
        return carDataPresent;
    }

    /**
     * Sets the value of the carDataPresent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCarDataPresent(Boolean value) {
        this.carDataPresent = value;
    }

    /**
     * Gets the value of the carDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CarDetails }
     *     
     */
    public CarDetails getCarDetails() {
        return carDetails;
    }

    /**
     * Sets the value of the carDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarDetails }
     *     
     */
    public void setCarDetails(CarDetails value) {
        this.carDetails = value;
    }

    /**
     * Gets the value of the bookingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingStatus() {
        return bookingStatus;
    }

    /**
     * Sets the value of the bookingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingStatus(String value) {
        this.bookingStatus = value;
    }

}
