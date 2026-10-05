
package uk.co.whitbread.bart.business.auth0.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for Employee2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Employee2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="employeeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ghNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ghCreation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="contact" type="{http://corporate.micros.com/1.0}Contact2"/&gt;
 *         &lt;element name="address" type="{http://corporate.micros.com/1.0}Address2" minOccurs="0"/&gt;
 *         &lt;element name="centralCard" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="customerReferenceAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="purchaseOrderAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userDefinedAnswers" type="{http://corporate.micros.com/1.0}ArrayOfUDFAnswers" minOccurs="0"/&gt;
 *         &lt;element name="accessLevel" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="STAYER"/&gt;
 *               &lt;enumeration value="SELF"/&gt;
 *               &lt;enumeration value="BOOKER"/&gt;
 *               &lt;enumeration value="SUPER"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="totalStays" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Employee2", propOrder = {
    "employeeID",
    "ghNumber",
    "ghCreation",
    "contact",
    "address",
    "centralCard",
    "customerReferenceAnswer",
    "purchaseOrderAnswer",
    "userDefinedAnswers",
    "accessLevel",
    "totalStays"
})
public class Employee2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String employeeID;
    protected String ghNumber;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate ghCreation;
    @XmlElement(required = true)
    protected Contact2 contact;
    protected Address2 address;
    protected String centralCard;
    protected String customerReferenceAnswer;
    protected String purchaseOrderAnswer;
    protected ArrayOfUDFAnswers userDefinedAnswers;
    protected String accessLevel;
    protected Long totalStays;

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
     * Gets the value of the ghNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGhNumber() {
        return ghNumber;
    }

    /**
     * Sets the value of the ghNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGhNumber(String value) {
        this.ghNumber = value;
    }

    /**
     * Gets the value of the ghCreation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getGhCreation() {
        return ghCreation;
    }

    /**
     * Sets the value of the ghCreation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGhCreation(LocalDate value) {
        this.ghCreation = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link Contact2 }
     *     
     */
    public Contact2 getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact2 }
     *     
     */
    public void setContact(Contact2 value) {
        this.contact = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address2 }
     *     
     */
    public Address2 getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address2 }
     *     
     */
    public void setAddress(Address2 value) {
        this.address = value;
    }

    /**
     * Gets the value of the centralCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCentralCard() {
        return centralCard;
    }

    /**
     * Sets the value of the centralCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCentralCard(String value) {
        this.centralCard = value;
    }

    /**
     * Gets the value of the customerReferenceAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerReferenceAnswer() {
        return customerReferenceAnswer;
    }

    /**
     * Sets the value of the customerReferenceAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerReferenceAnswer(String value) {
        this.customerReferenceAnswer = value;
    }

    /**
     * Gets the value of the purchaseOrderAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrderAnswer() {
        return purchaseOrderAnswer;
    }

    /**
     * Sets the value of the purchaseOrderAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseOrderAnswer(String value) {
        this.purchaseOrderAnswer = value;
    }

    /**
     * Gets the value of the userDefinedAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUDFAnswers }
     *     
     */
    public ArrayOfUDFAnswers getUserDefinedAnswers() {
        return userDefinedAnswers;
    }

    /**
     * Sets the value of the userDefinedAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUDFAnswers }
     *     
     */
    public void setUserDefinedAnswers(ArrayOfUDFAnswers value) {
        this.userDefinedAnswers = value;
    }

    /**
     * Gets the value of the accessLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the value of the accessLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessLevel(String value) {
        this.accessLevel = value;
    }

    /**
     * Gets the value of the totalStays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalStays() {
        return totalStays;
    }

    /**
     * Sets the value of the totalStays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalStays(Long value) {
        this.totalStays = value;
    }

}
