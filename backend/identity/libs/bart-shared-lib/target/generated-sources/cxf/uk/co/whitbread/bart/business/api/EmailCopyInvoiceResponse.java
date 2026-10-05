
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
 *         &lt;element name="emailCopyInvoiceResult" type="{http://corporate.micros.com/1.0}EmailCopyInvoiceResponse"/&gt;
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
    "emailCopyInvoiceResult"
})
@XmlRootElement(name = "emailCopyInvoiceResponse")
public class EmailCopyInvoiceResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected EmailCopyInvoiceResponse2 emailCopyInvoiceResult;

    /**
     * Gets the value of the emailCopyInvoiceResult property.
     * 
     * @return
     *     possible object is
     *     {@link EmailCopyInvoiceResponse2 }
     *     
     */
    public EmailCopyInvoiceResponse2 getEmailCopyInvoiceResult() {
        return emailCopyInvoiceResult;
    }

    /**
     * Sets the value of the emailCopyInvoiceResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailCopyInvoiceResponse2 }
     *     
     */
    public void setEmailCopyInvoiceResult(EmailCopyInvoiceResponse2 value) {
        this.emailCopyInvoiceResult = value;
    }

}
