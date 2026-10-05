
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
 *         &lt;element name="SalesUploadEmployeesResult" type="{http://corporate.micros.com/1.0}UploadEmployeesResponse"/&gt;
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
    "salesUploadEmployeesResult"
})
@XmlRootElement(name = "SalesUploadEmployeesResponse")
public class SalesUploadEmployeesResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesUploadEmployeesResult", required = true)
    protected UploadEmployeesResponse salesUploadEmployeesResult;

    /**
     * Gets the value of the salesUploadEmployeesResult property.
     * 
     * @return
     *     possible object is
     *     {@link UploadEmployeesResponse }
     *     
     */
    public UploadEmployeesResponse getSalesUploadEmployeesResult() {
        return salesUploadEmployeesResult;
    }

    /**
     * Sets the value of the salesUploadEmployeesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UploadEmployeesResponse }
     *     
     */
    public void setSalesUploadEmployeesResult(UploadEmployeesResponse value) {
        this.salesUploadEmployeesResult = value;
    }

}
