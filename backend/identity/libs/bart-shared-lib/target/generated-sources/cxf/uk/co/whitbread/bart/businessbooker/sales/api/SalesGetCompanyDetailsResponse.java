
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
 *         &lt;element name="SalesGetCompanyDetailsResult" type="{http://corporate.micros.com/1.0}SalesGetCompanyDetailsResponse"/&gt;
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
    "salesGetCompanyDetailsResult"
})
@XmlRootElement(name = "SalesGetCompanyDetailsResponse")
public class SalesGetCompanyDetailsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesGetCompanyDetailsResult", required = true)
    protected SalesGetCompanyDetailsResponse2 salesGetCompanyDetailsResult;

    /**
     * Gets the value of the salesGetCompanyDetailsResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesGetCompanyDetailsResponse2 }
     *     
     */
    public SalesGetCompanyDetailsResponse2 getSalesGetCompanyDetailsResult() {
        return salesGetCompanyDetailsResult;
    }

    /**
     * Sets the value of the salesGetCompanyDetailsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesGetCompanyDetailsResponse2 }
     *     
     */
    public void setSalesGetCompanyDetailsResult(SalesGetCompanyDetailsResponse2 value) {
        this.salesGetCompanyDetailsResult = value;
    }

}
