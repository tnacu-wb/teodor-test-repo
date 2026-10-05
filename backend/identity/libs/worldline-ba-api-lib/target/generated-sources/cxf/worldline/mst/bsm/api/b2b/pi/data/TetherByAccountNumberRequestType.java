
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetherByAccountNumberRequestType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetherByAccountNumberRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Header" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}HeaderType"/&gt;
 *         &lt;element name="TrustedPartnerCredentials" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TrustedPartnerCredentialsType"/&gt;
 *         &lt;element name="LinkCode" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}LinkCodeType"/&gt;
 *         &lt;element name="AccountNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}AccountNumberFromInvoiceType"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
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
@XmlType(name = "TetherByAccountNumberRequestType", propOrder = {
    "header",
    "trustedPartnerCredentials",
    "linkCode",
    "accountNumber",
    "newMemorableWord",
    "customAttributes"
})
public class TetherByAccountNumberRequestType {

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
     * Note: This is the 16 digit account number as displayed on the customers invoice
     * 
     */
    @XmlElement(name = "AccountNumber", required = true)
    protected String accountNumber;
    /**
     * This must be specified in order to use CNP going forward.  Effectively replaces the user password
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
     * Note: This is the 16 digit account number as displayed on the customers invoice
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAccountNumber()
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
    }

    /**
     * This must be specified in order to use CNP going forward.  Effectively replaces the user password
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
