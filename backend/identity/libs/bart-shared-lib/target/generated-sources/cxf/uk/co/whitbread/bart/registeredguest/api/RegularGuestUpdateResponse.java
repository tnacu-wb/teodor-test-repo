
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
 *         &lt;element name="RegularGuestUpdateResult" type="{http://bartws.micros.com/1.13}RegularGuestUpdateResponse"/&gt;
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
    "regularGuestUpdateResult"
})
@XmlRootElement(name = "RegularGuestUpdateResponse")
public class RegularGuestUpdateResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RegularGuestUpdateResult", required = true)
    protected RegularGuestUpdateResponse2 regularGuestUpdateResult;

    /**
     * Gets the value of the regularGuestUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link RegularGuestUpdateResponse2 }
     *     
     */
    public RegularGuestUpdateResponse2 getRegularGuestUpdateResult() {
        return regularGuestUpdateResult;
    }

    /**
     * Sets the value of the regularGuestUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegularGuestUpdateResponse2 }
     *     
     */
    public void setRegularGuestUpdateResult(RegularGuestUpdateResponse2 value) {
        this.regularGuestUpdateResult = value;
    }

}
