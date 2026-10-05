
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
 *         &lt;element name="SalesUpdateRestrictedRatesResult" type="{http://corporate.micros.com/1.0}SalesUpdateRestrictedRatesResponse"/&gt;
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
    "salesUpdateRestrictedRatesResult"
})
@XmlRootElement(name = "SalesUpdateRestrictedRatesResponse")
public class SalesUpdateRestrictedRatesResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesUpdateRestrictedRatesResult", required = true)
    protected SalesUpdateRestrictedRatesResponse2 salesUpdateRestrictedRatesResult;

    /**
     * Gets the value of the salesUpdateRestrictedRatesResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesUpdateRestrictedRatesResponse2 }
     *     
     */
    public SalesUpdateRestrictedRatesResponse2 getSalesUpdateRestrictedRatesResult() {
        return salesUpdateRestrictedRatesResult;
    }

    /**
     * Sets the value of the salesUpdateRestrictedRatesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesUpdateRestrictedRatesResponse2 }
     *     
     */
    public void setSalesUpdateRestrictedRatesResult(SalesUpdateRestrictedRatesResponse2 value) {
        this.salesUpdateRestrictedRatesResult = value;
    }

}
