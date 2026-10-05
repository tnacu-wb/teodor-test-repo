
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AvailabilityResult3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AvailabilityResult3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="availableRates" type="{http://bartws.micros.com/1.31}ArrayOfratePlanAvailableRates3"/&gt;
 *         &lt;element name="vatRate" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="limitedAvailability" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="prepaymentAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="threeDSecureMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CNPAuthorisation" type="{http://bartws.micros.com/1.31}CNPAuthorisation" minOccurs="0"/&gt;
 *         &lt;element name="breakfastsAvailable" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastBreakfast"/&gt;
 *         &lt;element name="foodBeverageExclusions" type="{http://bartws.micros.com/1.31}ArrayOffoodBeverageExclusionFoodBeverageExclusions" minOccurs="0"/&gt;
 *         &lt;element name="availabilityError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_PROPERTY_CODE_SUPPLIED"/&gt;
 *               &lt;enumeration value="BOOKINGS_NOT_ACCEPTED_AT_SPECIFIED_PROPERTY"/&gt;
 *               &lt;enumeration value="NO_AVAILABILITY_AT_SPECIFIED_PROPERTY"/&gt;
 *               &lt;enumeration value="INVALID_ARRIVAL_DATE"/&gt;
 *               &lt;enumeration value="INVALID_DEPARTURE_DATE"/&gt;
 *               &lt;enumeration value="ARRIVAL_MUST_NOT_NOT_BE_BEFORE_TODAY"/&gt;
 *               &lt;enumeration value="ARRIVAL_TOO_FAR_IN_ADVANCE"/&gt;
 *               &lt;enumeration value="DEPARTURE_TOO_FAR_IN_ADVANCE"/&gt;
 *               &lt;enumeration value="TOO_LATE_TO_BOOK_ON_DAY_OF_ARRIVAL"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_ROOMS"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_ADULTS"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_CHILDREN"/&gt;
 *               &lt;enumeration value="INVALID_COT_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_ROOM_REQUIREMENTS"/&gt;
 *               &lt;enumeration value="INVALID_CELL_CODE"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_NIGHTS"/&gt;
 *               &lt;enumeration value="INVALID_ROOM_TYPE"/&gt;
 *               &lt;enumeration value="ROOMS_ALREADY_HELD"/&gt;
 *               &lt;enumeration value="AMEND_ONLINE_FORBIDDEN"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="specialEvents" type="{http://bartws.micros.com/1.31}ArrayOfspecialEventSpecialEvent" minOccurs="0"/&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.31}ErrorDetails" minOccurs="0"/&gt;
 *         &lt;element name="cellCodeAvailability" type="{http://bartws.micros.com/1.31}ArrayOfCellCode" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAvailableNonBA" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="requestIsAGroup" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cityTaxForLeisure" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cityTaxForBusiness" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="paymentProvider" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="3CP"/&gt;
 *               &lt;enumeration value="DATACASH_3DS_V1"/&gt;
 *               &lt;enumeration value="DATACASH_3DS_V2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilityResult3", propOrder = {
    "hotelCode",
    "availableRates",
    "vatRate",
    "limitedAvailability",
    "prepaymentAllowed",
    "threeDSecureMethod",
    "cnpAuthorisation",
    "breakfastsAvailable",
    "foodBeverageExclusions",
    "availabilityError",
    "specialEvents",
    "errorDetail",
    "cellCodeAvailability",
    "dinnerAvailable",
    "dinnerAvailableNonBA",
    "requestIsAGroup",
    "cityTaxForLeisure",
    "cityTaxForBusiness",
    "paymentProvider"
})
public class AvailabilityResult3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String hotelCode;
    @XmlElement(required = true)
    protected ArrayOfratePlanAvailableRates3 availableRates;
    protected Double vatRate;
    protected Boolean limitedAvailability;
    protected Boolean prepaymentAllowed;
    protected String threeDSecureMethod;
    @XmlElement(name = "CNPAuthorisation")
    protected CNPAuthorisation cnpAuthorisation;
    @XmlElement(required = true)
    protected ArrayOfbreakfastBreakfast breakfastsAvailable;
    protected ArrayOffoodBeverageExclusionFoodBeverageExclusions foodBeverageExclusions;
    protected String availabilityError;
    protected ArrayOfspecialEventSpecialEvent specialEvents;
    protected ErrorDetails errorDetail;
    protected ArrayOfCellCode cellCodeAvailability;
    protected Boolean dinnerAvailable;
    protected Boolean dinnerAvailableNonBA;
    protected Boolean requestIsAGroup;
    protected Boolean cityTaxForLeisure;
    protected Boolean cityTaxForBusiness;
    protected String paymentProvider;

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
     * Gets the value of the availableRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfratePlanAvailableRates3 }
     *     
     */
    public ArrayOfratePlanAvailableRates3 getAvailableRates() {
        return availableRates;
    }

    /**
     * Sets the value of the availableRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfratePlanAvailableRates3 }
     *     
     */
    public void setAvailableRates(ArrayOfratePlanAvailableRates3 value) {
        this.availableRates = value;
    }

    /**
     * Gets the value of the vatRate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVatRate() {
        return vatRate;
    }

    /**
     * Sets the value of the vatRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVatRate(Double value) {
        this.vatRate = value;
    }

    /**
     * Gets the value of the limitedAvailability property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLimitedAvailability() {
        return limitedAvailability;
    }

    /**
     * Sets the value of the limitedAvailability property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLimitedAvailability(Boolean value) {
        this.limitedAvailability = value;
    }

    /**
     * Gets the value of the prepaymentAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrepaymentAllowed() {
        return prepaymentAllowed;
    }

    /**
     * Sets the value of the prepaymentAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrepaymentAllowed(Boolean value) {
        this.prepaymentAllowed = value;
    }

    /**
     * Gets the value of the threeDSecureMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThreeDSecureMethod() {
        return threeDSecureMethod;
    }

    /**
     * Sets the value of the threeDSecureMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThreeDSecureMethod(String value) {
        this.threeDSecureMethod = value;
    }

    /**
     * Gets the value of the cnpAuthorisation property.
     * 
     * @return
     *     possible object is
     *     {@link CNPAuthorisation }
     *     
     */
    public CNPAuthorisation getCNPAuthorisation() {
        return cnpAuthorisation;
    }

    /**
     * Sets the value of the cnpAuthorisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CNPAuthorisation }
     *     
     */
    public void setCNPAuthorisation(CNPAuthorisation value) {
        this.cnpAuthorisation = value;
    }

    /**
     * Gets the value of the breakfastsAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public ArrayOfbreakfastBreakfast getBreakfastsAvailable() {
        return breakfastsAvailable;
    }

    /**
     * Sets the value of the breakfastsAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public void setBreakfastsAvailable(ArrayOfbreakfastBreakfast value) {
        this.breakfastsAvailable = value;
    }

    /**
     * Gets the value of the foodBeverageExclusions property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOffoodBeverageExclusionFoodBeverageExclusions }
     *     
     */
    public ArrayOffoodBeverageExclusionFoodBeverageExclusions getFoodBeverageExclusions() {
        return foodBeverageExclusions;
    }

    /**
     * Sets the value of the foodBeverageExclusions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOffoodBeverageExclusionFoodBeverageExclusions }
     *     
     */
    public void setFoodBeverageExclusions(ArrayOffoodBeverageExclusionFoodBeverageExclusions value) {
        this.foodBeverageExclusions = value;
    }

    /**
     * Gets the value of the availabilityError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvailabilityError() {
        return availabilityError;
    }

    /**
     * Sets the value of the availabilityError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailabilityError(String value) {
        this.availabilityError = value;
    }

    /**
     * Gets the value of the specialEvents property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfspecialEventSpecialEvent }
     *     
     */
    public ArrayOfspecialEventSpecialEvent getSpecialEvents() {
        return specialEvents;
    }

    /**
     * Sets the value of the specialEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfspecialEventSpecialEvent }
     *     
     */
    public void setSpecialEvents(ArrayOfspecialEventSpecialEvent value) {
        this.specialEvents = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetail(ErrorDetails value) {
        this.errorDetail = value;
    }

    /**
     * Gets the value of the cellCodeAvailability property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCellCode }
     *     
     */
    public ArrayOfCellCode getCellCodeAvailability() {
        return cellCodeAvailability;
    }

    /**
     * Sets the value of the cellCodeAvailability property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCellCode }
     *     
     */
    public void setCellCodeAvailability(ArrayOfCellCode value) {
        this.cellCodeAvailability = value;
    }

    /**
     * Gets the value of the dinnerAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailable() {
        return dinnerAvailable;
    }

    /**
     * Sets the value of the dinnerAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailable(Boolean value) {
        this.dinnerAvailable = value;
    }

    /**
     * Gets the value of the dinnerAvailableNonBA property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailableNonBA() {
        return dinnerAvailableNonBA;
    }

    /**
     * Sets the value of the dinnerAvailableNonBA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailableNonBA(Boolean value) {
        this.dinnerAvailableNonBA = value;
    }

    /**
     * Gets the value of the requestIsAGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequestIsAGroup() {
        return requestIsAGroup;
    }

    /**
     * Sets the value of the requestIsAGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequestIsAGroup(Boolean value) {
        this.requestIsAGroup = value;
    }

    /**
     * Gets the value of the cityTaxForLeisure property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCityTaxForLeisure() {
        return cityTaxForLeisure;
    }

    /**
     * Sets the value of the cityTaxForLeisure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCityTaxForLeisure(Boolean value) {
        this.cityTaxForLeisure = value;
    }

    /**
     * Gets the value of the cityTaxForBusiness property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCityTaxForBusiness() {
        return cityTaxForBusiness;
    }

    /**
     * Sets the value of the cityTaxForBusiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCityTaxForBusiness(Boolean value) {
        this.cityTaxForBusiness = value;
    }

    /**
     * Gets the value of the paymentProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentProvider() {
        return paymentProvider;
    }

    /**
     * Sets the value of the paymentProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentProvider(String value) {
        this.paymentProvider = value;
    }

}
