
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BounceEvent complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BounceEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}TrackingEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SMTPCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BounceCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SMTPReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BounceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BounceEvent", propOrder = {
    "smtpCode",
    "bounceCategory",
    "smtpReason",
    "bounceType"
})
public class BounceEvent
    extends TrackingEvent
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SMTPCode")
    protected String smtpCode;
    @XmlElement(name = "BounceCategory")
    protected String bounceCategory;
    @XmlElement(name = "SMTPReason")
    protected String smtpReason;
    @XmlElement(name = "BounceType")
    protected String bounceType;

    /**
     * Gets the value of the smtpCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPCode() {
        return smtpCode;
    }

    /**
     * Sets the value of the smtpCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPCode(String value) {
        this.smtpCode = value;
    }

    /**
     * Gets the value of the bounceCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBounceCategory() {
        return bounceCategory;
    }

    /**
     * Sets the value of the bounceCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBounceCategory(String value) {
        this.bounceCategory = value;
    }

    /**
     * Gets the value of the smtpReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPReason() {
        return smtpReason;
    }

    /**
     * Sets the value of the smtpReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPReason(String value) {
        this.smtpReason = value;
    }

    /**
     * Gets the value of the bounceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBounceType() {
        return bounceType;
    }

    /**
     * Sets the value of the bounceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBounceType(String value) {
        this.bounceType = value;
    }

}
