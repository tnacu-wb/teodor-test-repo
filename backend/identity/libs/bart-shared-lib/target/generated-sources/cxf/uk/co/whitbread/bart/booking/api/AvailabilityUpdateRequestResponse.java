
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
 *         &lt;element name="AvailabilityUpdateRequestResult" type="{http://bartws.micros.com/1.31}AvailabilityUpdateResponse"/&gt;
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
    "availabilityUpdateRequestResult"
})
@XmlRootElement(name = "AvailabilityUpdateRequestResponse")
public class AvailabilityUpdateRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "AvailabilityUpdateRequestResult", required = true)
    protected AvailabilityUpdateResponse availabilityUpdateRequestResult;

    /**
     * Gets the value of the availabilityUpdateRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link AvailabilityUpdateResponse }
     *     
     */
    public AvailabilityUpdateResponse getAvailabilityUpdateRequestResult() {
        return availabilityUpdateRequestResult;
    }

    /**
     * Sets the value of the availabilityUpdateRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailabilityUpdateResponse }
     *     
     */
    public void setAvailabilityUpdateRequestResult(AvailabilityUpdateResponse value) {
        this.availabilityUpdateRequestResult = value;
    }

}
