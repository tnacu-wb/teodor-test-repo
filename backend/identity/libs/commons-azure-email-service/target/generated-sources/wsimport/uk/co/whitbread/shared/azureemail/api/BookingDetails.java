
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BookingDetails complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="BookingDetails">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="arrivalDay" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="arrivalMonth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="arrivalYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="currencyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="languageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="mpiRes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numberNights" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="paidStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="resNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingDetails", propOrder = {
    "arrivalDate",
    "arrivalDay",
    "arrivalMonth",
    "arrivalYear",
    "currencyCode",
    "departureDate",
    "hotelCode",
    "hotelName",
    "languageCode",
    "mpiRes",
    "numberNights",
    "paidStatus",
    "resNumber"
})
public class BookingDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String arrivalDate;
    @XmlElement(nillable = true)
    protected String arrivalDay;
    @XmlElement(nillable = true)
    protected String arrivalMonth;
    @XmlElement(nillable = true)
    protected String arrivalYear;
    @XmlElement(nillable = true)
    protected String currencyCode;
    @XmlElement(nillable = true)
    protected String departureDate;
    @XmlElement(nillable = true)
    protected String hotelCode;
    @XmlElement(nillable = true)
    protected String hotelName;
    @XmlElement(nillable = true)
    protected String languageCode;
    @XmlElement(nillable = true)
    protected String mpiRes;
    protected Integer numberNights;
    @XmlElement(nillable = true)
    protected String paidStatus;
    @XmlElement(nillable = true)
    protected String resNumber;

    /**
     * Gets the value of the arrivalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalDate() {
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
    public void setArrivalDate(String value) {
        this.arrivalDate = value;
    }

    /**
     * Gets the value of the arrivalDay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalDay() {
        return arrivalDay;
    }

    /**
     * Sets the value of the arrivalDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalDay(String value) {
        this.arrivalDay = value;
    }

    /**
     * Gets the value of the arrivalMonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalMonth() {
        return arrivalMonth;
    }

    /**
     * Sets the value of the arrivalMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalMonth(String value) {
        this.arrivalMonth = value;
    }

    /**
     * Gets the value of the arrivalYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalYear() {
        return arrivalYear;
    }

    /**
     * Sets the value of the arrivalYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalYear(String value) {
        this.arrivalYear = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the departureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureDate() {
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
    public void setDepartureDate(String value) {
        this.departureDate = value;
    }

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
     * Gets the value of the hotelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Sets the value of the hotelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelName(String value) {
        this.hotelName = value;
    }

    /**
     * Gets the value of the languageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the value of the languageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageCode(String value) {
        this.languageCode = value;
    }

    /**
     * Gets the value of the mpiRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMpiRes() {
        return mpiRes;
    }

    /**
     * Sets the value of the mpiRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMpiRes(String value) {
        this.mpiRes = value;
    }

    /**
     * Gets the value of the numberNights property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberNights() {
        return numberNights;
    }

    /**
     * Sets the value of the numberNights property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberNights(Integer value) {
        this.numberNights = value;
    }

    /**
     * Gets the value of the paidStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaidStatus() {
        return paidStatus;
    }

    /**
     * Sets the value of the paidStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaidStatus(String value) {
        this.paidStatus = value;
    }

    /**
     * Gets the value of the resNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResNumber() {
        return resNumber;
    }

    /**
     * Sets the value of the resNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResNumber(String value) {
        this.resNumber = value;
    }

}
