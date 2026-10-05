
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AvailabilityUpdateDetails" type="{http://bartws.micros.com/1.31}AvailabilityUpdateRequest" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "availabilityUpdateDetails"
})
@XmlRootElement(name = "AvailabilityUpdateRequest")
public class AvailabilityUpdateRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "AvailabilityUpdateDetails")
    protected AvailabilityUpdateRequest2 availabilityUpdateDetails;

    /**
     * Gets the value of the availabilityUpdateDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AvailabilityUpdateRequest2 }
     *     
     */
    public AvailabilityUpdateRequest2 getAvailabilityUpdateDetails() {
        return availabilityUpdateDetails;
    }

    /**
     * Sets the value of the availabilityUpdateDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailabilityUpdateRequest2 }
     *     
     */
    public void setAvailabilityUpdateDetails(AvailabilityUpdateRequest2 value) {
        this.availabilityUpdateDetails = value;
    }

}
