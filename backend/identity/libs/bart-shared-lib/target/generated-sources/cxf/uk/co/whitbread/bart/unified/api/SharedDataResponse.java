
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SharedDataResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SharedDataResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="donations" type="{http://bartws.micros.com/1.17}ArrayOfCharityDonations"/&gt;
 *         &lt;element name="bookingWindows" type="{http://bartws.micros.com/1.17}ArrayOfBookingWindowBookingWindows"/&gt;
 *         &lt;element name="countries" type="{http://bartws.micros.com/1.17}ArrayOfCountryCountryCodes"/&gt;
 *         &lt;element name="titles" type="{http://bartws.micros.com/1.17}ArrayOftitlesItemString"/&gt;
 *         &lt;element name="regions" type="{http://bartws.micros.com/1.17}ArrayOfRegionRegion" minOccurs="0"/&gt;
 *         &lt;element name="breakfasts" type="{http://bartws.micros.com/1.17}ArrayOfbreakfastBreakfast" minOccurs="0"/&gt;
 *         &lt;element name="upsellCategories" type="{http://bartws.micros.com/1.17}ArrayOfUpsellCategoryCategoryList" minOccurs="0"/&gt;
 *         &lt;element name="upsellItems" type="{http://bartws.micros.com/1.17}ArrayOfUpsellItemUpsellItem" minOccurs="0"/&gt;
 *         &lt;element name="internetPrepaymentAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="internetBookingAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="brands" type="{http://bartws.micros.com/1.17}ArrayOfBrandBrands"/&gt;
 *         &lt;element name="hotelBrands" type="{http://bartws.micros.com/1.17}ArrayOfHotelBrandHotelBrands"/&gt;
 *         &lt;element name="lettingTypes" type="{http://bartws.micros.com/1.17}ArrayOfLettingTypeLettingTypes"/&gt;
 *         &lt;element name="facilities" type="{http://bartws.micros.com/1.17}ArrayOfFacilitySummaryFacilitySummaries"/&gt;
 *         &lt;element name="rateClassifications" type="{http://bartws.micros.com/1.17}ArrayOfRateClassificationRateClassifications"/&gt;
 *         &lt;element name="loyaltyPrograms" type="{http://bartws.micros.com/1.17}ArrayOfLoyaltyProgramLoyaltyProgram" minOccurs="0"/&gt;
 *         &lt;element name="creditCardsAccepted" type="{http://bartws.micros.com/1.17}ArrayOfAcceptedCreditCardAcceptedCreditCards"/&gt;
 *         &lt;element name="carParkOperators" type="{http://bartws.micros.com/1.17}ArrayOfCarParkOperatorsCarParkOperators" minOccurs="0"/&gt;
 *         &lt;element name="businessTypes" type="{http://bartws.micros.com/1.17}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *         &lt;element name="employeeNumbers" type="{http://bartws.micros.com/1.17}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *         &lt;element name="ukHotelSpend" type="{http://bartws.micros.com/1.17}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *         &lt;element name="restrictedRates" type="{http://bartws.micros.com/1.17}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *         &lt;element name="corpUpsellItems" type="{http://bartws.micros.com/1.17}ArrayOfListItemListItem" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SharedDataResponse", propOrder = {
    "donations",
    "bookingWindows",
    "countries",
    "titles",
    "regions",
    "breakfasts",
    "upsellCategories",
    "upsellItems",
    "internetPrepaymentAllowed",
    "internetBookingAvailable",
    "brands",
    "hotelBrands",
    "lettingTypes",
    "facilities",
    "rateClassifications",
    "loyaltyPrograms",
    "creditCardsAccepted",
    "carParkOperators",
    "businessTypes",
    "employeeNumbers",
    "ukHotelSpend",
    "restrictedRates",
    "corpUpsellItems"
})
public class SharedDataResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfCharityDonations donations;
    @XmlElement(required = true)
    protected ArrayOfBookingWindowBookingWindows bookingWindows;
    @XmlElement(required = true)
    protected ArrayOfCountryCountryCodes countries;
    @XmlElement(required = true)
    protected ArrayOftitlesItemString titles;
    protected ArrayOfRegionRegion regions;
    protected ArrayOfbreakfastBreakfast breakfasts;
    protected ArrayOfUpsellCategoryCategoryList upsellCategories;
    protected ArrayOfUpsellItemUpsellItem upsellItems;
    protected boolean internetPrepaymentAllowed;
    protected boolean internetBookingAvailable;
    @XmlElement(required = true)
    protected ArrayOfBrandBrands brands;
    @XmlElement(required = true)
    protected ArrayOfHotelBrandHotelBrands hotelBrands;
    @XmlElement(required = true)
    protected ArrayOfLettingTypeLettingTypes lettingTypes;
    @XmlElement(required = true)
    protected ArrayOfFacilitySummaryFacilitySummaries facilities;
    @XmlElement(required = true)
    protected ArrayOfRateClassificationRateClassifications rateClassifications;
    protected ArrayOfLoyaltyProgramLoyaltyProgram loyaltyPrograms;
    @XmlElement(required = true)
    protected ArrayOfAcceptedCreditCardAcceptedCreditCards creditCardsAccepted;
    protected ArrayOfCarParkOperatorsCarParkOperators carParkOperators;
    protected ArrayOfListItemListItem businessTypes;
    protected ArrayOfListItemListItem employeeNumbers;
    protected ArrayOfListItemListItem ukHotelSpend;
    protected ArrayOfListItemListItem restrictedRates;
    protected ArrayOfListItemListItem corpUpsellItems;

    /**
     * Gets the value of the donations property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCharityDonations }
     *     
     */
    public ArrayOfCharityDonations getDonations() {
        return donations;
    }

    /**
     * Sets the value of the donations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCharityDonations }
     *     
     */
    public void setDonations(ArrayOfCharityDonations value) {
        this.donations = value;
    }

    /**
     * Gets the value of the bookingWindows property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBookingWindowBookingWindows }
     *     
     */
    public ArrayOfBookingWindowBookingWindows getBookingWindows() {
        return bookingWindows;
    }

    /**
     * Sets the value of the bookingWindows property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBookingWindowBookingWindows }
     *     
     */
    public void setBookingWindows(ArrayOfBookingWindowBookingWindows value) {
        this.bookingWindows = value;
    }

    /**
     * Gets the value of the countries property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCountryCountryCodes }
     *     
     */
    public ArrayOfCountryCountryCodes getCountries() {
        return countries;
    }

    /**
     * Sets the value of the countries property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCountryCountryCodes }
     *     
     */
    public void setCountries(ArrayOfCountryCountryCodes value) {
        this.countries = value;
    }

    /**
     * Gets the value of the titles property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOftitlesItemString }
     *     
     */
    public ArrayOftitlesItemString getTitles() {
        return titles;
    }

    /**
     * Sets the value of the titles property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOftitlesItemString }
     *     
     */
    public void setTitles(ArrayOftitlesItemString value) {
        this.titles = value;
    }

    /**
     * Gets the value of the regions property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRegionRegion }
     *     
     */
    public ArrayOfRegionRegion getRegions() {
        return regions;
    }

    /**
     * Sets the value of the regions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRegionRegion }
     *     
     */
    public void setRegions(ArrayOfRegionRegion value) {
        this.regions = value;
    }

    /**
     * Gets the value of the breakfasts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public ArrayOfbreakfastBreakfast getBreakfasts() {
        return breakfasts;
    }

    /**
     * Sets the value of the breakfasts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public void setBreakfasts(ArrayOfbreakfastBreakfast value) {
        this.breakfasts = value;
    }

    /**
     * Gets the value of the upsellCategories property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellCategoryCategoryList }
     *     
     */
    public ArrayOfUpsellCategoryCategoryList getUpsellCategories() {
        return upsellCategories;
    }

    /**
     * Sets the value of the upsellCategories property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellCategoryCategoryList }
     *     
     */
    public void setUpsellCategories(ArrayOfUpsellCategoryCategoryList value) {
        this.upsellCategories = value;
    }

    /**
     * Gets the value of the upsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellItemUpsellItem }
     *     
     */
    public ArrayOfUpsellItemUpsellItem getUpsellItems() {
        return upsellItems;
    }

    /**
     * Sets the value of the upsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellItemUpsellItem }
     *     
     */
    public void setUpsellItems(ArrayOfUpsellItemUpsellItem value) {
        this.upsellItems = value;
    }

    /**
     * Gets the value of the internetPrepaymentAllowed property.
     * 
     */
    public boolean isInternetPrepaymentAllowed() {
        return internetPrepaymentAllowed;
    }

    /**
     * Sets the value of the internetPrepaymentAllowed property.
     * 
     */
    public void setInternetPrepaymentAllowed(boolean value) {
        this.internetPrepaymentAllowed = value;
    }

    /**
     * Gets the value of the internetBookingAvailable property.
     * 
     */
    public boolean isInternetBookingAvailable() {
        return internetBookingAvailable;
    }

    /**
     * Sets the value of the internetBookingAvailable property.
     * 
     */
    public void setInternetBookingAvailable(boolean value) {
        this.internetBookingAvailable = value;
    }

    /**
     * Gets the value of the brands property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBrandBrands }
     *     
     */
    public ArrayOfBrandBrands getBrands() {
        return brands;
    }

    /**
     * Sets the value of the brands property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBrandBrands }
     *     
     */
    public void setBrands(ArrayOfBrandBrands value) {
        this.brands = value;
    }

    /**
     * Gets the value of the hotelBrands property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHotelBrandHotelBrands }
     *     
     */
    public ArrayOfHotelBrandHotelBrands getHotelBrands() {
        return hotelBrands;
    }

    /**
     * Sets the value of the hotelBrands property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHotelBrandHotelBrands }
     *     
     */
    public void setHotelBrands(ArrayOfHotelBrandHotelBrands value) {
        this.hotelBrands = value;
    }

    /**
     * Gets the value of the lettingTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLettingTypeLettingTypes }
     *     
     */
    public ArrayOfLettingTypeLettingTypes getLettingTypes() {
        return lettingTypes;
    }

    /**
     * Sets the value of the lettingTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLettingTypeLettingTypes }
     *     
     */
    public void setLettingTypes(ArrayOfLettingTypeLettingTypes value) {
        this.lettingTypes = value;
    }

    /**
     * Gets the value of the facilities property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFacilitySummaryFacilitySummaries }
     *     
     */
    public ArrayOfFacilitySummaryFacilitySummaries getFacilities() {
        return facilities;
    }

    /**
     * Sets the value of the facilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFacilitySummaryFacilitySummaries }
     *     
     */
    public void setFacilities(ArrayOfFacilitySummaryFacilitySummaries value) {
        this.facilities = value;
    }

    /**
     * Gets the value of the rateClassifications property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRateClassificationRateClassifications }
     *     
     */
    public ArrayOfRateClassificationRateClassifications getRateClassifications() {
        return rateClassifications;
    }

    /**
     * Sets the value of the rateClassifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRateClassificationRateClassifications }
     *     
     */
    public void setRateClassifications(ArrayOfRateClassificationRateClassifications value) {
        this.rateClassifications = value;
    }

    /**
     * Gets the value of the loyaltyPrograms property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLoyaltyProgramLoyaltyProgram }
     *     
     */
    public ArrayOfLoyaltyProgramLoyaltyProgram getLoyaltyPrograms() {
        return loyaltyPrograms;
    }

    /**
     * Sets the value of the loyaltyPrograms property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLoyaltyProgramLoyaltyProgram }
     *     
     */
    public void setLoyaltyPrograms(ArrayOfLoyaltyProgramLoyaltyProgram value) {
        this.loyaltyPrograms = value;
    }

    /**
     * Gets the value of the creditCardsAccepted property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAcceptedCreditCardAcceptedCreditCards }
     *     
     */
    public ArrayOfAcceptedCreditCardAcceptedCreditCards getCreditCardsAccepted() {
        return creditCardsAccepted;
    }

    /**
     * Sets the value of the creditCardsAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAcceptedCreditCardAcceptedCreditCards }
     *     
     */
    public void setCreditCardsAccepted(ArrayOfAcceptedCreditCardAcceptedCreditCards value) {
        this.creditCardsAccepted = value;
    }

    /**
     * Gets the value of the carParkOperators property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCarParkOperatorsCarParkOperators }
     *     
     */
    public ArrayOfCarParkOperatorsCarParkOperators getCarParkOperators() {
        return carParkOperators;
    }

    /**
     * Sets the value of the carParkOperators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCarParkOperatorsCarParkOperators }
     *     
     */
    public void setCarParkOperators(ArrayOfCarParkOperatorsCarParkOperators value) {
        this.carParkOperators = value;
    }

    /**
     * Gets the value of the businessTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getBusinessTypes() {
        return businessTypes;
    }

    /**
     * Sets the value of the businessTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setBusinessTypes(ArrayOfListItemListItem value) {
        this.businessTypes = value;
    }

    /**
     * Gets the value of the employeeNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getEmployeeNumbers() {
        return employeeNumbers;
    }

    /**
     * Sets the value of the employeeNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setEmployeeNumbers(ArrayOfListItemListItem value) {
        this.employeeNumbers = value;
    }

    /**
     * Gets the value of the ukHotelSpend property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getUkHotelSpend() {
        return ukHotelSpend;
    }

    /**
     * Sets the value of the ukHotelSpend property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setUkHotelSpend(ArrayOfListItemListItem value) {
        this.ukHotelSpend = value;
    }

    /**
     * Gets the value of the restrictedRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getRestrictedRates() {
        return restrictedRates;
    }

    /**
     * Sets the value of the restrictedRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setRestrictedRates(ArrayOfListItemListItem value) {
        this.restrictedRates = value;
    }

    /**
     * Gets the value of the corpUpsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getCorpUpsellItems() {
        return corpUpsellItems;
    }

    /**
     * Sets the value of the corpUpsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setCorpUpsellItems(ArrayOfListItemListItem value) {
        this.corpUpsellItems = value;
    }

}
