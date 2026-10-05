
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
 *         &lt;element name="AvailabilityRequestResult" type="{http://bartws.micros.com/1.31}AvailabilityResponse3"/&gt;
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
    "availabilityRequestResult"
})
@XmlRootElement(name = "AvailabilityRequestResponse")
public class AvailabilityRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "AvailabilityRequestResult", required = true)
    protected AvailabilityResponse3 availabilityRequestResult;

    /**
     * Gets the value of the availabilityRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link AvailabilityResponse3 }
     *     
     */
    public AvailabilityResponse3 getAvailabilityRequestResult() {
        return availabilityRequestResult;
    }

    /**
     * Sets the value of the availabilityRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailabilityResponse3 }
     *     
     */
    public void setAvailabilityRequestResult(AvailabilityResponse3 value) {
        this.availabilityRequestResult = value;
    }

}
