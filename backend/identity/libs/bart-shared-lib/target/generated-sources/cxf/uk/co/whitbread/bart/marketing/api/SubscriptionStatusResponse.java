
package uk.co.whitbread.bart.marketing.api;

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
 *         &lt;element name="SubscriptionStatusResult" type="{http://bartws.micros.com/1.0}SubscriptionStatusResponse2015"/&gt;
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
    "subscriptionStatusResult"
})
@XmlRootElement(name = "SubscriptionStatusResponse")
public class SubscriptionStatusResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SubscriptionStatusResult", required = true)
    protected SubscriptionStatusResponse2015 subscriptionStatusResult;

    /**
     * Gets the value of the subscriptionStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionStatusResponse2015 }
     *     
     */
    public SubscriptionStatusResponse2015 getSubscriptionStatusResult() {
        return subscriptionStatusResult;
    }

    /**
     * Sets the value of the subscriptionStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionStatusResponse2015 }
     *     
     */
    public void setSubscriptionStatusResult(SubscriptionStatusResponse2015 value) {
        this.subscriptionStatusResult = value;
    }

}
