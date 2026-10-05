
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ProfileDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ProfileDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="personalDetails" type="{http://corporate.micros.com/1.0}ProfilePersonalDetails"/&gt;
 *         &lt;element name="bookingPreferences" type="{http://corporate.micros.com/1.0}BookingPreferences"/&gt;
 *         &lt;element name="paymentOption" type="{http://corporate.micros.com/1.0}ProfilePaymentOption"/&gt;
 *         &lt;element name="newsletterPreferences" type="{http://corporate.micros.com/1.0}ProfileNewsletterPreferences"/&gt;
 *         &lt;element name="profileLocked" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfileDetails", propOrder = {
    "personalDetails",
    "bookingPreferences",
    "paymentOption",
    "newsletterPreferences",
    "profileLocked"
})
public class ProfileDetails2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ProfilePersonalDetails personalDetails;
    @XmlElement(required = true)
    protected BookingPreferences bookingPreferences;
    @XmlElement(required = true)
    protected ProfilePaymentOption paymentOption;
    @XmlElement(required = true)
    protected ProfileNewsletterPreferences newsletterPreferences;
    protected boolean profileLocked;

    /**
     * Gets the value of the personalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ProfilePersonalDetails }
     *     
     */
    public ProfilePersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    /**
     * Sets the value of the personalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfilePersonalDetails }
     *     
     */
    public void setPersonalDetails(ProfilePersonalDetails value) {
        this.personalDetails = value;
    }

    /**
     * Gets the value of the bookingPreferences property.
     * 
     * @return
     *     possible object is
     *     {@link BookingPreferences }
     *     
     */
    public BookingPreferences getBookingPreferences() {
        return bookingPreferences;
    }

    /**
     * Sets the value of the bookingPreferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingPreferences }
     *     
     */
    public void setBookingPreferences(BookingPreferences value) {
        this.bookingPreferences = value;
    }

    /**
     * Gets the value of the paymentOption property.
     * 
     * @return
     *     possible object is
     *     {@link ProfilePaymentOption }
     *     
     */
    public ProfilePaymentOption getPaymentOption() {
        return paymentOption;
    }

    /**
     * Sets the value of the paymentOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfilePaymentOption }
     *     
     */
    public void setPaymentOption(ProfilePaymentOption value) {
        this.paymentOption = value;
    }

    /**
     * Gets the value of the newsletterPreferences property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileNewsletterPreferences }
     *     
     */
    public ProfileNewsletterPreferences getNewsletterPreferences() {
        return newsletterPreferences;
    }

    /**
     * Sets the value of the newsletterPreferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileNewsletterPreferences }
     *     
     */
    public void setNewsletterPreferences(ProfileNewsletterPreferences value) {
        this.newsletterPreferences = value;
    }

    /**
     * Gets the value of the profileLocked property.
     * 
     */
    public boolean isProfileLocked() {
        return profileLocked;
    }

    /**
     * Sets the value of the profileLocked property.
     * 
     */
    public void setProfileLocked(boolean value) {
        this.profileLocked = value;
    }

}
