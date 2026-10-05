
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ForgottenPasswordResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ForgottenPasswordResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="passwordToken" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="forgottenPasswordError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="NOT_A_REGISTERED_ACCOUNT"/&gt;
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
@XmlType(name = "ForgottenPasswordResponse", propOrder = {
    "passwordToken",
    "success",
    "forgottenPasswordError",
    "errorDetail"
})
public class ForgottenPasswordResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String passwordToken;
    protected boolean success;
    protected String forgottenPasswordError;
    protected ErrorDetails errorDetail;

    /**
     * Gets the value of the passwordToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordToken() {
        return passwordToken;
    }

    /**
     * Sets the value of the passwordToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordToken(String value) {
        this.passwordToken = value;
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the forgottenPasswordError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForgottenPasswordError() {
        return forgottenPasswordError;
    }

    /**
     * Sets the value of the forgottenPasswordError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForgottenPasswordError(String value) {
        this.forgottenPasswordError = value;
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
