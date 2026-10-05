
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AlternativeRoom3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AlternativeRoom3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="lettingType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="totalCost" type="{http://bartws.micros.com/1.31}Price3"/&gt;
 *         &lt;element name="dailyRates" type="{http://bartws.micros.com/1.31}ArrayOfdailyRateDailyRate3"/&gt;
 *         &lt;element name="availabilityStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="ROOM_TYPE_GUARANTEED"/&gt;
 *               &lt;enumeration value="ROOM_TYPE_NOT_GUARANTEED"/&gt;
 *               &lt;enumeration value="ALTERNATIVE_ROOM_TYPE_OFFERED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="alternativeType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="ROOM_UPSELL"/&gt;
 *               &lt;enumeration value="ACCESSIBLE"/&gt;
 *               &lt;enumeration value="ACCESSIBLE_ROOM_UPSELL"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="numberAvailable" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlternativeRoom3", propOrder = {
    "lettingType",
    "totalCost",
    "dailyRates",
    "availabilityStatus",
    "alternativeType",
    "numberAvailable"
})
public class AlternativeRoom3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String lettingType;
    @XmlElement(required = true)
    protected Price3 totalCost;
    @XmlElement(required = true)
    protected ArrayOfdailyRateDailyRate3 dailyRates;
    protected String availabilityStatus;
    protected String alternativeType;
    protected Long numberAvailable;

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
     * Gets the value of the totalCost property.
     * 
     * @return
     *     possible object is
     *     {@link Price3 }
     *     
     */
    public Price3 getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the value of the totalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price3 }
     *     
     */
    public void setTotalCost(Price3 value) {
        this.totalCost = value;
    }

    /**
     * Gets the value of the dailyRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfdailyRateDailyRate3 }
     *     
     */
    public ArrayOfdailyRateDailyRate3 getDailyRates() {
        return dailyRates;
    }

    /**
     * Sets the value of the dailyRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfdailyRateDailyRate3 }
     *     
     */
    public void setDailyRates(ArrayOfdailyRateDailyRate3 value) {
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
     * Gets the value of the alternativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlternativeType() {
        return alternativeType;
    }

    /**
     * Sets the value of the alternativeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlternativeType(String value) {
        this.alternativeType = value;
    }

    /**
     * Gets the value of the numberAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumberAvailable() {
        return numberAvailable;
    }

    /**
     * Sets the value of the numberAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumberAvailable(Long value) {
        this.numberAvailable = value;
    }

}
