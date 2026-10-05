
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
 *         &lt;element name="CheckExistingCompanyResult" type="{http://corporate.micros.com/1.0}CheckExistingCompanyResponse"/&gt;
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
    "checkExistingCompanyResult"
})
@XmlRootElement(name = "CheckExistingCompanyResponse")
public class CheckExistingCompanyResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CheckExistingCompanyResult", required = true)
    protected CheckExistingCompanyResponse2 checkExistingCompanyResult;

    /**
     * Gets the value of the checkExistingCompanyResult property.
     * 
     * @return
     *     possible object is
     *     {@link CheckExistingCompanyResponse2 }
     *     
     */
    public CheckExistingCompanyResponse2 getCheckExistingCompanyResult() {
        return checkExistingCompanyResult;
    }

    /**
     * Sets the value of the checkExistingCompanyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckExistingCompanyResponse2 }
     *     
     */
    public void setCheckExistingCompanyResult(CheckExistingCompanyResponse2 value) {
        this.checkExistingCompanyResult = value;
    }

}
