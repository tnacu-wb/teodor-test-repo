
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for DirectoryTenant complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DirectoryTenant"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TenantOrgId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TenantUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TenantType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CustomDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TenantPublicKeysUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TenantName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PublicKeys" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}JsonWebKey" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectoryTenant", propOrder = {
    "customerId",
    "tenantOrgId",
    "tenantUri",
    "tenantType",
    "customDomain",
    "tenantPublicKeysUri",
    "tenantName",
    "description",
    "publicKeys"
})
public class DirectoryTenant
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CustomerId")
    protected String customerId;
    @XmlElement(name = "TenantOrgId")
    protected String tenantOrgId;
    @XmlElement(name = "TenantUri")
    protected String tenantUri;
    @XmlElement(name = "TenantType")
    protected String tenantType;
    @XmlElement(name = "CustomDomain")
    protected String customDomain;
    @XmlElement(name = "TenantPublicKeysUri")
    protected String tenantPublicKeysUri;
    @XmlElement(name = "TenantName")
    protected String tenantName;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "PublicKeys")
    protected List<JsonWebKey> publicKeys;

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the tenantOrgId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenantOrgId() {
        return tenantOrgId;
    }

    /**
     * Sets the value of the tenantOrgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenantOrgId(String value) {
        this.tenantOrgId = value;
    }

    /**
     * Gets the value of the tenantUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenantUri() {
        return tenantUri;
    }

    /**
     * Sets the value of the tenantUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenantUri(String value) {
        this.tenantUri = value;
    }

    /**
     * Gets the value of the tenantType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenantType() {
        return tenantType;
    }

    /**
     * Sets the value of the tenantType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenantType(String value) {
        this.tenantType = value;
    }

    /**
     * Gets the value of the customDomain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDomain() {
        return customDomain;
    }

    /**
     * Sets the value of the customDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDomain(String value) {
        this.customDomain = value;
    }

    /**
     * Gets the value of the tenantPublicKeysUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenantPublicKeysUri() {
        return tenantPublicKeysUri;
    }

    /**
     * Sets the value of the tenantPublicKeysUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenantPublicKeysUri(String value) {
        this.tenantPublicKeysUri = value;
    }

    /**
     * Gets the value of the tenantName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * Sets the value of the tenantName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenantName(String value) {
        this.tenantName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the publicKeys property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the publicKeys property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPublicKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JsonWebKey }
     * </p>
     * 
     * 
     * @return
     *     The value of the publicKeys property.
     */
    public List<JsonWebKey> getPublicKeys() {
        if (publicKeys == null) {
            publicKeys = new ArrayList<>();
        }
        return this.publicKeys;
    }

}
