
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for NextArrival2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="NextArrival2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="historyRecordNumber" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="checkInOnline" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="isCheckedIn" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NextArrival2", propOrder = {
    "hotelCode",
    "historyRecordNumber",
    "arrivalDate",
    "departureDate",
    "checkInOnline",
    "isCheckedIn"
})
public class NextArrival2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String hotelCode;
    protected long historyRecordNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate arrivalDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate departureDate;
    protected boolean checkInOnline;
    protected boolean isCheckedIn;

    /**
     * Gets the value of the hotelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelCode() {
        return hotelCode;
    }

    /**
     * Sets the value of the hotelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelCode(String value) {
        this.hotelCode = value;
    }

    /**
     * Gets the value of the historyRecordNumber property.
     * 
     */
    public long getHistoryRecordNumber() {
        return historyRecordNumber;
    }

    /**
     * Sets the value of the historyRecordNumber property.
     * 
     */
    public void setHistoryRecordNumber(long value) {
        this.historyRecordNumber = value;
    }

    /**
     * Gets the value of the arrivalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    /**
     * Sets the value of the arrivalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalDate(LocalDate value) {
        this.arrivalDate = value;
    }

    /**
     * Gets the value of the departureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the value of the departureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDate(LocalDate value) {
        this.departureDate = value;
    }

    /**
     * Gets the value of the checkInOnline property.
     * 
     */
    public boolean isCheckInOnline() {
        return checkInOnline;
    }

    /**
     * Sets the value of the checkInOnline property.
     * 
     */
    public void setCheckInOnline(boolean value) {
        this.checkInOnline = value;
    }

    /**
     * Gets the value of the isCheckedIn property.
     * 
     */
    public boolean isIsCheckedIn() {
        return isCheckedIn;
    }

    /**
     * Sets the value of the isCheckedIn property.
     * 
     */
    public void setIsCheckedIn(boolean value) {
        this.isCheckedIn = value;
    }

}
