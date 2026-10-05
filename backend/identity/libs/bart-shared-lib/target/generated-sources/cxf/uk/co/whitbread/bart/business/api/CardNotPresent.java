
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CardNotPresent complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CardNotPresent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="businessAccountUsername" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="businessAccountPassword" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardNotPresent", propOrder = {
    "businessAccountUsername",
    "businessAccountPassword"
})
public class CardNotPresent
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String businessAccountUsername;
    @XmlElement(required = true)
    protected String businessAccountPassword;

    /**
     * Gets the value of the businessAccountUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessAccountUsername() {
        return businessAccountUsername;
    }

    /**
     * Sets the value of the businessAccountUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessAccountUsername(String value) {
        this.businessAccountUsername = value;
    }

    /**
     * Gets the value of the businessAccountPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessAccountPassword() {
        return businessAccountPassword;
    }

    /**
     * Sets the value of the businessAccountPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessAccountPassword(String value) {
        this.businessAccountPassword = value;
    }

}
