
package uk.co.whitbread.bart.booking.api;

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
 *         &lt;element name="PostcodeValidationRequestResult" type="{http://bartws.micros.com/1.31}PostcodeValidationResponse"/&gt;
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
    "postcodeValidationRequestResult"
})
@XmlRootElement(name = "PostcodeValidationRequestResponse")
public class PostcodeValidationRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "PostcodeValidationRequestResult", required = true)
    protected PostcodeValidationResponse postcodeValidationRequestResult;

    /**
     * Gets the value of the postcodeValidationRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link PostcodeValidationResponse }
     *     
     */
    public PostcodeValidationResponse getPostcodeValidationRequestResult() {
        return postcodeValidationRequestResult;
    }

    /**
     * Sets the value of the postcodeValidationRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostcodeValidationResponse }
     *     
     */
    public void setPostcodeValidationRequestResult(PostcodeValidationResponse value) {
        this.postcodeValidationRequestResult = value;
    }

}
