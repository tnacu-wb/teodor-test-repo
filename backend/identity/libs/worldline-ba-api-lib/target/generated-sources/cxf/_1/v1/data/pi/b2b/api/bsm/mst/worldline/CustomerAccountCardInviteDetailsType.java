
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardInviteDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardInviteDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegInfoTitle"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="25"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegInfoForename"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegInfoSurname"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegInfoEmailAddress" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}EmailAddressType"/&gt;
 *         &lt;element name="SendMeACopyOfInvite" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "CustomerAccountCardInviteDetailsType", propOrder = {
    "regInfoTitle",
    "regInfoForename",
    "regInfoSurname",
    "regInfoEmailAddress",
    "sendMeACopyOfInvite",
    "customAttributes"
})
public class CustomerAccountCardInviteDetailsType {

    @XmlElement(name = "RegInfoTitle", required = true)
    protected String regInfoTitle;
    @XmlElement(name = "RegInfoForename", required = true)
    protected String regInfoForename;
    @XmlElement(name = "RegInfoSurname", required = true)
    protected String regInfoSurname;
    @XmlElement(name = "RegInfoEmailAddress", required = true)
    protected String regInfoEmailAddress;
    @XmlElement(name = "SendMeACopyOfInvite")
    protected boolean sendMeACopyOfInvite;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the regInfoTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegInfoTitle() {
        return regInfoTitle;
    }

    /**
     * Sets the value of the regInfoTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegInfoTitle(String value) {
        this.regInfoTitle = value;
    }

    /**
     * Gets the value of the regInfoForename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegInfoForename() {
        return regInfoForename;
    }

    /**
     * Sets the value of the regInfoForename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegInfoForename(String value) {
        this.regInfoForename = value;
    }

    /**
     * Gets the value of the regInfoSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegInfoSurname() {
        return regInfoSurname;
    }

    /**
     * Sets the value of the regInfoSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegInfoSurname(String value) {
        this.regInfoSurname = value;
    }

    /**
     * Gets the value of the regInfoEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegInfoEmailAddress() {
        return regInfoEmailAddress;
    }

    /**
     * Sets the value of the regInfoEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegInfoEmailAddress(String value) {
        this.regInfoEmailAddress = value;
    }

    /**
     * Gets the value of the sendMeACopyOfInvite property.
     * 
     */
    public boolean isSendMeACopyOfInvite() {
        return sendMeACopyOfInvite;
    }

    /**
     * Sets the value of the sendMeACopyOfInvite property.
     * 
     */
    public void setSendMeACopyOfInvite(boolean value) {
        this.sendMeACopyOfInvite = value;
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
