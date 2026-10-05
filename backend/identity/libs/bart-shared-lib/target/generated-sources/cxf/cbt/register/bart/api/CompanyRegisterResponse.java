
package cbt.register.bart.api;

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
 *         &lt;element name="CompanyRegisterResult" type="{http://corporate.micros.com/1.0}CompanyRegisterResponse"/&gt;
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
    "companyRegisterResult"
})
@XmlRootElement(name = "CompanyRegisterResponse")
public class CompanyRegisterResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CompanyRegisterResult", required = true)
    protected CompanyRegisterResponse2 companyRegisterResult;

    /**
     * Gets the value of the companyRegisterResult property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyRegisterResponse2 }
     *     
     */
    public CompanyRegisterResponse2 getCompanyRegisterResult() {
        return companyRegisterResult;
    }

    /**
     * Sets the value of the companyRegisterResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyRegisterResponse2 }
     *     
     */
    public void setCompanyRegisterResult(CompanyRegisterResponse2 value) {
        this.companyRegisterResult = value;
    }

}
