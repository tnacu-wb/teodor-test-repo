
package uk.co.whitbread.bart.cancellation.api;

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
 *         &lt;element name="CancellationRequest" type="{http://bartws.micros.com/1.2}CancellationRequest" minOccurs="0"/&gt;
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
    "cancellationRequest"
})
@XmlRootElement(name = "CancellationRequest")
public class CancellationRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CancellationRequest")
    protected CancellationRequest2 cancellationRequest;

    /**
     * Gets the value of the cancellationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CancellationRequest2 }
     *     
     */
    public CancellationRequest2 getCancellationRequest() {
        return cancellationRequest;
    }

    /**
     * Sets the value of the cancellationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CancellationRequest2 }
     *     
     */
    public void setCancellationRequest(CancellationRequest2 value) {
        this.cancellationRequest = value;
    }

}
