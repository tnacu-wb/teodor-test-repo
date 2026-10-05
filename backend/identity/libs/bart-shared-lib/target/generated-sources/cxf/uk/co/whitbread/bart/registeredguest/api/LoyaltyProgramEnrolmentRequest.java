
package uk.co.whitbread.bart.registeredguest.api;

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
 *         &lt;element name="LoyaltyProgramEnrolmentDetails" type="{http://bartws.micros.com/1.13}LoyaltyProgramEnrolmentRequest" minOccurs="0"/&gt;
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
    "loyaltyProgramEnrolmentDetails"
})
@XmlRootElement(name = "LoyaltyProgramEnrolmentRequest")
public class LoyaltyProgramEnrolmentRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "LoyaltyProgramEnrolmentDetails")
    protected LoyaltyProgramEnrolmentRequest2 loyaltyProgramEnrolmentDetails;

    /**
     * Gets the value of the loyaltyProgramEnrolmentDetails property.
     * 
     * @return
     *     possible object is
     *     {@link LoyaltyProgramEnrolmentRequest2 }
     *     
     */
    public LoyaltyProgramEnrolmentRequest2 getLoyaltyProgramEnrolmentDetails() {
        return loyaltyProgramEnrolmentDetails;
    }

    /**
     * Sets the value of the loyaltyProgramEnrolmentDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoyaltyProgramEnrolmentRequest2 }
     *     
     */
    public void setLoyaltyProgramEnrolmentDetails(LoyaltyProgramEnrolmentRequest2 value) {
        this.loyaltyProgramEnrolmentDetails = value;
    }

}
