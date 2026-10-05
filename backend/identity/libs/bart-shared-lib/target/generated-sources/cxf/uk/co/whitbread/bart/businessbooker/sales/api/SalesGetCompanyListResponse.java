
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
 *         &lt;element name="SalesGetCompanyListResult" type="{http://corporate.micros.com/1.0}SalesGetCompanyListResponse"/&gt;
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
    "salesGetCompanyListResult"
})
@XmlRootElement(name = "SalesGetCompanyListResponse")
public class SalesGetCompanyListResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesGetCompanyListResult", required = true)
    protected SalesGetCompanyListResponse2 salesGetCompanyListResult;

    /**
     * Gets the value of the salesGetCompanyListResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesGetCompanyListResponse2 }
     *     
     */
    public SalesGetCompanyListResponse2 getSalesGetCompanyListResult() {
        return salesGetCompanyListResult;
    }

    /**
     * Sets the value of the salesGetCompanyListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesGetCompanyListResponse2 }
     *     
     */
    public void setSalesGetCompanyListResult(SalesGetCompanyListResponse2 value) {
        this.salesGetCompanyListResult = value;
    }

}
