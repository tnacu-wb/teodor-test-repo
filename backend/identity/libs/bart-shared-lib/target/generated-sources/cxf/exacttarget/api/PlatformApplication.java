
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PlatformApplication complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PlatformApplication"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Package" type="{http://exacttarget.com/wsdl/partnerAPI}PlatformApplicationPackage"/&gt;
 *         &lt;element name="Packages" type="{http://exacttarget.com/wsdl/partnerAPI}PlatformApplicationPackage" maxOccurs="unbounded"/&gt;
 *         &lt;element name="ResourceSpecification" type="{http://exacttarget.com/wsdl/partnerAPI}ResourceSpecification" minOccurs="0"/&gt;
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
@XmlType(name = "PlatformApplication", propOrder = {
    "_package",
    "packages",
    "resourceSpecification",
    "developerVersion"
})
public class PlatformApplication
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Package", required = true)
    protected PlatformApplicationPackage _package;
    @XmlElement(name = "Packages", required = true)
    protected List<PlatformApplicationPackage> packages;
    @XmlElement(name = "ResourceSpecification")
    protected ResourceSpecification resourceSpecification;
    @XmlElement(name = "DeveloperVersion")
    protected String developerVersion;

    /**
     * Gets the value of the package property.
     * 
     * @return
     *     possible object is
     *     {@link PlatformApplicationPackage }
     *     
     */
    public PlatformApplicationPackage getPackage() {
        return _package;
    }

    /**
     * Sets the value of the package property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlatformApplicationPackage }
     *     
     */
    public void setPackage(PlatformApplicationPackage value) {
        this._package = value;
    }

    /**
     * Gets the value of the packages property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the packages property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPackages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlatformApplicationPackage }
     * </p>
     * 
     * 
     * @return
     *     The value of the packages property.
     */
    public List<PlatformApplicationPackage> getPackages() {
        if (packages == null) {
            packages = new ArrayList<>();
        }
        return this.packages;
    }

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
