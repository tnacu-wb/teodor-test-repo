
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ChangePasswordRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ChangePasswordRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="registeredEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="registeredTelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="passwordToken" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="newPassword" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangePasswordRequest", propOrder = {
    "registeredEmail",
    "registeredTelephoneNumber",
    "passwordToken",
    "newPassword"
})
public class ChangePasswordRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String registeredEmail;
    protected String registeredTelephoneNumber;
    @XmlElement(required = true)
    protected String passwordToken;
    @XmlElement(required = true)
    protected String newPassword;

    /**
     * Gets the value of the registeredEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisteredEmail() {
        return registeredEmail;
    }

    /**
     * Sets the value of the registeredEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisteredEmail(String value) {
        this.registeredEmail = value;
    }

    /**
     * Gets the value of the registeredTelephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisteredTelephoneNumber() {
        return registeredTelephoneNumber;
    }

    /**
     * Sets the value of the registeredTelephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisteredTelephoneNumber(String value) {
        this.registeredTelephoneNumber = value;
    }

    /**
     * Gets the value of the passwordToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordToken() {
        return passwordToken;
    }

    /**
     * Sets the value of the passwordToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordToken(String value) {
        this.passwordToken = value;
    }

    /**
     * Gets the value of the newPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the value of the newPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewPassword(String value) {
        this.newPassword = value;
    }

}
