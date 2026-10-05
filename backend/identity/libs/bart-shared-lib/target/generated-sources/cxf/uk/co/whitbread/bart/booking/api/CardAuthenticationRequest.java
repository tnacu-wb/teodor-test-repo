
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
 *         &lt;element name="CardAuthenticationDetails" type="{http://bartws.micros.com/1.31}CardAuthenticationRequest3" minOccurs="0"/&gt;
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
    "cardAuthenticationDetails"
})
@XmlRootElement(name = "CardAuthenticationRequest")
public class CardAuthenticationRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CardAuthenticationDetails")
    protected CardAuthenticationRequest3 cardAuthenticationDetails;

    /**
     * Gets the value of the cardAuthenticationDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CardAuthenticationRequest3 }
     *     
     */
    public CardAuthenticationRequest3 getCardAuthenticationDetails() {
        return cardAuthenticationDetails;
    }

    /**
     * Sets the value of the cardAuthenticationDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardAuthenticationRequest3 }
     *     
     */
    public void setCardAuthenticationDetails(CardAuthenticationRequest3 value) {
        this.cardAuthenticationDetails = value;
    }

}
