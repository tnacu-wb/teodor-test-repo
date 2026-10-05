
package uk.co.whitbread.azure.email.api;

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
 *         &lt;element name="out" type="{https://dto.email.transact.comms.int.wtbapi.com}MiscTextEmailResponse"/&gt;
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
    "out"
})
@XmlRootElement(name = "sendMiscellaneousTextEmailsResponse", namespace = "https://email.transact.comms.int.wtbapi.com")
public class SendMiscellaneousTextEmailsResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(namespace = "https://email.transact.comms.int.wtbapi.com", required = true, nillable = true)
    protected MiscTextEmailResponse out;

    /**
     * Gets the value of the out property.
     * 
     * @return
     *     possible object is
     *     {@link MiscTextEmailResponse }
     *     
     */
    public MiscTextEmailResponse getOut() {
        return out;
    }

    /**
     * Sets the value of the out property.
     * 
     * @param value
     *     allowed object is
     *     {@link MiscTextEmailResponse }
     *     
     */
    public void setOut(MiscTextEmailResponse value) {
        this.out = value;
    }

}
