
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
 *         &lt;element name="UnsubscribeResult" type="{http://bartws.micros.com/1.0}UnsubscribeResponse"/&gt;
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
    "unsubscribeResult"
})
@XmlRootElement(name = "UnsubscribeResponse")
public class UnsubscribeResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UnsubscribeResult", required = true)
    protected UnsubscribeResponse2 unsubscribeResult;

    /**
     * Gets the value of the unsubscribeResult property.
     * 
     * @return
     *     possible object is
     *     {@link UnsubscribeResponse2 }
     *     
     */
    public UnsubscribeResponse2 getUnsubscribeResult() {
        return unsubscribeResult;
    }

    /**
     * Sets the value of the unsubscribeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnsubscribeResponse2 }
     *     
     */
    public void setUnsubscribeResult(UnsubscribeResponse2 value) {
        this.unsubscribeResult = value;
    }

}
