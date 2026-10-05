
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for IsEmailRegisteredResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="IsEmailRegisteredResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="isRegistered" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="isRegisteredError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.13}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IsEmailRegisteredResponse", propOrder = {
    "isRegistered",
    "isRegisteredError",
    "errorDetail"
})
public class IsEmailRegisteredResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean isRegistered;
    protected String isRegisteredError;
    protected ErrorDetails errorDetail;

    /**
     * Gets the value of the isRegistered property.
     * 
     */
    public boolean isIsRegistered() {
        return isRegistered;
    }

    /**
     * Sets the value of the isRegistered property.
     * 
     */
    public void setIsRegistered(boolean value) {
        this.isRegistered = value;
    }

    /**
     * Gets the value of the isRegisteredError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsRegisteredError() {
        return isRegisteredError;
    }

    /**
     * Sets the value of the isRegisteredError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsRegisteredError(String value) {
        this.isRegisteredError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetail(ErrorDetails value) {
        this.errorDetail = value;
    }

}
