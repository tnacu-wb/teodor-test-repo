
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
 *         &lt;element name="SalesUpdateMarketingResult" type="{http://corporate.micros.com/1.0}SalesUpdateMarketingResponse"/&gt;
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
    "salesUpdateMarketingResult"
})
@XmlRootElement(name = "SalesUpdateMarketingResponse")
public class SalesUpdateMarketingResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesUpdateMarketingResult", required = true)
    protected SalesUpdateMarketingResponse2 salesUpdateMarketingResult;

    /**
     * Gets the value of the salesUpdateMarketingResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesUpdateMarketingResponse2 }
     *     
     */
    public SalesUpdateMarketingResponse2 getSalesUpdateMarketingResult() {
        return salesUpdateMarketingResult;
    }

    /**
     * Sets the value of the salesUpdateMarketingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesUpdateMarketingResponse2 }
     *     
     */
    public void setSalesUpdateMarketingResult(SalesUpdateMarketingResponse2 value) {
        this.salesUpdateMarketingResult = value;
    }

}
