
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AccountActivationDetailsResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AccountActivationDetailsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="companyID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="contact" type="{http://corporate.micros.com/1.0}Contact"/&gt;
 *         &lt;element name="address" type="{http://corporate.micros.com/1.0}Address" minOccurs="0"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="profileLocked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="MIPOAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MICRAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MIUDAnswers" type="{http://corporate.micros.com/1.0}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *         &lt;element name="changeCompanyName" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="employeeID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountActivationDetailsResponse", propOrder = {
    "sessionID",
    "companyID",
    "contact",
    "address",
    "success",
    "profileLocked",
    "mipoAnswer",
    "micrAnswer",
    "miudAnswers",
    "changeCompanyName",
    "employeeID",
    "errors"
})
public class AccountActivationDetailsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String companyID;
    @XmlElement(required = true)
    protected Contact contact;
    protected Address address;
    protected Boolean success;
    protected Boolean profileLocked;
    @XmlElement(name = "MIPOAnswer")
    protected String mipoAnswer;
    @XmlElement(name = "MICRAnswer")
    protected String micrAnswer;
    @XmlElement(name = "MIUDAnswers")
    protected ArrayOfListItemListItem miudAnswers;
    protected Boolean changeCompanyName;
    @XmlElement(required = true)
    protected String employeeID;
    protected ArrayOferrorError errors;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the companyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyID(String value) {
        this.companyID = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setContact(Contact value) {
        this.contact = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the success property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuccess(Boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the profileLocked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProfileLocked() {
        return profileLocked;
    }

    /**
     * Sets the value of the profileLocked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProfileLocked(Boolean value) {
        this.profileLocked = value;
    }

    /**
     * Gets the value of the mipoAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMIPOAnswer() {
        return mipoAnswer;
    }

    /**
     * Sets the value of the mipoAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMIPOAnswer(String value) {
        this.mipoAnswer = value;
    }

    /**
     * Gets the value of the micrAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMICRAnswer() {
        return micrAnswer;
    }

    /**
     * Sets the value of the micrAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMICRAnswer(String value) {
        this.micrAnswer = value;
    }

    /**
     * Gets the value of the miudAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getMIUDAnswers() {
        return miudAnswers;
    }

    /**
     * Sets the value of the miudAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setMIUDAnswers(ArrayOfListItemListItem value) {
        this.miudAnswers = value;
    }

    /**
     * Gets the value of the changeCompanyName property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isChangeCompanyName() {
        return changeCompanyName;
    }

    /**
     * Sets the value of the changeCompanyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChangeCompanyName(Boolean value) {
        this.changeCompanyName = value;
    }

    /**
     * Gets the value of the employeeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeID() {
        return employeeID;
    }

    /**
     * Sets the value of the employeeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeID(String value) {
        this.employeeID = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOferrorError }
     *     
     */
    public ArrayOferrorError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOferrorError }
     *     
     */
    public void setErrors(ArrayOferrorError value) {
        this.errors = value;
    }

}
