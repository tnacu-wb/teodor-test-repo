
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
 *         &lt;element name="SalesMimicCompanyResult" type="{http://corporate.micros.com/1.0}SalesMimicCompanyResponse"/&gt;
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
    "salesMimicCompanyResult"
})
@XmlRootElement(name = "SalesMimicCompanyResponse")
public class SalesMimicCompanyResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesMimicCompanyResult", required = true)
    protected SalesMimicCompanyResponse2 salesMimicCompanyResult;

    /**
     * Gets the value of the salesMimicCompanyResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesMimicCompanyResponse2 }
     *     
     */
    public SalesMimicCompanyResponse2 getSalesMimicCompanyResult() {
        return salesMimicCompanyResult;
    }

    /**
     * Sets the value of the salesMimicCompanyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesMimicCompanyResponse2 }
     *     
     */
    public void setSalesMimicCompanyResult(SalesMimicCompanyResponse2 value) {
        this.salesMimicCompanyResult = value;
    }

}
