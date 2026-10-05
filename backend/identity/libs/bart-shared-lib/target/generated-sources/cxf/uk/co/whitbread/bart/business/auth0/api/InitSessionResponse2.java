
package uk.co.whitbread.bart.business.auth0.api;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for InitSessionResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="InitSessionResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="companyID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="employee" type="{http://corporate.micros.com/1.0}Employee2"/&gt;
 *         &lt;element name="basicCompanyDetails" type="{http://corporate.micros.com/1.0}BasicCompanyDetails2"/&gt;
 *         &lt;element name="setupDetails" type="{http://corporate.micros.com/1.0}CompanySetupDetails2" minOccurs="0"/&gt;
 *         &lt;element name="numberOfEmployees" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="upcomingBookings" type="{http://corporate.micros.com/1.0}ArrayOfUpcomingBookingSummary" minOccurs="0"/&gt;
 *         &lt;element name="setupProgress"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="COMPANY_DETAILS"/&gt;
 *               &lt;enumeration value="PAYMENT_OPTIONS"/&gt;
 *               &lt;enumeration value="MANAGEMENT_INFORMATION"/&gt;
 *               &lt;enumeration value="BOOKING_ALLOWANCES"/&gt;
 *               &lt;enumeration value="BOOKING_ALERTS"/&gt;
 *               &lt;enumeration value="COMPLETE"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="miSetupRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="changeCompanyName" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="awaitingApproval" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="tetheredGuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MyPILink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dismissMPILink" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError2" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InitSessionResponse", propOrder = {
    "sessionID",
    "companyID",
    "employee",
    "basicCompanyDetails",
    "setupDetails",
    "numberOfEmployees",
    "upcomingBookings",
    "setupProgress",
    "miSetupRequired",
    "changeCompanyName",
    "awaitingApproval",
    "tetheredGuid",
    "myPILink",
    "dismissMPILink",
    "success",
    "errors"
})
public class InitSessionResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String companyID;
    @XmlElement(required = true)
    protected Employee2 employee;
    @XmlElement(required = true)
    protected BasicCompanyDetails2 basicCompanyDetails;
    protected CompanySetupDetails2 setupDetails;
    protected BigDecimal numberOfEmployees;
    protected ArrayOfUpcomingBookingSummary upcomingBookings;
    @XmlElement(required = true)
    protected String setupProgress;
    protected boolean miSetupRequired;
    protected boolean changeCompanyName;
    protected BigDecimal awaitingApproval;
    protected String tetheredGuid;
    @XmlElement(name = "MyPILink")
    protected String myPILink;
    protected boolean dismissMPILink;
    protected boolean success;
    protected ArrayOferrorError2 errors;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the companyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyID(String value) {
        this.companyID = value;
    }

    /**
     * Gets the value of the employee property.
     * 
     * @return
     *     possible object is
     *     {@link Employee2 }
     *     
     */
    public Employee2 getEmployee() {
        return employee;
    }

    /**
     * Sets the value of the employee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Employee2 }
     *     
     */
    public void setEmployee(Employee2 value) {
        this.employee = value;
    }

    /**
     * Gets the value of the basicCompanyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BasicCompanyDetails2 }
     *     
     */
    public BasicCompanyDetails2 getBasicCompanyDetails() {
        return basicCompanyDetails;
    }

    /**
     * Sets the value of the basicCompanyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicCompanyDetails2 }
     *     
     */
    public void setBasicCompanyDetails(BasicCompanyDetails2 value) {
        this.basicCompanyDetails = value;
    }

    /**
     * Gets the value of the setupDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CompanySetupDetails2 }
     *     
     */
    public CompanySetupDetails2 getSetupDetails() {
        return setupDetails;
    }

    /**
     * Sets the value of the setupDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanySetupDetails2 }
     *     
     */
    public void setSetupDetails(CompanySetupDetails2 value) {
        this.setupDetails = value;
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

    /**
     * Gets the value of the upcomingBookings property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpcomingBookingSummary }
     *     
     */
    public ArrayOfUpcomingBookingSummary getUpcomingBookings() {
        return upcomingBookings;
    }

    /**
     * Sets the value of the upcomingBookings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpcomingBookingSummary }
     *     
     */
    public void setUpcomingBookings(ArrayOfUpcomingBookingSummary value) {
        this.upcomingBookings = value;
    }

    /**
     * Gets the value of the setupProgress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetupProgress() {
        return setupProgress;
    }

    /**
     * Sets the value of the setupProgress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetupProgress(String value) {
        this.setupProgress = value;
    }

    /**
     * Gets the value of the miSetupRequired property.
     * 
     */
    public boolean isMiSetupRequired() {
        return miSetupRequired;
    }

    /**
     * Sets the value of the miSetupRequired property.
     * 
     */
    public void setMiSetupRequired(boolean value) {
        this.miSetupRequired = value;
    }

    /**
     * Gets the value of the changeCompanyName property.
     * 
     */
    public boolean isChangeCompanyName() {
        return changeCompanyName;
    }

    /**
     * Sets the value of the changeCompanyName property.
     * 
     */
    public void setChangeCompanyName(boolean value) {
        this.changeCompanyName = value;
    }

    /**
     * Gets the value of the awaitingApproval property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAwaitingApproval() {
        return awaitingApproval;
    }

    /**
     * Sets the value of the awaitingApproval property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAwaitingApproval(BigDecimal value) {
        this.awaitingApproval = value;
    }

    /**
     * Gets the value of the tetheredGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTetheredGuid() {
        return tetheredGuid;
    }

    /**
     * Sets the value of the tetheredGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTetheredGuid(String value) {
        this.tetheredGuid = value;
    }

    /**
     * Gets the value of the myPILink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyPILink() {
        return myPILink;
    }

    /**
     * Sets the value of the myPILink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyPILink(String value) {
        this.myPILink = value;
    }

    /**
     * Gets the value of the dismissMPILink property.
     * 
     */
    public boolean isDismissMPILink() {
        return dismissMPILink;
    }

    /**
     * Sets the value of the dismissMPILink property.
     * 
     */
    public void setDismissMPILink(boolean value) {
        this.dismissMPILink = value;
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
     *     {@link ArrayOferrorError2 }
     *     
     */
    public ArrayOferrorError2 getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOferrorError2 }
     *     
     */
    public void setErrors(ArrayOferrorError2 value) {
        this.errors = value;
    }

}
