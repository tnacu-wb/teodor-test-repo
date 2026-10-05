
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AvailabilityResponse3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AvailabilityResponse3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="availabilityResult" type="{http://bartws.micros.com/1.31}ArrayOfhotelAvailabilityResult3"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilityResponse3", propOrder = {
    "sessionID",
    "availabilityResult"
})
public class AvailabilityResponse3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected ArrayOfhotelAvailabilityResult3 availabilityResult;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the availabilityResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfhotelAvailabilityResult3 }
     *     
     */
    public ArrayOfhotelAvailabilityResult3 getAvailabilityResult() {
        return availabilityResult;
    }

    /**
     * Sets the value of the availabilityResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfhotelAvailabilityResult3 }
     *     
     */
    public void setAvailabilityResult(ArrayOfhotelAvailabilityResult3 value) {
        this.availabilityResult = value;
    }

}
