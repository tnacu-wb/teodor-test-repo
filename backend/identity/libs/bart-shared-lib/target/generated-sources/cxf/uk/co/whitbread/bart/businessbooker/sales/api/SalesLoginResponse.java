
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
 *         &lt;element name="SalesLoginResult" type="{http://corporate.micros.com/1.0}SalesLoginResponse"/&gt;
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
    "salesLoginResult"
})
@XmlRootElement(name = "SalesLoginResponse")
public class SalesLoginResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SalesLoginResult", required = true)
    protected SalesLoginResponse2 salesLoginResult;

    /**
     * Gets the value of the salesLoginResult property.
     * 
     * @return
     *     possible object is
     *     {@link SalesLoginResponse2 }
     *     
     */
    public SalesLoginResponse2 getSalesLoginResult() {
        return salesLoginResult;
    }

    /**
     * Sets the value of the salesLoginResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesLoginResponse2 }
     *     
     */
    public void setSalesLoginResult(SalesLoginResponse2 value) {
        this.salesLoginResult = value;
    }

}
