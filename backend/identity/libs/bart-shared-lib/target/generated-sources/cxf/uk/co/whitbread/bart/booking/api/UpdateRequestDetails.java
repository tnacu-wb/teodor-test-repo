
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for UpdateRequestDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpdateRequestDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="rateCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cellCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="noOfRooms" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="roomDetails" type="{http://bartws.micros.com/1.31}ArrayOfroomAvailableRooms"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateRequestDetails", propOrder = {
    "confirmationNumber",
    "hotelCode",
    "arrivalDate",
    "departureDate",
    "rateCode",
    "cellCode",
    "noOfRooms",
    "roomDetails"
})
public class UpdateRequestDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String confirmationNumber;
    @XmlElement(required = true)
    protected String hotelCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate arrivalDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate departureDate;
    @XmlElement(required = true)
    protected String rateCode;
    protected String cellCode;
    protected long noOfRooms;
    @XmlElement(required = true)
    protected ArrayOfroomAvailableRooms roomDetails;

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
     * Gets the value of the rateCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateCode() {
        return rateCode;
    }

    /**
     * Sets the value of the rateCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateCode(String value) {
        this.rateCode = value;
    }

    /**
     * Gets the value of the cellCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellCode() {
        return cellCode;
    }

    /**
     * Sets the value of the cellCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellCode(String value) {
        this.cellCode = value;
    }

    /**
     * Gets the value of the noOfRooms property.
     * 
     */
    public long getNoOfRooms() {
        return noOfRooms;
    }

    /**
     * Sets the value of the noOfRooms property.
     * 
     */
    public void setNoOfRooms(long value) {
        this.noOfRooms = value;
    }

    /**
     * Gets the value of the roomDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfroomAvailableRooms }
     *     
     */
    public ArrayOfroomAvailableRooms getRoomDetails() {
        return roomDetails;
    }

    /**
     * Sets the value of the roomDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfroomAvailableRooms }
     *     
     */
    public void setRoomDetails(ArrayOfroomAvailableRooms value) {
        this.roomDetails = value;
    }

}
