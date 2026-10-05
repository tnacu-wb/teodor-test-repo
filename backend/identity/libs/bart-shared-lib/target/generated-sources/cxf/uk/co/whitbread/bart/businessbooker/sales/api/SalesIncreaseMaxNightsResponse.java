
package uk.co.whitbread.bart.businessbooker.sales.api;

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
 *         &lt;element name="SalesIncreaseMaxNightsResult" type="{http://corporate.micros.com/1.0}SalesIncreaseMaxNightsResponse"/&gt;
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
    "salesIncreaseMaxNightsResult"
})
@XmlRootElement(name = "SalesIncreaseMaxNightsResponse")
public class SalesIncreaseMaxNightsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesIncreaseMaxNightsResult", required = true)
    protected SalesIncreaseMaxNightsResponse2 salesIncreaseMaxNightsResult;

    /**
     * Gets the value of the salesIncreaseMaxNightsResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesIncreaseMaxNightsResponse2 }
     *     
     */
    public SalesIncreaseMaxNightsResponse2 getSalesIncreaseMaxNightsResult() {
        return salesIncreaseMaxNightsResult;
    }

    /**
     * Sets the value of the salesIncreaseMaxNightsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesIncreaseMaxNightsResponse2 }
     *     
     */
    public void setSalesIncreaseMaxNightsResult(SalesIncreaseMaxNightsResponse2 value) {
        this.salesIncreaseMaxNightsResult = value;
    }

}
