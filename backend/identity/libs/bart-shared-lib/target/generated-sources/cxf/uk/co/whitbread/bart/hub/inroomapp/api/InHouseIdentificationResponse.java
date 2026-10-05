
package uk.co.whitbread.bart.hub.inroomapp.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for InHouseIdentificationResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="InHouseIdentificationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="guests" type="{http://hub.micros.com/1.0}ArrayOfGuestGuest"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="QRCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="upsellItems" type="{http://hub.micros.com/1.0}ArrayOfupsellItemUpsellItem"/&gt;
 *         &lt;element name="payment" type="{http://hub.micros.com/1.0}Payment"/&gt;
 *         &lt;element name="validUpsellItems" type="{http://hub.micros.com/1.0}ArrayOfvalidUpsellItemValidUpsellItem"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="errors" type="{http://hub.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InHouseIdentificationResponse", propOrder = {
    "guests",
    "arrivalDate",
    "departureDate",
    "hotelCode",
    "roomNumber",
    "qrCode",
    "upsellItems",
    "payment",
    "validUpsellItems",
    "success",
    "errors"
})
public class InHouseIdentificationResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfGuestGuest guests;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate arrivalDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate departureDate;
    @XmlElement(required = true)
    protected String hotelCode;
    @XmlElement(required = true)
    protected String roomNumber;
    @XmlElement(name = "QRCode", required = true)
    protected String qrCode;
    @XmlElement(required = true)
    protected ArrayOfupsellItemUpsellItem upsellItems;
    @XmlElement(required = true)
    protected Payment payment;
    @XmlElement(required = true)
    protected ArrayOfvalidUpsellItemValidUpsellItem validUpsellItems;
    protected boolean success;
    protected ArrayOferrorError errors;

    /**
     * Gets the value of the guests property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfGuestGuest }
     *     
     */
    public ArrayOfGuestGuest getGuests() {
        return guests;
    }

    /**
     * Sets the value of the guests property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfGuestGuest }
     *     
     */
    public void setGuests(ArrayOfGuestGuest value) {
        this.guests = value;
    }

    /**
     * Gets the value of the arrivalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getArrivalDate() {
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
    public void setArrivalDate(LocalDate value) {
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
    public LocalDate getDepartureDate() {
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
    public void setDepartureDate(LocalDate value) {
        this.departureDate = value;
    }

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
     * Gets the value of the roomNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the value of the roomNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomNumber(String value) {
        this.roomNumber = value;
    }

    /**
     * Gets the value of the qrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQRCode() {
        return qrCode;
    }

    /**
     * Sets the value of the qrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQRCode(String value) {
        this.qrCode = value;
    }

    /**
     * Gets the value of the upsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public ArrayOfupsellItemUpsellItem getUpsellItems() {
        return upsellItems;
    }

    /**
     * Sets the value of the upsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public void setUpsellItems(ArrayOfupsellItemUpsellItem value) {
        this.upsellItems = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setPayment(Payment value) {
        this.payment = value;
    }

    /**
     * Gets the value of the validUpsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfvalidUpsellItemValidUpsellItem }
     *     
     */
    public ArrayOfvalidUpsellItemValidUpsellItem getValidUpsellItems() {
        return validUpsellItems;
    }

    /**
     * Sets the value of the validUpsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfvalidUpsellItemValidUpsellItem }
     *     
     */
    public void setValidUpsellItems(ArrayOfvalidUpsellItemValidUpsellItem value) {
        this.validUpsellItems = value;
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOferrorError }
     *     
     */
    public ArrayOferrorError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOferrorError }
     *     
     */
    public void setErrors(ArrayOferrorError value) {
        this.errors = value;
    }

}
