
package uk.co.whitbread.bart.hub.inroomapp.api;

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
 *         &lt;element name="UpsellCheckResult" type="{http://hub.micros.com/1.0}UpsellCheckResponse"/&gt;
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
    "upsellCheckResult"
})
@XmlRootElement(name = "UpsellCheckResponse")
public class UpsellCheckResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UpsellCheckResult", required = true)
    protected UpsellCheckResponse2 upsellCheckResult;

    /**
     * Gets the value of the upsellCheckResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpsellCheckResponse2 }
     *     
     */
    public UpsellCheckResponse2 getUpsellCheckResult() {
        return upsellCheckResult;
    }

    /**
     * Sets the value of the upsellCheckResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsellCheckResponse2 }
     *     
     */
    public void setUpsellCheckResult(UpsellCheckResponse2 value) {
        this.upsellCheckResult = value;
    }

}
