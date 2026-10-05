
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PlatformApplicationPackage complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PlatformApplicationPackage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResourceSpecification" type="{http://exacttarget.com/wsdl/partnerAPI}ResourceSpecification" minOccurs="0"/&gt;
 *         &lt;element name="SigningKey" type="{http://exacttarget.com/wsdl/partnerAPI}PublicKeyManagement" minOccurs="0"/&gt;
 *         &lt;element name="IsUpgrade" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DeveloperVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlatformApplicationPackage", propOrder = {
    "resourceSpecification",
    "signingKey",
    "isUpgrade",
    "developerVersion"
})
public class PlatformApplicationPackage
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ResourceSpecification")
    protected ResourceSpecification resourceSpecification;
    @XmlElement(name = "SigningKey")
    protected PublicKeyManagement signingKey;
    @XmlElement(name = "IsUpgrade")
    protected Boolean isUpgrade;
    @XmlElement(name = "DeveloperVersion")
    protected String developerVersion;

    /**
     * Gets the value of the resourceSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceSpecification }
     *     
     */
    public ResourceSpecification getResourceSpecification() {
        return resourceSpecification;
    }

    /**
     * Sets the value of the resourceSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceSpecification }
     *     
     */
    public void setResourceSpecification(ResourceSpecification value) {
        this.resourceSpecification = value;
    }

    /**
     * Gets the value of the signingKey property.
     * 
     * @return
     *     possible object is
     *     {@link PublicKeyManagement }
     *     
     */
    public PublicKeyManagement getSigningKey() {
        return signingKey;
    }

    /**
     * Sets the value of the signingKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicKeyManagement }
     *     
     */
    public void setSigningKey(PublicKeyManagement value) {
        this.signingKey = value;
    }

    /**
     * Gets the value of the isUpgrade property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsUpgrade() {
        return isUpgrade;
    }

    /**
     * Sets the value of the isUpgrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUpgrade(Boolean value) {
        this.isUpgrade = value;
    }

    /**
     * Gets the value of the developerVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeveloperVersion() {
        return developerVersion;
    }

    /**
     * Sets the value of the developerVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeveloperVersion(String value) {
        this.developerVersion = value;
    }

}
