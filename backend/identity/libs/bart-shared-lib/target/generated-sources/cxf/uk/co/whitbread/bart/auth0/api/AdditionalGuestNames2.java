
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AdditionalGuestNames2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AdditionalGuestNames2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="additionalTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalNationality" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="additionalPassport" type="{http://bartws.micros.com/1.13}Passport2" minOccurs="0"/&gt;
 *         &lt;element name="additionalCarRegistration" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="additionalEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalTelephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalMobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalGuestHistory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="additionalGuestID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalGuestNames2", propOrder = {
    "additionalTitle",
    "additionalFirstName",
    "additionalLastName",
    "additionalNationality",
    "additionalPassport",
    "additionalCarRegistration",
    "additionalEmailAddress",
    "additionalTelephone",
    "additionalMobile",
    "additionalGuestHistory",
    "additionalGuestID"
})
public class AdditionalGuestNames2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String additionalTitle;
    protected String additionalFirstName;
    protected String additionalLastName;
    protected String additionalNationality;
    protected Passport2 additionalPassport;
    protected String additionalCarRegistration;
    protected String additionalEmailAddress;
    protected String additionalTelephone;
    protected String additionalMobile;
    protected String additionalGuestHistory;
    protected Long additionalGuestID;

    /**
     * Gets the value of the additionalTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalTitle() {
        return additionalTitle;
    }

    /**
     * Sets the value of the additionalTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalTitle(String value) {
        this.additionalTitle = value;
    }

    /**
     * Gets the value of the additionalFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalFirstName() {
        return additionalFirstName;
    }

    /**
     * Sets the value of the additionalFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalFirstName(String value) {
        this.additionalFirstName = value;
    }

    /**
     * Gets the value of the additionalLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalLastName() {
        return additionalLastName;
    }

    /**
     * Sets the value of the additionalLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalLastName(String value) {
        this.additionalLastName = value;
    }

    /**
     * Gets the value of the additionalNationality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalNationality() {
        return additionalNationality;
    }

    /**
     * Sets the value of the additionalNationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalNationality(String value) {
        this.additionalNationality = value;
    }

    /**
     * Gets the value of the additionalPassport property.
     * 
     * @return
     *     possible object is
     *     {@link Passport2 }
     *     
     */
    public Passport2 getAdditionalPassport() {
        return additionalPassport;
    }

    /**
     * Sets the value of the additionalPassport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Passport2 }
     *     
     */
    public void setAdditionalPassport(Passport2 value) {
        this.additionalPassport = value;
    }

    /**
     * Gets the value of the additionalCarRegistration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalCarRegistration() {
        return additionalCarRegistration;
    }

    /**
     * Sets the value of the additionalCarRegistration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalCarRegistration(String value) {
        this.additionalCarRegistration = value;
    }

    /**
     * Gets the value of the additionalEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalEmailAddress() {
        return additionalEmailAddress;
    }

    /**
     * Sets the value of the additionalEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalEmailAddress(String value) {
        this.additionalEmailAddress = value;
    }

    /**
     * Gets the value of the additionalTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalTelephone() {
        return additionalTelephone;
    }

    /**
     * Sets the value of the additionalTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalTelephone(String value) {
        this.additionalTelephone = value;
    }

    /**
     * Gets the value of the additionalMobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalMobile() {
        return additionalMobile;
    }

    /**
     * Sets the value of the additionalMobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalMobile(String value) {
        this.additionalMobile = value;
    }

    /**
     * Gets the value of the additionalGuestHistory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalGuestHistory() {
        return additionalGuestHistory;
    }

    /**
     * Sets the value of the additionalGuestHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalGuestHistory(String value) {
        this.additionalGuestHistory = value;
    }

    /**
     * Gets the value of the additionalGuestID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAdditionalGuestID() {
        return additionalGuestID;
    }

    /**
     * Sets the value of the additionalGuestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAdditionalGuestID(Long value) {
        this.additionalGuestID = value;
    }

}
