
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Alternative address option
 *         Populate the adress object when alternative address is the despatch choice.
 *         Option to AssociateAddressWithFutureCardholder
 * 
 *         Registered user address option
 *         Do not populate address
 *         Selected registered user must be populated. If the selected registered user has an address already associated with it it will be used. If not the account correspondence address will be used.
 * 
 *         Account correspondence address
 *         Do not populate the address.
 *         The account correspondence address will be used.
 * 
 * &lt;p&gt;Java class for CustomerAccountCardAlternativeDespatchDetailType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardAlternativeDespatchDetailType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Title"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Forename"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Surname"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Address" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}AddressISOType" minOccurs="0"/&gt;
 *         &lt;element name="AssociateAddressWithFutureCardholder" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="CustomAttributes" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardAlternativeDespatchDetailType", propOrder = {
    "title",
    "forename",
    "surname",
    "address",
    "associateAddressWithFutureCardholder",
    "customAttributes"
})
public class CustomerAccountCardAlternativeDespatchDetailType {

    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Forename", required = true)
    protected String forename;
    @XmlElement(name = "Surname", required = true)
    protected String surname;
    @XmlElement(name = "Address")
    protected AddressISOType address;
    /**
     * Should we associate this address with any future cardholder registrations? When True we will use this address as a default card delivery address when the cardholder registers their card. When False this is considered an ad-hoc address and it will not be associated with the registered user
     * 
     */
    @XmlElement(name = "AssociateAddressWithFutureCardholder")
    protected boolean associateAddressWithFutureCardholder;
    @XmlElement(name = "CustomAttributes")
    protected List<Object> customAttributes;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the forename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForename() {
        return forename;
    }

    /**
     * Sets the value of the forename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForename(String value) {
        this.forename = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link AddressISOType }
     *     
     */
    public AddressISOType getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressISOType }
     *     
     */
    public void setAddress(AddressISOType value) {
        this.address = value;
    }

    /**
     * Should we associate this address with any future cardholder registrations? When True we will use this address as a default card delivery address when the cardholder registers their card. When False this is considered an ad-hoc address and it will not be associated with the registered user
     * 
     */
    public boolean isAssociateAddressWithFutureCardholder() {
        return associateAddressWithFutureCardholder;
    }

    /**
     * Sets the value of the associateAddressWithFutureCardholder property.
     * 
     */
    public void setAssociateAddressWithFutureCardholder(boolean value) {
        this.associateAddressWithFutureCardholder = value;
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
     * {@link Object }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<Object> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
