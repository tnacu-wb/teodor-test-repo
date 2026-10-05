
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CNPCheckTelephoneBookingBySessionIDRequestType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CNPCheckTelephoneBookingBySessionIDRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Header" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}HeaderType"/&gt;
 *         &lt;element name="TrustedPartnerCredentials" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TrustedPartnerCredentialsType"/&gt;
 *         &lt;element name="CNPSessionID" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="Char1Position"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Char1Value"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Char2Position"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Char2Value"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="1"/&gt;
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
@XmlType(name = "CNPCheckTelephoneBookingBySessionIDRequestType", propOrder = {
    "header",
    "trustedPartnerCredentials",
    "cnpSessionID",
    "char1Position",
    "char1Value",
    "char2Position",
    "char2Value",
    "customAttributes"
})
public class CNPCheckTelephoneBookingBySessionIDRequestType {

    @XmlElement(name = "Header", required = true)
    protected HeaderType header;
    @XmlElement(name = "TrustedPartnerCredentials", required = true)
    protected TrustedPartnerCredentialsType trustedPartnerCredentials;
    /**
     * SessionID created by calling 'cnpGetDigitsForCheckTelephoneBooking'
     * 
     */
    @XmlElement(name = "CNPSessionID", required = true)
    protected String cnpSessionID;
    /**
     * Position of first character (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char1Position")
    protected int char1Position;
    /**
     * First supplied character (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char1Value", required = true)
    protected String char1Value;
    /**
     * Position of second character (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char2Position")
    protected int char2Position;
    /**
     * Second supplied character (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char2Value", required = true)
    protected String char2Value;
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
     * SessionID created by calling 'cnpGetDigitsForCheckTelephoneBooking'
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCNPSessionID() {
        return cnpSessionID;
    }

    /**
     * Sets the value of the cnpSessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCNPSessionID()
     */
    public void setCNPSessionID(String value) {
        this.cnpSessionID = value;
    }

    /**
     * Position of first character (of memorable word OR password)
     * 
     */
    public int getChar1Position() {
        return char1Position;
    }

    /**
     * Sets the value of the char1Position property.
     * 
     */
    public void setChar1Position(int value) {
        this.char1Position = value;
    }

    /**
     * First supplied character (of memorable word OR password)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChar1Value() {
        return char1Value;
    }

    /**
     * Sets the value of the char1Value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getChar1Value()
     */
    public void setChar1Value(String value) {
        this.char1Value = value;
    }

    /**
     * Position of second character (of memorable word OR password)
     * 
     */
    public int getChar2Position() {
        return char2Position;
    }

    /**
     * Sets the value of the char2Position property.
     * 
     */
    public void setChar2Position(int value) {
        this.char2Position = value;
    }

    /**
     * Second supplied character (of memorable word OR password)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChar2Value() {
        return char2Value;
    }

    /**
     * Sets the value of the char2Value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getChar2Value()
     */
    public void setChar2Value(String value) {
        this.char2Value = value;
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
