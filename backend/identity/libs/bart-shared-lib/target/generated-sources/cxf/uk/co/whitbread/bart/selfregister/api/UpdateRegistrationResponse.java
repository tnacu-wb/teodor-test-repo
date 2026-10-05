
package uk.co.whitbread.bart.selfregister.api;

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
 *         &lt;element name="UpdateRegistrationResult" type="{http://corporate.micros.com}UpdateRegistrationResponse"/&gt;
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
    "updateRegistrationResult"
})
@XmlRootElement(name = "UpdateRegistrationResponse")
public class UpdateRegistrationResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UpdateRegistrationResult", required = true)
    protected UpdateRegistrationResponse2 updateRegistrationResult;

    /**
     * Gets the value of the updateRegistrationResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateRegistrationResponse2 }
     *     
     */
    public UpdateRegistrationResponse2 getUpdateRegistrationResult() {
        return updateRegistrationResult;
    }

    /**
     * Sets the value of the updateRegistrationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateRegistrationResponse2 }
     *     
     */
    public void setUpdateRegistrationResult(UpdateRegistrationResponse2 value) {
        this.updateRegistrationResult = value;
    }

}
