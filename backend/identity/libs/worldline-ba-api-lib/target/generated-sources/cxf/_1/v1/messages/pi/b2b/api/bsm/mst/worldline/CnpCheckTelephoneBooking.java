
package _1.v1.messages.pi.b2b.api.bsm.mst.worldline;

import _1.v1.data.pi.b2b.api.bsm.mst.worldline.CNPCheckTelephoneBookingRequestType;
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
 *         &lt;element name="Request" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CNPCheckTelephoneBookingRequestType"/&gt;
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
    "request"
})
@XmlRootElement(name = "cnpCheckTelephoneBooking")
public class CnpCheckTelephoneBooking {

    @XmlElement(name = "Request", required = true)
    protected CNPCheckTelephoneBookingRequestType request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link CNPCheckTelephoneBookingRequestType }
     *     
     */
    public CNPCheckTelephoneBookingRequestType getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link CNPCheckTelephoneBookingRequestType }
     *     
     */
    public void setRequest(CNPCheckTelephoneBookingRequestType value) {
        this.request = value;
    }

}
