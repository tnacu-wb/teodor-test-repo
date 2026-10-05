
package uk.co.whitbread.bart.selfregister.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ProfileNewsletterPreferences complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ProfileNewsletterPreferences"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="newsletterUK" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="newsletterDubai" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="newsletterIndia" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="restaurant" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfileNewsletterPreferences", propOrder = {
    "newsletterUK",
    "newsletterDubai",
    "newsletterIndia",
    "restaurant"
})
public class ProfileNewsletterPreferences
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Boolean newsletterUK;
    protected Boolean newsletterDubai;
    protected Boolean newsletterIndia;
    protected Boolean restaurant;

    /**
     * Gets the value of the newsletterUK property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewsletterUK() {
        return newsletterUK;
    }

    /**
     * Sets the value of the newsletterUK property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewsletterUK(Boolean value) {
        this.newsletterUK = value;
    }

    /**
     * Gets the value of the newsletterDubai property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewsletterDubai() {
        return newsletterDubai;
    }

    /**
     * Sets the value of the newsletterDubai property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewsletterDubai(Boolean value) {
        this.newsletterDubai = value;
    }

    /**
     * Gets the value of the newsletterIndia property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewsletterIndia() {
        return newsletterIndia;
    }

    /**
     * Sets the value of the newsletterIndia property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewsletterIndia(Boolean value) {
        this.newsletterIndia = value;
    }

    /**
     * Gets the value of the restaurant property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRestaurant() {
        return restaurant;
    }

    /**
     * Sets the value of the restaurant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRestaurant(Boolean value) {
        this.restaurant = value;
    }

}
