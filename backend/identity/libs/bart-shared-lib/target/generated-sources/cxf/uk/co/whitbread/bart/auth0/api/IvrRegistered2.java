
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ivrRegistered2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ivrRegistered2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ivrType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="TELEPHONE"/&gt;
 *               &lt;enumeration value="MOBILE"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ivrPin"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="6"/&gt;
 *               &lt;minLength value="6"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="existingPin" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="6"/&gt;
 *               &lt;minLength value="6"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ivrRegistered2", propOrder = {
    "ivrType",
    "ivrPin",
    "existingPin"
})
public class IvrRegistered2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String ivrType;
    @XmlElement(required = true)
    protected String ivrPin;
    protected String existingPin;

    /**
     * Gets the value of the ivrType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIvrType() {
        return ivrType;
    }

    /**
     * Sets the value of the ivrType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIvrType(String value) {
        this.ivrType = value;
    }

    /**
     * Gets the value of the ivrPin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIvrPin() {
        return ivrPin;
    }

    /**
     * Sets the value of the ivrPin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIvrPin(String value) {
        this.ivrPin = value;
    }

    /**
     * Gets the value of the existingPin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExistingPin() {
        return existingPin;
    }

    /**
     * Sets the value of the existingPin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExistingPin(String value) {
        this.existingPin = value;
    }

}
