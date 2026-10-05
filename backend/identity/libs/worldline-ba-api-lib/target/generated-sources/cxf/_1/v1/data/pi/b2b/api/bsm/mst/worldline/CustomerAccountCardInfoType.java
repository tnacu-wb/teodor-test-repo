
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Read-only information
 * 
 * &lt;p&gt;Java class for CustomerAccountCardInfoType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CardId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="PAN"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CardNumberMaskedType"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="19"/&gt;
 *               &lt;pattern value="[0-9]{16,19}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PrimarySchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="IsMyCard" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Status" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}eCustomerAccountCardStatusType"/&gt;
 *         &lt;element name="IsActivated" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="ExpiryDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="CardCurrentSpend" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType" minOccurs="0"/&gt;
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
 *         &lt;element name="RegInfoEmail"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="200"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegisteredUsers" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountRegisteredUserType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ContextCan" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlType(name = "CustomerAccountCardInfoType", propOrder = {
    "cardId",
    "pan",
    "primarySchemeCustomerId",
    "schemeCustomerId",
    "isMyCard",
    "status",
    "isActivated",
    "expiryDate",
    "cardCurrentSpend",
    "regInfoTitle",
    "regInfoForename",
    "regInfoSurname",
    "regInfoEmail",
    "registeredUsers",
    "contextCan",
    "customAttributes"
})
public class CustomerAccountCardInfoType {

    @XmlElement(name = "CardId")
    protected int cardId;
    @XmlElement(name = "PAN", required = true)
    protected String pan;
    @XmlElement(name = "PrimarySchemeCustomerId")
    protected int primarySchemeCustomerId;
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    @XmlElement(name = "IsMyCard")
    protected boolean isMyCard;
    @XmlElement(name = "Status", required = true)
    @XmlSchemaType(name = "string")
    protected ECustomerAccountCardStatusType status;
    @XmlElement(name = "IsActivated")
    protected boolean isActivated;
    @XmlElement(name = "ExpiryDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expiryDate;
    @XmlElement(name = "CardCurrentSpend")
    protected CurrencyType cardCurrentSpend;
    @XmlElement(name = "RegInfoTitle", required = true)
    protected String regInfoTitle;
    @XmlElement(name = "RegInfoForename", required = true)
    protected String regInfoForename;
    @XmlElement(name = "RegInfoSurname", required = true)
    protected String regInfoSurname;
    @XmlElement(name = "RegInfoEmail", required = true)
    protected String regInfoEmail;
    /**
     * Card owners can be account holders, card holders, and other roles. There is usually one however can be multiple
     * 
     */
    @XmlElement(name = "RegisteredUsers")
    protected List<CustomerAccountRegisteredUserType> registeredUsers;
    /**
     * Comma seperated list of actions that can be performed in the combined context of the state of account or cost centre. card and user
     *             e.g.
     *             CardActivate
     *             CardCancel
     *             CardView
     * 
     */
    @XmlElement(name = "ContextCan", required = true)
    protected String contextCan;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the cardId property.
     * 
     */
    public int getCardId() {
        return cardId;
    }

    /**
     * Sets the value of the cardId property.
     * 
     */
    public void setCardId(int value) {
        this.cardId = value;
    }

    /**
     * Gets the value of the pan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAN() {
        return pan;
    }

    /**
     * Sets the value of the pan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAN(String value) {
        this.pan = value;
    }

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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ECustomerAccountCardStatusType }
     *     
     */
    public ECustomerAccountCardStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECustomerAccountCardStatusType }
     *     
     */
    public void setStatus(ECustomerAccountCardStatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the isActivated property.
     * 
     */
    public boolean isIsActivated() {
        return isActivated;
    }

    /**
     * Sets the value of the isActivated property.
     * 
     */
    public void setIsActivated(boolean value) {
        this.isActivated = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the cardCurrentSpend property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCardCurrentSpend() {
        return cardCurrentSpend;
    }

    /**
     * Sets the value of the cardCurrentSpend property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCardCurrentSpend(CurrencyType value) {
        this.cardCurrentSpend = value;
    }

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
     * Gets the value of the regInfoEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegInfoEmail() {
        return regInfoEmail;
    }

    /**
     * Sets the value of the regInfoEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegInfoEmail(String value) {
        this.regInfoEmail = value;
    }

    /**
     * Card owners can be account holders, card holders, and other roles. There is usually one however can be multiple
     * 
     * Gets the value of the registeredUsers property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the registeredUsers property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getRegisteredUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerAccountRegisteredUserType }
     * </p>
     * 
     * 
     * @return
     *     The value of the registeredUsers property.
     */
    public List<CustomerAccountRegisteredUserType> getRegisteredUsers() {
        if (registeredUsers == null) {
            registeredUsers = new ArrayList<>();
        }
        return this.registeredUsers;
    }

    /**
     * Comma seperated list of actions that can be performed in the combined context of the state of account or cost centre. card and user
     *             e.g.
     *             CardActivate
     *             CardCancel
     *             CardView
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextCan() {
        return contextCan;
    }

    /**
     * Sets the value of the contextCan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getContextCan()
     */
    public void setContextCan(String value) {
        this.contextCan = value;
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
