
package uk.co.whitbread.bart.business.api;

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
 *         &lt;element name="GetEmployeeListResult" type="{http://corporate.micros.com/1.0}GetEmployeeListResponse"/&gt;
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
    "getEmployeeListResult"
})
@XmlRootElement(name = "GetEmployeeListResponse")
public class GetEmployeeListResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "GetEmployeeListResult", required = true)
    protected GetEmployeeListResponse2 getEmployeeListResult;

    /**
     * Gets the value of the getEmployeeListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetEmployeeListResponse2 }
     *     
     */
    public GetEmployeeListResponse2 getGetEmployeeListResult() {
        return getEmployeeListResult;
    }

    /**
     * Sets the value of the getEmployeeListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetEmployeeListResponse2 }
     *     
     */
    public void setGetEmployeeListResult(GetEmployeeListResponse2 value) {
        this.getEmployeeListResult = value;
    }

}
