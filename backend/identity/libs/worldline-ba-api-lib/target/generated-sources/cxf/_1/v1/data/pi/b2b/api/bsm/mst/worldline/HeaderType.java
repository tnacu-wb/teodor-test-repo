
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Standard header type (may be extended in the future)
 * 
 * &lt;p&gt;Java class for HeaderType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="HeaderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ClientMessageId"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CultureCode" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CultureCodeType"/&gt;
 *         &lt;element name="UserIPv4" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}IPV4Address" minOccurs="0"/&gt;
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
@XmlType(name = "HeaderType", propOrder = {
    "clientMessageId",
    "cultureCode",
    "userIPv4",
    "customAttributes"
})
public class HeaderType {

    /**
     * Message identifier created by original client
     * 
     */
    @XmlElement(name = "ClientMessageId", required = true)
    protected String clientMessageId;
    /**
     * Culture code for the request.  Initially the API will only support en-GB.
     * 
     */
    @XmlElement(name = "CultureCode", required = true)
    protected String cultureCode;
    /**
     * IP Address of end user if known (the person at the browser)
     * 
     */
    @XmlElement(name = "UserIPv4")
    protected String userIPv4;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Message identifier created by original client
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientMessageId() {
        return clientMessageId;
    }

    /**
     * Sets the value of the clientMessageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getClientMessageId()
     */
    public void setClientMessageId(String value) {
        this.clientMessageId = value;
    }

    /**
     * Culture code for the request.  Initially the API will only support en-GB.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCultureCode() {
        return cultureCode;
    }

    /**
     * Sets the value of the cultureCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCultureCode()
     */
    public void setCultureCode(String value) {
        this.cultureCode = value;
    }

    /**
     * IP Address of end user if known (the person at the browser)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserIPv4() {
        return userIPv4;
    }

    /**
     * Sets the value of the userIPv4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getUserIPv4()
     */
    public void setUserIPv4(String value) {
        this.userIPv4 = value;
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
