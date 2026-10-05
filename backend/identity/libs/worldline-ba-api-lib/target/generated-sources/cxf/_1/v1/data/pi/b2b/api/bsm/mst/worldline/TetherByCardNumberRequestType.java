
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetherByCardNumberRequestType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetherByCardNumberRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Header" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}HeaderType"/&gt;
 *         &lt;element name="TrustedPartnerCredentials" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TrustedPartnerCredentialsType"/&gt;
 *         &lt;element name="LinkCode" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}LinkCodeType"/&gt;
 *         &lt;element name="CardNumber" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CardNumberType"/&gt;
 *         &lt;element name="NewMemorableWord"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="64"/&gt;
 *               &lt;minLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
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
@XmlType(name = "TetherByCardNumberRequestType", propOrder = {
    "header",
    "trustedPartnerCredentials",
    "linkCode",
    "cardNumber",
    "newMemorableWord",
    "customAttributes"
})
public class TetherByCardNumberRequestType {

    @XmlElement(name = "Header", required = true)
    protected HeaderType header;
    @XmlElement(name = "TrustedPartnerCredentials", required = true)
    protected TrustedPartnerCredentialsType trustedPartnerCredentials;
    /**
     * Unique code that will either have been emailed to user OR should be retrieved from Worldline MMA area
     * 
     */
    @XmlElement(name = "LinkCode", required = true)
    protected String linkCode;
    /**
     * This card number must be associated with the user (i.e. for a cardholder the card must registered to the user and for an account holder the card must be a "my card")
     * 
     */
    @XmlElement(name = "CardNumber", required = true)
    protected String cardNumber;
    /**
     * This must be specified in order to use CNP going forward
     * 
     */
    @XmlElement(name = "NewMemorableWord", required = true)
    protected String newMemorableWord;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link HeaderType }
     *     
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *     
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Gets the value of the trustedPartnerCredentials property.
     * 
     * @return
     *     possible object is
     *     {@link TrustedPartnerCredentialsType }
     *     
     */
    public TrustedPartnerCredentialsType getTrustedPartnerCredentials() {
        return trustedPartnerCredentials;
    }

    /**
     * Sets the value of the trustedPartnerCredentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrustedPartnerCredentialsType }
     *     
     */
    public void setTrustedPartnerCredentials(TrustedPartnerCredentialsType value) {
        this.trustedPartnerCredentials = value;
    }

    /**
     * Unique code that will either have been emailed to user OR should be retrieved from Worldline MMA area
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkCode() {
        return linkCode;
    }

    /**
     * Sets the value of the linkCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLinkCode()
     */
    public void setLinkCode(String value) {
        this.linkCode = value;
    }

    /**
     * This card number must be associated with the user (i.e. for a cardholder the card must registered to the user and for an account holder the card must be a "my card")
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the value of the cardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCardNumber()
     */
    public void setCardNumber(String value) {
        this.cardNumber = value;
    }

    /**
     * This must be specified in order to use CNP going forward
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewMemorableWord() {
        return newMemorableWord;
    }

    /**
     * Sets the value of the newMemorableWord property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getNewMemorableWord()
     */
    public void setNewMemorableWord(String value) {
        this.newMemorableWord = value;
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
