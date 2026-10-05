
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PastBooking complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PastBooking"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="guestHistoryNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guestHistoryRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bookingID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bookedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rateName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="totalCost" type="{http://corporate.micros.com/1.0}Price" minOccurs="0"/&gt;
 *         &lt;element name="bookingFee" type="{http://corporate.micros.com/1.0}Price" minOccurs="0"/&gt;
 *         &lt;element name="paymentStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rooms" type="{http://corporate.micros.com/1.0}ArrayOfRoomRoom" minOccurs="0"/&gt;
 *         &lt;element name="isMPIBooking" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PastBooking", propOrder = {
    "hotelCode",
    "guestHistoryNumber",
    "guestHistoryRecordNumber",
    "bookingID",
    "arrivalDate",
    "departureDate",
    "bookedBy",
    "rateName",
    "totalCost",
    "bookingFee",
    "paymentStatus",
    "rooms",
    "isMPIBooking"
})
public class PastBooking
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String hotelCode;
    protected String guestHistoryNumber;
    protected String guestHistoryRecordNumber;
    protected String bookingID;
    protected String arrivalDate;
    protected String departureDate;
    protected String bookedBy;
    protected String rateName;
    protected Price totalCost;
    protected Price bookingFee;
    protected String paymentStatus;
    protected ArrayOfRoomRoom rooms;
    protected boolean isMPIBooking;

    /**
     * Gets the value of the hotelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelCode() {
        return hotelCode;
    }

    /**
     * Sets the value of the hotelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelCode(String value) {
        this.hotelCode = value;
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
     * Gets the value of the guestHistoryRecordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestHistoryRecordNumber() {
        return guestHistoryRecordNumber;
    }

    /**
     * Sets the value of the guestHistoryRecordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestHistoryRecordNumber(String value) {
        this.guestHistoryRecordNumber = value;
    }

    /**
     * Gets the value of the bookingID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingID() {
        return bookingID;
    }

    /**
     * Sets the value of the bookingID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingID(String value) {
        this.bookingID = value;
    }

    /**
     * Gets the value of the arrivalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalDate() {
        return arrivalDate;
    }

    /**
     * Sets the value of the arrivalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalDate(String value) {
        this.arrivalDate = value;
    }

    /**
     * Gets the value of the departureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the value of the departureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDate(String value) {
        this.departureDate = value;
    }

    /**
     * Gets the value of the bookedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookedBy() {
        return bookedBy;
    }

    /**
     * Sets the value of the bookedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookedBy(String value) {
        this.bookedBy = value;
    }

    /**
     * Gets the value of the rateName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateName() {
        return rateName;
    }

    /**
     * Sets the value of the rateName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateName(String value) {
        this.rateName = value;
    }

    /**
     * Gets the value of the totalCost property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the value of the totalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setTotalCost(Price value) {
        this.totalCost = value;
    }

    /**
     * Gets the value of the bookingFee property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getBookingFee() {
        return bookingFee;
    }

    /**
     * Sets the value of the bookingFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setBookingFee(Price value) {
        this.bookingFee = value;
    }

    /**
     * Gets the value of the paymentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the value of the paymentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentStatus(String value) {
        this.paymentStatus = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRoomRoom }
     *     
     */
    public ArrayOfRoomRoom getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRoomRoom }
     *     
     */
    public void setRooms(ArrayOfRoomRoom value) {
        this.rooms = value;
    }

    /**
     * Gets the value of the isMPIBooking property.
     * 
     */
    public boolean isIsMPIBooking() {
        return isMPIBooking;
    }

    /**
     * Sets the value of the isMPIBooking property.
     * 
     */
    public void setIsMPIBooking(boolean value) {
        this.isMPIBooking = value;
    }

}
