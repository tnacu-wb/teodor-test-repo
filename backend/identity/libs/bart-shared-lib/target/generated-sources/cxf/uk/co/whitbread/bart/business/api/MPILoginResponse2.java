
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for MPILoginResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MPILoginResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="numberOfFutureStays" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="numberOfPastStays" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="numberOfCancelledBookings" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="companyName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="address" type="{http://corporate.micros.com/1.0}Address"/&gt;
 *         &lt;element name="contact" type="{http://corporate.micros.com/1.0}SelfRegisterContact"/&gt;
 *         &lt;element name="isExistingCompany" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "MPILoginResponse", propOrder = {
    "sessionID",
    "numberOfFutureStays",
    "numberOfPastStays",
    "numberOfCancelledBookings",
    "companyName",
    "address",
    "contact",
    "isExistingCompany",
    "success",
    "errors"
})
public class MPILoginResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected long numberOfFutureStays;
    protected long numberOfPastStays;
    protected long numberOfCancelledBookings;
    @XmlElement(required = true)
    protected String companyName;
    @XmlElement(required = true)
    protected Address address;
    @XmlElement(required = true)
    protected SelfRegisterContact contact;
    protected boolean isExistingCompany;
    protected boolean success;
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
     * Gets the value of the numberOfFutureStays property.
     * 
     */
    public long getNumberOfFutureStays() {
        return numberOfFutureStays;
    }

    /**
     * Sets the value of the numberOfFutureStays property.
     * 
     */
    public void setNumberOfFutureStays(long value) {
        this.numberOfFutureStays = value;
    }

    /**
     * Gets the value of the numberOfPastStays property.
     * 
     */
    public long getNumberOfPastStays() {
        return numberOfPastStays;
    }

    /**
     * Sets the value of the numberOfPastStays property.
     * 
     */
    public void setNumberOfPastStays(long value) {
        this.numberOfPastStays = value;
    }

    /**
     * Gets the value of the numberOfCancelledBookings property.
     * 
     */
    public long getNumberOfCancelledBookings() {
        return numberOfCancelledBookings;
    }

    /**
     * Sets the value of the numberOfCancelledBookings property.
     * 
     */
    public void setNumberOfCancelledBookings(long value) {
        this.numberOfCancelledBookings = value;
    }

    /**
     * Gets the value of the companyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the value of the companyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyName(String value) {
        this.companyName = value;
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
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link SelfRegisterContact }
     *     
     */
    public SelfRegisterContact getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelfRegisterContact }
     *     
     */
    public void setContact(SelfRegisterContact value) {
        this.contact = value;
    }

    /**
     * Gets the value of the isExistingCompany property.
     * 
     */
    public boolean isIsExistingCompany() {
        return isExistingCompany;
    }

    /**
     * Sets the value of the isExistingCompany property.
     * 
     */
    public void setIsExistingCompany(boolean value) {
        this.isExistingCompany = value;
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
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
