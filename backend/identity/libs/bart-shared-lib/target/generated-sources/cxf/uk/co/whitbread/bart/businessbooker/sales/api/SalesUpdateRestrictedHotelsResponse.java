
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
 *         &lt;element name="SalesUpdateRestrictedHotelsResult" type="{http://corporate.micros.com/1.0}SalesUpdateRestrictedHotelsResponse"/&gt;
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
    "salesUpdateRestrictedHotelsResult"
})
@XmlRootElement(name = "SalesUpdateRestrictedHotelsResponse")
public class SalesUpdateRestrictedHotelsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesUpdateRestrictedHotelsResult", required = true)
    protected SalesUpdateRestrictedHotelsResponse2 salesUpdateRestrictedHotelsResult;

    /**
     * Gets the value of the salesUpdateRestrictedHotelsResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesUpdateRestrictedHotelsResponse2 }
     *     
     */
    public SalesUpdateRestrictedHotelsResponse2 getSalesUpdateRestrictedHotelsResult() {
        return salesUpdateRestrictedHotelsResult;
    }

    /**
     * Sets the value of the salesUpdateRestrictedHotelsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesUpdateRestrictedHotelsResponse2 }
     *     
     */
    public void setSalesUpdateRestrictedHotelsResult(SalesUpdateRestrictedHotelsResponse2 value) {
        this.salesUpdateRestrictedHotelsResult = value;
    }

}
