
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardHolderInviteDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardHolderInviteDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IsMyCard" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="InviteeDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardInviteDetailsType" minOccurs="0"/&gt;
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
@XmlType(name = "CustomerAccountCardHolderInviteDetailsType", propOrder = {
    "isMyCard",
    "inviteeDetails",
    "customAttributes"
})
public class CustomerAccountCardHolderInviteDetailsType {

    @XmlElement(name = "IsMyCard")
    protected boolean isMyCard;
    @XmlElement(name = "InviteeDetails")
    protected CustomerAccountCardInviteDetailsType inviteeDetails;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the isMyCard property.
     * 
     */
    public boolean isIsMyCard() {
        return isMyCard;
    }

    /**
     * Sets the value of the isMyCard property.
     * 
     */
    public void setIsMyCard(boolean value) {
        this.isMyCard = value;
    }

    /**
     * Gets the value of the inviteeDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardInviteDetailsType }
     *     
     */
    public CustomerAccountCardInviteDetailsType getInviteeDetails() {
        return inviteeDetails;
    }

    /**
     * Sets the value of the inviteeDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardInviteDetailsType }
     *     
     */
    public void setInviteeDetails(CustomerAccountCardInviteDetailsType value) {
        this.inviteeDetails = value;
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
