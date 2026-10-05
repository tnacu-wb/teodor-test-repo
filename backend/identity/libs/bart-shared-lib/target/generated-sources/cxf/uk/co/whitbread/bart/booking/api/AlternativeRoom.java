
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AlternativeRoom complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AlternativeRoom"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="lettingType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlternativeRoom", propOrder = {
    "lettingType",
    "totalCost",
    "dailyRates",
    "availabilityStatus"
})
public class AlternativeRoom
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String lettingType;
    @XmlElement(required = true)
    protected Price totalCost;
    @XmlElement(required = true)
    protected ArrayOfdailyRateDailyRate dailyRates;
    protected String availabilityStatus;

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

}
