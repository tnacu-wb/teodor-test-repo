
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingSummaryResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingSummaryResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bookings" type="{http://corporate.micros.com/1.0}ArrayOfBookingSummaryBookingSummary"/&gt;
 *         &lt;element name="endOfFile" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingSummaryResponse", propOrder = {
    "bookings",
    "endOfFile",
    "success",
    "errors"
})
public class BookingSummaryResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfBookingSummaryBookingSummary bookings;
    protected Boolean endOfFile;
    protected boolean success;
    protected ArrayOferrorError errors;

    /**
     * Gets the value of the bookings property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBookingSummaryBookingSummary }
     *     
     */
    public ArrayOfBookingSummaryBookingSummary getBookings() {
        return bookings;
    }

    /**
     * Sets the value of the bookings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBookingSummaryBookingSummary }
     *     
     */
    public void setBookings(ArrayOfBookingSummaryBookingSummary value) {
        this.bookings = value;
    }

    /**
     * Gets the value of the endOfFile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEndOfFile() {
        return endOfFile;
    }

    /**
     * Sets the value of the endOfFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEndOfFile(Boolean value) {
        this.endOfFile = value;
    }

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

}
