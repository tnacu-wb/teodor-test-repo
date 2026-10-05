
package uk.co.whitbread.bart.checkbookingconfirmation.api;

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
 *         &lt;element name="CheckBookingConfirmationRequest" type="{http://bartws.micros.com/1.4}CheckBookingConfirmationRequest" minOccurs="0"/&gt;
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
    "checkBookingConfirmationRequest"
})
@XmlRootElement(name = "CheckBookingConfirmationRequest")
public class CheckBookingConfirmationRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CheckBookingConfirmationRequest")
    protected CheckBookingConfirmationRequest2 checkBookingConfirmationRequest;

    /**
     * Gets the value of the checkBookingConfirmationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CheckBookingConfirmationRequest2 }
     *     
     */
    public CheckBookingConfirmationRequest2 getCheckBookingConfirmationRequest() {
        return checkBookingConfirmationRequest;
    }

    /**
     * Sets the value of the checkBookingConfirmationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckBookingConfirmationRequest2 }
     *     
     */
    public void setCheckBookingConfirmationRequest(CheckBookingConfirmationRequest2 value) {
        this.checkBookingConfirmationRequest = value;
    }

}
