
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CompanyDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CompanyDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="basicCompanyDetails" type="{http://corporate.micros.com/1.0}BasicCompanyDetails"/&gt;
 *         &lt;element name="setupDetails" type="{http://corporate.micros.com/1.0}CompanySetupDetails" minOccurs="0"/&gt;
 *         &lt;element name="companyPaymentDetails" type="{http://corporate.micros.com/1.0}CompanyPaymentDetails" minOccurs="0"/&gt;
 *         &lt;element name="companyMIDetails" type="{http://corporate.micros.com/1.0}CompanyMIDetails" minOccurs="0"/&gt;
 *         &lt;element name="bookingAllowances" type="{http://corporate.micros.com/1.0}BookingAllowances" minOccurs="0"/&gt;
 *         &lt;element name="bookingAlerts" type="{http://corporate.micros.com/1.0}BookingAlerts" minOccurs="0"/&gt;
 *         &lt;element name="numberOfEmployees" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanyDetails", propOrder = {
    "basicCompanyDetails",
    "setupDetails",
    "companyPaymentDetails",
    "companyMIDetails",
    "bookingAllowances",
    "bookingAlerts",
    "numberOfEmployees"
})
public class CompanyDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected BasicCompanyDetails basicCompanyDetails;
    protected CompanySetupDetails setupDetails;
    protected CompanyPaymentDetails companyPaymentDetails;
    protected CompanyMIDetails companyMIDetails;
    protected BookingAllowances bookingAllowances;
    protected BookingAlerts bookingAlerts;
    protected BigDecimal numberOfEmployees;

    /**
     * Gets the value of the basicCompanyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BasicCompanyDetails }
     *     
     */
    public BasicCompanyDetails getBasicCompanyDetails() {
        return basicCompanyDetails;
    }

    /**
     * Sets the value of the basicCompanyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicCompanyDetails }
     *     
     */
    public void setBasicCompanyDetails(BasicCompanyDetails value) {
        this.basicCompanyDetails = value;
    }

    /**
     * Gets the value of the setupDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CompanySetupDetails }
     *     
     */
    public CompanySetupDetails getSetupDetails() {
        return setupDetails;
    }

    /**
     * Sets the value of the setupDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanySetupDetails }
     *     
     */
    public void setSetupDetails(CompanySetupDetails value) {
        this.setupDetails = value;
    }

    /**
     * Gets the value of the companyPaymentDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyPaymentDetails }
     *     
     */
    public CompanyPaymentDetails getCompanyPaymentDetails() {
        return companyPaymentDetails;
    }

    /**
     * Sets the value of the companyPaymentDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyPaymentDetails }
     *     
     */
    public void setCompanyPaymentDetails(CompanyPaymentDetails value) {
        this.companyPaymentDetails = value;
    }

    /**
     * Gets the value of the companyMIDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyMIDetails }
     *     
     */
    public CompanyMIDetails getCompanyMIDetails() {
        return companyMIDetails;
    }

    /**
     * Sets the value of the companyMIDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyMIDetails }
     *     
     */
    public void setCompanyMIDetails(CompanyMIDetails value) {
        this.companyMIDetails = value;
    }

    /**
     * Gets the value of the bookingAllowances property.
     * 
     * @return
     *     possible object is
     *     {@link BookingAllowances }
     *     
     */
    public BookingAllowances getBookingAllowances() {
        return bookingAllowances;
    }

    /**
     * Sets the value of the bookingAllowances property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingAllowances }
     *     
     */
    public void setBookingAllowances(BookingAllowances value) {
        this.bookingAllowances = value;
    }

    /**
     * Gets the value of the bookingAlerts property.
     * 
     * @return
     *     possible object is
     *     {@link BookingAlerts }
     *     
     */
    public BookingAlerts getBookingAlerts() {
        return bookingAlerts;
    }

    /**
     * Sets the value of the bookingAlerts property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingAlerts }
     *     
     */
    public void setBookingAlerts(BookingAlerts value) {
        this.bookingAlerts = value;
    }

    /**
     * Gets the value of the numberOfEmployees property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfEmployees() {
        return numberOfEmployees;
    }

    /**
     * Sets the value of the numberOfEmployees property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfEmployees(BigDecimal value) {
        this.numberOfEmployees = value;
    }

}
