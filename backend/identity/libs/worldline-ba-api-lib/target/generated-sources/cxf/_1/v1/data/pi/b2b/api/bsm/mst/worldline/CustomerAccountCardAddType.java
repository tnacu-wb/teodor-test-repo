
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardAddType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardAddType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardBaseDetailsType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PrimarySchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="APIUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType" minOccurs="0"/&gt;
 *         &lt;element name="DespatchChoice" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardDespatchChoiceType" minOccurs="0"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardAddType", propOrder = {
    "primarySchemeCustomerId",
    "schemeCustomerId",
    "apiUserGuid",
    "despatchChoice",
    "customAttributes"
})
public class CustomerAccountCardAddType
    extends CustomerAccountCardBaseDetailsType
{

    @XmlElement(name = "PrimarySchemeCustomerId")
    protected int primarySchemeCustomerId;
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    /**
     * If appropriate select a user from a list
     * 
     *                 Note: To invite someone to be a registered user use additional call to customerAccountCardInvite
     * 
     */
    @XmlElement(name = "APIUserGuid")
    protected String apiUserGuid;
    /**
     * If not supplied defaults to AccountCorrespondenceAddress
     * 
     */
    @XmlElement(name = "DespatchChoice")
    protected CustomerAccountCardDespatchChoiceType despatchChoice;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the primarySchemeCustomerId property.
     * 
     */
    public int getPrimarySchemeCustomerId() {
        return primarySchemeCustomerId;
    }

    /**
     * Sets the value of the primarySchemeCustomerId property.
     * 
     */
    public void setPrimarySchemeCustomerId(int value) {
        this.primarySchemeCustomerId = value;
    }

    /**
     * Gets the value of the schemeCustomerId property.
     * 
     */
    public int getSchemeCustomerId() {
        return schemeCustomerId;
    }

    /**
     * Sets the value of the schemeCustomerId property.
     * 
     */
    public void setSchemeCustomerId(int value) {
        this.schemeCustomerId = value;
    }

    /**
     * If appropriate select a user from a list
     * 
     *                 Note: To invite someone to be a registered user use additional call to customerAccountCardInvite
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
     * @see #getAPIUserGuid()
     */
    public void setAPIUserGuid(String value) {
        this.apiUserGuid = value;
    }

    /**
     * If not supplied defaults to AccountCorrespondenceAddress
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardDespatchChoiceType }
     *     
     */
    public CustomerAccountCardDespatchChoiceType getDespatchChoice() {
        return despatchChoice;
    }

    /**
     * Sets the value of the despatchChoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardDespatchChoiceType }
     *     
     * @see #getDespatchChoice()
     */
    public void setDespatchChoice(CustomerAccountCardDespatchChoiceType value) {
        this.despatchChoice = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customAttributes property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomAttributeType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<CustomAttributeType> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
