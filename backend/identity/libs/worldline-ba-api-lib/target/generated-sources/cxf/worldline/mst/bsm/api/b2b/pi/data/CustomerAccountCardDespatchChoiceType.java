
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardDespatchChoiceType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardDespatchChoiceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DespatchChoice"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}eDespatchChoiceType"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CustomerAccountCardAlternativeDespatchDetail" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardAlternativeDespatchDetailType" minOccurs="0"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardDespatchChoiceType", propOrder = {
    "despatchChoice",
    "customerAccountCardAlternativeDespatchDetail",
    "customAttributes"
})
public class CustomerAccountCardDespatchChoiceType {

    /**
     * AccountCorrespondenceAddress
     *             RegisteredUserAddress
     *             AlternativeAddress
     * 
     */
    @XmlElement(name = "DespatchChoice", required = true)
    protected EDespatchChoiceType despatchChoice;
    /**
     * Alternative Address Rules
     *             Populate this object when alternative address is the despatch choice.
     * 
     *             Registered User Address Rules
     *             SelectedRegisteredUser must be populated.
     *             if the registered user has a correspondence address it will be used otherwise the account correspondence address will be used
     * 
     *             Account Correspondence Address
     *             Do not populate this object
     * 
     */
    @XmlElement(name = "CustomerAccountCardAlternativeDespatchDetail")
    protected CustomerAccountCardAlternativeDespatchDetailType customerAccountCardAlternativeDespatchDetail;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * AccountCorrespondenceAddress
     *             RegisteredUserAddress
     *             AlternativeAddress
     * 
     * @return
     *     possible object is
     *     {@link EDespatchChoiceType }
     *     
     */
    public EDespatchChoiceType getDespatchChoice() {
        return despatchChoice;
    }

    /**
     * Sets the value of the despatchChoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDespatchChoiceType }
     *     
     * @see #getDespatchChoice()
     */
    public void setDespatchChoice(EDespatchChoiceType value) {
        this.despatchChoice = value;
    }

    /**
     * Alternative Address Rules
     *             Populate this object when alternative address is the despatch choice.
     * 
     *             Registered User Address Rules
     *             SelectedRegisteredUser must be populated.
     *             if the registered user has a correspondence address it will be used otherwise the account correspondence address will be used
     * 
     *             Account Correspondence Address
     *             Do not populate this object
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardAlternativeDespatchDetailType }
     *     
     */
    public CustomerAccountCardAlternativeDespatchDetailType getCustomerAccountCardAlternativeDespatchDetail() {
        return customerAccountCardAlternativeDespatchDetail;
    }

    /**
     * Sets the value of the customerAccountCardAlternativeDespatchDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardAlternativeDespatchDetailType }
     *     
     * @see #getCustomerAccountCardAlternativeDespatchDetail()
     */
    public void setCustomerAccountCardAlternativeDespatchDetail(CustomerAccountCardAlternativeDespatchDetailType value) {
        this.customerAccountCardAlternativeDespatchDetail = value;
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
