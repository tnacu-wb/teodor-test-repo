
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetheredUserOverviewType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetheredUserOverviewType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TetheredUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="APIUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="UserRole" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CountMyCards" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MemorableWordExist" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "TetheredUserOverviewType", propOrder = {
    "tetheredUserGuid",
    "apiUserGuid",
    "userRole",
    "countMyCards",
    "memorableWordExist",
    "customAttributes"
})
public class TetheredUserOverviewType {

    /**
     * Only applies to tethered users
     * 
     */
    @XmlElement(name = "TetheredUserGuid", required = true)
    protected String tetheredUserGuid;
    /**
     * This is an unique identifier for a user and uniquely identifies a user whether the user is tethered ot not. This value may be used for example in a mycard situation when adding or updating a card rather than selecting the user from a list of APIUserGuid
     * 
     */
    @XmlElement(name = "APIUserGuid", required = true)
    protected String apiUserGuid;
    /**
     * AccountHolder
     *             AccountCardHolder
     *             AccountCardHolderWithReportsAndInvoices ReportsAndInvoices
     * 
     */
    @XmlElement(name = "UserRole", required = true)
    protected String userRole;
    /**
     * This will be a count of the users “current” cards [note ‘current’ cards can be active (activated) or inactive (not yet activated)]
     * 
     */
    @XmlElement(name = "CountMyCards")
    protected int countMyCards;
    /**
     * Only applies to tethered users
     * 
     */
    @XmlElement(name = "MemorableWordExist")
    protected boolean memorableWordExist;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Only applies to tethered users
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTetheredUserGuid() {
        return tetheredUserGuid;
    }

    /**
     * Sets the value of the tetheredUserGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getTetheredUserGuid()
     */
    public void setTetheredUserGuid(String value) {
        this.tetheredUserGuid = value;
    }

    /**
     * This is an unique identifier for a user and uniquely identifies a user whether the user is tethered ot not. This value may be used for example in a mycard situation when adding or updating a card rather than selecting the user from a list of APIUserGuid
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
     * AccountHolder
     *             AccountCardHolder
     *             AccountCardHolderWithReportsAndInvoices ReportsAndInvoices
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets the value of the userRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getUserRole()
     */
    public void setUserRole(String value) {
        this.userRole = value;
    }

    /**
     * This will be a count of the users “current” cards [note ‘current’ cards can be active (activated) or inactive (not yet activated)]
     * 
     */
    public int getCountMyCards() {
        return countMyCards;
    }

    /**
     * Sets the value of the countMyCards property.
     * 
     */
    public void setCountMyCards(int value) {
        this.countMyCards = value;
    }

    /**
     * Only applies to tethered users
     * 
     */
    public boolean isMemorableWordExist() {
        return memorableWordExist;
    }

    /**
     * Sets the value of the memorableWordExist property.
     * 
     */
    public void setMemorableWordExist(boolean value) {
        this.memorableWordExist = value;
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
