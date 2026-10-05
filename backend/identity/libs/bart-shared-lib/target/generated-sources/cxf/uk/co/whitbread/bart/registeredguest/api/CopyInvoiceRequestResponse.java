
package uk.co.whitbread.bart.registeredguest.api;

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
 *         &lt;element name="CopyInvoiceRequestResult" type="{http://bartws.micros.com/1.13}CopyInvoiceResponse"/&gt;
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
    "copyInvoiceRequestResult"
})
@XmlRootElement(name = "CopyInvoiceRequestResponse")
public class CopyInvoiceRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CopyInvoiceRequestResult", required = true)
    protected CopyInvoiceResponse copyInvoiceRequestResult;

    /**
     * Gets the value of the copyInvoiceRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link CopyInvoiceResponse }
     *     
     */
    public CopyInvoiceResponse getCopyInvoiceRequestResult() {
        return copyInvoiceRequestResult;
    }

    /**
     * Sets the value of the copyInvoiceRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyInvoiceResponse }
     *     
     */
    public void setCopyInvoiceRequestResult(CopyInvoiceResponse value) {
        this.copyInvoiceRequestResult = value;
    }

}
