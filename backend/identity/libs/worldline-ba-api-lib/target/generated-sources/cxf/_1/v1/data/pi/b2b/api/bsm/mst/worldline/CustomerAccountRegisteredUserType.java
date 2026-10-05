
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountRegisteredUserType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountRegisteredUserType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="APIUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="DisplayName" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="HasAddress" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountRegisteredUserType", propOrder = {
    "apiUserGuid",
    "displayName",
    "emailAddress",
    "hasAddress"
})
public class CustomerAccountRegisteredUserType {

    @XmlElement(name = "APIUserGuid", required = true)
    protected String apiUserGuid;
    /**
     * Title Forename Surname
     * 
     */
    @XmlElement(name = "DisplayName", required = true)
    protected Object displayName;
    /**
     * Email Address
     * 
     */
    @XmlElement(name = "EmailAddress", required = true)
    protected String emailAddress;
    @XmlElement(name = "HasAddress")
    protected boolean hasAddress;

    /**
     * Gets the value of the apiUserGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPIUserGuid() {
        return apiUserGuid;
    }

    /**
     * Sets the value of the apiUserGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAPIUserGuid(String value) {
        this.apiUserGuid = value;
    }

    /**
     * Title Forename Surname
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     * @see #getDisplayName()
     */
    public void setDisplayName(Object value) {
        this.displayName = value;
    }

    /**
     * Email Address
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
     * @see #getEmailAddress()
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the hasAddress property.
     * 
     */
    public boolean isHasAddress() {
        return hasAddress;
    }

    /**
     * Sets the value of the hasAddress property.
     * 
     */
    public void setHasAddress(boolean value) {
        this.hasAddress = value;
    }

}
