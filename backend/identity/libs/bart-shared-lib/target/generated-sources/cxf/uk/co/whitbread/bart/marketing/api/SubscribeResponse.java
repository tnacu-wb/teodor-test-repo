
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
 *         &lt;element name="SubscribeResult" type="{http://bartws.micros.com/1.0}SubscriptionResponse"/&gt;
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
    "subscribeResult"
})
@XmlRootElement(name = "SubscribeResponse")
public class SubscribeResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SubscribeResult", required = true)
    protected SubscriptionResponse subscribeResult;

    /**
     * Gets the value of the subscribeResult property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionResponse }
     *     
     */
    public SubscriptionResponse getSubscribeResult() {
        return subscribeResult;
    }

    /**
     * Sets the value of the subscribeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionResponse }
     *     
     */
    public void setSubscribeResult(SubscriptionResponse value) {
        this.subscribeResult = value;
    }

}
