
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
 *         &lt;element name="UpdateCompanyMIDetailsResult" type="{http://corporate.micros.com/1.0}UpdateCompanyMIDetailsResponse"/&gt;
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
    "updateCompanyMIDetailsResult"
})
@XmlRootElement(name = "UpdateCompanyMIDetailsResponse")
public class UpdateCompanyMIDetailsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UpdateCompanyMIDetailsResult", required = true)
    protected UpdateCompanyMIDetailsResponse2 updateCompanyMIDetailsResult;

    /**
     * Gets the value of the updateCompanyMIDetailsResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateCompanyMIDetailsResponse2 }
     *     
     */
    public UpdateCompanyMIDetailsResponse2 getUpdateCompanyMIDetailsResult() {
        return updateCompanyMIDetailsResult;
    }

    /**
     * Sets the value of the updateCompanyMIDetailsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateCompanyMIDetailsResponse2 }
     *     
     */
    public void setUpdateCompanyMIDetailsResult(UpdateCompanyMIDetailsResponse2 value) {
        this.updateCompanyMIDetailsResult = value;
    }

}
