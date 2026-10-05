
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ProfilePersonalDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ProfilePersonalDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contact" type="{http://corporate.micros.com/1.0}Contact"/&gt;
 *         &lt;element name="address" type="{http://corporate.micros.com/1.0}Address"/&gt;
 *         &lt;element name="carReg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nationality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="passportNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userDefinedAnswers" type="{http://corporate.micros.com/1.0}ArrayOfUDFAnswersUDFAnswers" minOccurs="0"/&gt;
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="purchaseOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfilePersonalDetails", propOrder = {
    "contact",
    "address",
    "carReg",
    "nationality",
    "passportNumber",
    "userDefinedAnswers",
    "customerReference",
    "purchaseOrder"
})
public class ProfilePersonalDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected Contact contact;
    @XmlElement(required = true)
    protected Address address;
    protected String carReg;
    protected String nationality;
    protected String passportNumber;
    protected ArrayOfUDFAnswersUDFAnswers userDefinedAnswers;
    protected String customerReference;
    protected String purchaseOrder;

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
     * Gets the value of the carReg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarReg() {
        return carReg;
    }

    /**
     * Sets the value of the carReg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarReg(String value) {
        this.carReg = value;
    }

    /**
     * Gets the value of the nationality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Sets the value of the nationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationality(String value) {
        this.nationality = value;
    }

    /**
     * Gets the value of the passportNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * Sets the value of the passportNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassportNumber(String value) {
        this.passportNumber = value;
    }

    /**
     * Gets the value of the userDefinedAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUDFAnswersUDFAnswers }
     *     
     */
    public ArrayOfUDFAnswersUDFAnswers getUserDefinedAnswers() {
        return userDefinedAnswers;
    }

    /**
     * Sets the value of the userDefinedAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUDFAnswersUDFAnswers }
     *     
     */
    public void setUserDefinedAnswers(ArrayOfUDFAnswersUDFAnswers value) {
        this.userDefinedAnswers = value;
    }

    /**
     * Gets the value of the customerReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerReference() {
        return customerReference;
    }

    /**
     * Sets the value of the customerReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerReference(String value) {
        this.customerReference = value;
    }

    /**
     * Gets the value of the purchaseOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the value of the purchaseOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseOrder(String value) {
        this.purchaseOrder = value;
    }

}
