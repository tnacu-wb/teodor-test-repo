
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
 *         &lt;element name="SalesSetEmployeeStatusResult" type="{http://corporate.micros.com/1.0}SalesSetEmployeeStatusResponse"/&gt;
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
    "salesSetEmployeeStatusResult"
})
@XmlRootElement(name = "SalesSetEmployeeStatusResponse")
public class SalesSetEmployeeStatusResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesSetEmployeeStatusResult", required = true)
    protected SalesSetEmployeeStatusResponse2 salesSetEmployeeStatusResult;

    /**
     * Gets the value of the salesSetEmployeeStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesSetEmployeeStatusResponse2 }
     *     
     */
    public SalesSetEmployeeStatusResponse2 getSalesSetEmployeeStatusResult() {
        return salesSetEmployeeStatusResult;
    }

    /**
     * Sets the value of the salesSetEmployeeStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesSetEmployeeStatusResponse2 }
     *     
     */
    public void setSalesSetEmployeeStatusResult(SalesSetEmployeeStatusResponse2 value) {
        this.salesSetEmployeeStatusResult = value;
    }

}
