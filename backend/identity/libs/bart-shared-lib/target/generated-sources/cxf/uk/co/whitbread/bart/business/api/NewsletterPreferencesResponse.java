
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
 *         &lt;element name="NewsletterPreferencesResult" type="{http://corporate.micros.com/1.0}NewsletterPreferencesResponse"/&gt;
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
    "newsletterPreferencesResult"
})
@XmlRootElement(name = "NewsletterPreferencesResponse")
public class NewsletterPreferencesResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "NewsletterPreferencesResult", required = true)
    protected NewsletterPreferencesResponse2 newsletterPreferencesResult;

    /**
     * Gets the value of the newsletterPreferencesResult property.
     * 
     * @return
     *     possible object is
     *     {@link NewsletterPreferencesResponse2 }
     *     
     */
    public NewsletterPreferencesResponse2 getNewsletterPreferencesResult() {
        return newsletterPreferencesResult;
    }

    /**
     * Sets the value of the newsletterPreferencesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link NewsletterPreferencesResponse2 }
     *     
     */
    public void setNewsletterPreferencesResult(NewsletterPreferencesResponse2 value) {
        this.newsletterPreferencesResult = value;
    }

}
