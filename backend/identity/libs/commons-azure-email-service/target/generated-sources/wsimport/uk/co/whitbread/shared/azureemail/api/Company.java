
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Company complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="Company">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="activateLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="authorityLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="companyLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="companyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="resetLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="unsubscribeLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Company", propOrder = {
    "activateLink",
    "authorityLevel",
    "companyLink",
    "companyName",
    "emailAddress",
    "resetLink",
    "unsubscribeLink"
})
public class Company
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String activateLink;
    @XmlElement(nillable = true)
    protected String authorityLevel;
    @XmlElement(nillable = true)
    protected String companyLink;
    @XmlElement(nillable = true)
    protected String companyName;
    @XmlElement(nillable = true)
    protected String emailAddress;
    @XmlElement(nillable = true)
    protected String resetLink;
    @XmlElement(nillable = true)
    protected String unsubscribeLink;

    /**
     * Gets the value of the activateLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivateLink() {
        return activateLink;
    }

    /**
     * Sets the value of the activateLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivateLink(String value) {
        this.activateLink = value;
    }

    /**
     * Gets the value of the authorityLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorityLevel() {
        return authorityLevel;
    }

    /**
     * Sets the value of the authorityLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorityLevel(String value) {
        this.authorityLevel = value;
    }

    /**
     * Gets the value of the companyLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyLink() {
        return companyLink;
    }

    /**
     * Sets the value of the companyLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyLink(String value) {
        this.companyLink = value;
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
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the resetLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResetLink() {
        return resetLink;
    }

    /**
     * Sets the value of the resetLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResetLink(String value) {
        this.resetLink = value;
    }

    /**
     * Gets the value of the unsubscribeLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnsubscribeLink() {
        return unsubscribeLink;
    }

    /**
     * Sets the value of the unsubscribeLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsubscribeLink(String value) {
        this.unsubscribeLink = value;
    }

}
