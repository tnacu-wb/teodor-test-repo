
package uk.co.whitbread.bart.registeredguest.api;

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
 *         &lt;element name="RegisteredGuestUpdateDetails" type="{http://bartws.micros.com/1.13}RegisteredGuestUpdateRequest" minOccurs="0"/&gt;
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
    "registeredGuestUpdateDetails"
})
@XmlRootElement(name = "RegisteredGuestUpdateRequest")
public class RegisteredGuestUpdateRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RegisteredGuestUpdateDetails")
    protected RegisteredGuestUpdateRequest2 registeredGuestUpdateDetails;

    /**
     * Gets the value of the registeredGuestUpdateDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RegisteredGuestUpdateRequest2 }
     *     
     */
    public RegisteredGuestUpdateRequest2 getRegisteredGuestUpdateDetails() {
        return registeredGuestUpdateDetails;
    }

    /**
     * Sets the value of the registeredGuestUpdateDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisteredGuestUpdateRequest2 }
     *     
     */
    public void setRegisteredGuestUpdateDetails(RegisteredGuestUpdateRequest2 value) {
        this.registeredGuestUpdateDetails = value;
    }

}
