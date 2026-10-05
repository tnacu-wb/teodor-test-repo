
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpdateCompanyPaymentDetailsResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpdateCompanyPaymentDetailsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *         &lt;element name="cardResponse" type="{http://corporate.micros.com/1.0}ArrayOfListItemListItem"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateCompanyPaymentDetailsResponse", propOrder = {
    "success",
    "errors",
    "cardResponse"
})
public class UpdateCompanyPaymentDetailsResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean success;
    protected ArrayOferrorError errors;
    @XmlElement(required = true)
    protected ArrayOfListItemListItem cardResponse;

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
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOferrorError }
     *     
     */
    public ArrayOferrorError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOferrorError }
     *     
     */
    public void setErrors(ArrayOferrorError value) {
        this.errors = value;
    }

    /**
     * Gets the value of the cardResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getCardResponse() {
        return cardResponse;
    }

    /**
     * Sets the value of the cardResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setCardResponse(ArrayOfListItemListItem value) {
        this.cardResponse = value;
    }

}
