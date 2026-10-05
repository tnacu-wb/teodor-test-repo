
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
 *         &lt;element name="SalesFindEmployeeResult" type="{http://corporate.micros.com/1.0}SalesFindEmployeeResponse"/&gt;
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
    "salesFindEmployeeResult"
})
@XmlRootElement(name = "SalesFindEmployeeResponse")
public class SalesFindEmployeeResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesFindEmployeeResult", required = true)
    protected SalesFindEmployeeResponse2 salesFindEmployeeResult;

    /**
     * Gets the value of the salesFindEmployeeResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesFindEmployeeResponse2 }
     *     
     */
    public SalesFindEmployeeResponse2 getSalesFindEmployeeResult() {
        return salesFindEmployeeResult;
    }

    /**
     * Sets the value of the salesFindEmployeeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesFindEmployeeResponse2 }
     *     
     */
    public void setSalesFindEmployeeResult(SalesFindEmployeeResponse2 value) {
        this.salesFindEmployeeResult = value;
    }

}
