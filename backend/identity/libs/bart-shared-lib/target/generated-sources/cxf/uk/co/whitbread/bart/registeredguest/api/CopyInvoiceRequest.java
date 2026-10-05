
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
 *         &lt;element name="CopyInvoiceDetails" type="{http://bartws.micros.com/1.13}CopyInvoiceRequest" minOccurs="0"/&gt;
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
    "copyInvoiceDetails"
})
@XmlRootElement(name = "CopyInvoiceRequest")
public class CopyInvoiceRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CopyInvoiceDetails")
    protected CopyInvoiceRequest2 copyInvoiceDetails;

    /**
     * Gets the value of the copyInvoiceDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CopyInvoiceRequest2 }
     *     
     */
    public CopyInvoiceRequest2 getCopyInvoiceDetails() {
        return copyInvoiceDetails;
    }

    /**
     * Sets the value of the copyInvoiceDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyInvoiceRequest2 }
     *     
     */
    public void setCopyInvoiceDetails(CopyInvoiceRequest2 value) {
        this.copyInvoiceDetails = value;
    }

}
