
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="resetPasswordRequestResult" type="{http://corporate.micros.com/1.0}ResetPasswordRequestResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resetPasswordRequestResult"
})
@XmlRootElement(name = "resetPasswordRequestResponse")
public class ResetPasswordRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ResetPasswordRequestResponse2 resetPasswordRequestResult;

    /**
     * Gets the value of the resetPasswordRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link ResetPasswordRequestResponse2 }
     *     
     */
    public ResetPasswordRequestResponse2 getResetPasswordRequestResult() {
        return resetPasswordRequestResult;
    }

    /**
     * Sets the value of the resetPasswordRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResetPasswordRequestResponse2 }
     *     
     */
    public void setResetPasswordRequestResult(ResetPasswordRequestResponse2 value) {
        this.resetPasswordRequestResult = value;
    }

}
