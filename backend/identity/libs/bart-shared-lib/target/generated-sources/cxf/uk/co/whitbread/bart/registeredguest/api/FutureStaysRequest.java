
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
 *         &lt;element name="FutureStaysDetails" type="{http://bartws.micros.com/1.13}FutureStaysRequest" minOccurs="0"/&gt;
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
    "futureStaysDetails"
})
@XmlRootElement(name = "FutureStaysRequest")
public class FutureStaysRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "FutureStaysDetails")
    protected FutureStaysRequest2 futureStaysDetails;

    /**
     * Gets the value of the futureStaysDetails property.
     * 
     * @return
     *     possible object is
     *     {@link FutureStaysRequest2 }
     *     
     */
    public FutureStaysRequest2 getFutureStaysDetails() {
        return futureStaysDetails;
    }

    /**
     * Sets the value of the futureStaysDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link FutureStaysRequest2 }
     *     
     */
    public void setFutureStaysDetails(FutureStaysRequest2 value) {
        this.futureStaysDetails = value;
    }

}
