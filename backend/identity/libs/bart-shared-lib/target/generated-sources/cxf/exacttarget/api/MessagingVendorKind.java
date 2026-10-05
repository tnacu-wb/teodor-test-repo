
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Deprecated.
 * 
 * &lt;p&gt;Java class for MessagingVendorKind complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MessagingVendorKind"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Vendor" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Kind" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="IsUsernameRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="IsPasswordRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="IsProfileRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessagingVendorKind", propOrder = {
    "vendor",
    "kind",
    "isUsernameRequired",
    "isPasswordRequired",
    "isProfileRequired"
})
public class MessagingVendorKind
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Vendor", required = true)
    protected String vendor;
    @XmlElement(name = "Kind", required = true)
    protected String kind;
    @XmlElement(name = "IsUsernameRequired")
    protected boolean isUsernameRequired;
    @XmlElement(name = "IsPasswordRequired")
    protected boolean isPasswordRequired;
    @XmlElement(name = "IsProfileRequired")
    protected boolean isProfileRequired;

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }

    /**
     * Gets the value of the isUsernameRequired property.
     * 
     */
    public boolean isIsUsernameRequired() {
        return isUsernameRequired;
    }

    /**
     * Sets the value of the isUsernameRequired property.
     * 
     */
    public void setIsUsernameRequired(boolean value) {
        this.isUsernameRequired = value;
    }

    /**
     * Gets the value of the isPasswordRequired property.
     * 
     */
    public boolean isIsPasswordRequired() {
        return isPasswordRequired;
    }

    /**
     * Sets the value of the isPasswordRequired property.
     * 
     */
    public void setIsPasswordRequired(boolean value) {
        this.isPasswordRequired = value;
    }

    /**
     * Gets the value of the isProfileRequired property.
     * 
     */
    public boolean isIsProfileRequired() {
        return isProfileRequired;
    }

    /**
     * Sets the value of the isProfileRequired property.
     * 
     */
    public void setIsProfileRequired(boolean value) {
        this.isProfileRequired = value;
    }

}
