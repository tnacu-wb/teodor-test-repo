
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
 *         &lt;element name="CardAuthenticationRequestResult" type="{http://bartws.micros.com/1.31}CardAuthenticationResponse3"/&gt;
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
    "cardAuthenticationRequestResult"
})
@XmlRootElement(name = "CardAuthenticationRequestResponse")
public class CardAuthenticationRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CardAuthenticationRequestResult", required = true)
    protected CardAuthenticationResponse3 cardAuthenticationRequestResult;

    /**
     * Gets the value of the cardAuthenticationRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link CardAuthenticationResponse3 }
     *     
     */
    public CardAuthenticationResponse3 getCardAuthenticationRequestResult() {
        return cardAuthenticationRequestResult;
    }

    /**
     * Sets the value of the cardAuthenticationRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardAuthenticationResponse3 }
     *     
     */
    public void setCardAuthenticationRequestResult(CardAuthenticationResponse3 value) {
        this.cardAuthenticationRequestResult = value;
    }

}
