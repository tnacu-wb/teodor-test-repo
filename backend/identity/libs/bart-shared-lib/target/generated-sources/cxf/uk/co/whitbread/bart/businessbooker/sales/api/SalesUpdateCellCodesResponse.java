
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
 *         &lt;element name="SalesUpdateCellCodesResult" type="{http://corporate.micros.com/1.0}SalesUpdateCellCodesResponse"/&gt;
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
    "salesUpdateCellCodesResult"
})
@XmlRootElement(name = "SalesUpdateCellCodesResponse")
public class SalesUpdateCellCodesResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesUpdateCellCodesResult", required = true)
    protected SalesUpdateCellCodesResponse2 salesUpdateCellCodesResult;

    /**
     * Gets the value of the salesUpdateCellCodesResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesUpdateCellCodesResponse2 }
     *     
     */
    public SalesUpdateCellCodesResponse2 getSalesUpdateCellCodesResult() {
        return salesUpdateCellCodesResult;
    }

    /**
     * Sets the value of the salesUpdateCellCodesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesUpdateCellCodesResponse2 }
     *     
     */
    public void setSalesUpdateCellCodesResult(SalesUpdateCellCodesResponse2 value) {
        this.salesUpdateCellCodesResult = value;
    }

}
