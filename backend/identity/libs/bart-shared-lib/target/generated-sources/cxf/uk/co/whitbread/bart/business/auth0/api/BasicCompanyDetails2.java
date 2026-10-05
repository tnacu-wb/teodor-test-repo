
package uk.co.whitbread.bart.business.auth0.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BasicCompanyDetails2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BasicCompanyDetails2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="companyName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="companyAddress" type="{http://corporate.micros.com/1.0}Address2"/&gt;
 *         &lt;element name="mainContact" type="{http://corporate.micros.com/1.0}MainContact2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicCompanyDetails2", propOrder = {
    "companyName",
    "companyAddress",
    "mainContact"
})
public class BasicCompanyDetails2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String companyName;
    @XmlElement(required = true)
    protected Address2 companyAddress;
    @XmlElement(required = true)
    protected MainContact2 mainContact;

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
     * Gets the value of the companyAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address2 }
     *     
     */
    public Address2 getCompanyAddress() {
        return companyAddress;
    }

    /**
     * Sets the value of the companyAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address2 }
     *     
     */
    public void setCompanyAddress(Address2 value) {
        this.companyAddress = value;
    }

    /**
     * Gets the value of the mainContact property.
     * 
     * @return
     *     possible object is
     *     {@link MainContact2 }
     *     
     */
    public MainContact2 getMainContact() {
        return mainContact;
    }

    /**
     * Sets the value of the mainContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link MainContact2 }
     *     
     */
    public void setMainContact(MainContact2 value) {
        this.mainContact = value;
    }

}
