
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AvailableRooms complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AvailableRooms"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="adults"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *               &lt;minInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="children"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="roomID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lettingType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="roomType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="FAM"/&gt;
 *               &lt;enumeration value="DB"/&gt;
 *               &lt;enumeration value="TWIN"/&gt;
 *               &lt;enumeration value="DIS"/&gt;
 *               &lt;enumeration value="SB"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cotRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="totalCost" type="{http://bartws.micros.com/1.31}Price"/&gt;
 *         &lt;element name="dailyRates" type="{http://bartws.micros.com/1.31}ArrayOfdailyRateDailyRate"/&gt;
 *         &lt;element name="availabilityStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="ROOM_TYPE_GUARANTEED"/&gt;
 *               &lt;enumeration value="ROOM_TYPE_NOT_GUARANTEED"/&gt;
 *               &lt;enumeration value="ALTERNATIVE_ROOM_TYPE_OFFERED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="alternativeRoom" type="{http://bartws.micros.com/1.31}AlternativeRoom" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailableRooms", propOrder = {
    "adults",
    "children",
    "roomNumber",
    "roomID",
    "lettingType",
    "roomType",
    "cotRequired",
    "totalCost",
    "dailyRates",
    "availabilityStatus",
    "alternativeRoom"
})
public class AvailableRooms
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected int adults;
    protected int children;
    protected Long roomNumber;
    protected String roomID;
    @XmlElement(required = true)
    protected String lettingType;
    protected String roomType;
    protected Boolean cotRequired;
    @XmlElement(required = true)
    protected Price totalCost;
    @XmlElement(required = true)
    protected ArrayOfdailyRateDailyRate dailyRates;
    protected String availabilityStatus;
    protected AlternativeRoom alternativeRoom;

    /**
     * Gets the value of the adults property.
     * 
     */
    public int getAdults() {
        return adults;
    }

    /**
     * Sets the value of the adults property.
     * 
     */
    public void setAdults(int value) {
        this.adults = value;
    }

    /**
     * Gets the value of the children property.
     * 
     */
    public int getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     */
    public void setChildren(int value) {
        this.children = value;
    }

    /**
     * Gets the value of the roomNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the value of the roomNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRoomNumber(Long value) {
        this.roomNumber = value;
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
     * Gets the value of the lettingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLettingType() {
        return lettingType;
    }

    /**
     * Sets the value of the lettingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLettingType(String value) {
        this.lettingType = value;
    }

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
     * Gets the value of the cotRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCotRequired() {
        return cotRequired;
    }

    /**
     * Sets the value of the cotRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCotRequired(Boolean value) {
        this.cotRequired = value;
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
     * Gets the value of the dailyRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfdailyRateDailyRate }
     *     
     */
    public ArrayOfdailyRateDailyRate getDailyRates() {
        return dailyRates;
    }

    /**
     * Sets the value of the dailyRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfdailyRateDailyRate }
     *     
     */
    public void setDailyRates(ArrayOfdailyRateDailyRate value) {
        this.dailyRates = value;
    }

    /**
     * Gets the value of the availabilityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    /**
     * Sets the value of the availabilityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailabilityStatus(String value) {
        this.availabilityStatus = value;
    }

    /**
     * Gets the value of the alternativeRoom property.
     * 
     * @return
     *     possible object is
     *     {@link AlternativeRoom }
     *     
     */
    public AlternativeRoom getAlternativeRoom() {
        return alternativeRoom;
    }

    /**
     * Sets the value of the alternativeRoom property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlternativeRoom }
     *     
     */
    public void setAlternativeRoom(AlternativeRoom value) {
        this.alternativeRoom = value;
    }

}
