
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookerDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookerDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bookingForename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bookingSurname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bookingTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookerDetails", propOrder = {
    "bookingForename",
    "bookingSurname",
    "bookingTitle",
    "emailAddress"
})
public class BookerDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String bookingForename;
    @XmlElement(nillable = true)
    protected String bookingSurname;
    @XmlElement(nillable = true)
    protected String bookingTitle;
    @XmlElement(nillable = true)
    protected String emailAddress;

    /**
     * Gets the value of the bookingForename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingForename() {
        return bookingForename;
    }

    /**
     * Sets the value of the bookingForename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingForename(String value) {
        this.bookingForename = value;
    }

    /**
     * Gets the value of the bookingSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingSurname() {
        return bookingSurname;
    }

    /**
     * Sets the value of the bookingSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingSurname(String value) {
        this.bookingSurname = value;
    }

    /**
     * Gets the value of the bookingTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingTitle() {
        return bookingTitle;
    }

    /**
     * Sets the value of the bookingTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingTitle(String value) {
        this.bookingTitle = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

}
