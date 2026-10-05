
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for Hotels complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Hotels"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelBusinessDesc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelAddress" type="{http://bartws.micros.com/1.17}HotelAddress"/&gt;
 *         &lt;element name="hotelPhone" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelFax" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="restaurantName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="brand" type="{http://bartws.micros.com/1.17}Brand"/&gt;
 *         &lt;element name="hotelBrand" type="{http://bartws.micros.com/1.17}HotelBrands" minOccurs="0"/&gt;
 *         &lt;element name="primaryArea" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="secondaryArea" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="midWeekRates" type="{http://bartws.micros.com/1.17}ArrayOfClassificationRateClass" minOccurs="0"/&gt;
 *         &lt;element name="weekendRates" type="{http://bartws.micros.com/1.17}ArrayOfClassificationRateClass" minOccurs="0"/&gt;
 *         &lt;element name="directions" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="internetBookingsAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="creditCardsAccepted" type="{http://bartws.micros.com/1.17}ArrayOfAcceptedCreditCardAcceptedCreditCards"/&gt;
 *         &lt;element name="maxRoomsBookable" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="maxRoomsAmendable" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="checkinKiosk" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="checkinTime" type="{http://bartws.micros.com/1.17}Time"/&gt;
 *         &lt;element name="checkoutTime" type="{http://bartws.micros.com/1.17}Time"/&gt;
 *         &lt;element name="hotelDinner" type="{http://bartws.micros.com/1.17}HotelDinner"/&gt;
 *         &lt;element name="lastUpdatedDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="lastUpdatedTime" type="{http://bartws.micros.com/1.17}Time"/&gt;
 *         &lt;element name="hotelBreakfasts" type="{http://bartws.micros.com/1.17}ArrayOfHotelBreakfastHotelBreakfasts"/&gt;
 *         &lt;element name="hotelUpsellItems" type="{http://bartws.micros.com/1.17}ArrayOfUpsellItemUpsellItem" minOccurs="0"/&gt;
 *         &lt;element name="hotelFacilities" type="{http://bartws.micros.com/1.17}ArrayOfHotelFacilityHotelFacilities"/&gt;
 *         &lt;element name="restaurantOpeningTimes" type="{http://bartws.micros.com/1.17}ArrayOfRestaurantTimeRestaurantOpeningTimes" minOccurs="0"/&gt;
 *         &lt;element name="checkInEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="hotelsError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_HOTEL_CODE"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="hotelDataError" type="{http://bartws.micros.com/1.17}ErrorDetails" minOccurs="0"/&gt;
 *         &lt;element name="openingDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="bookingWindows" type="{http://bartws.micros.com/1.17}ArrayOfBookingWindowBookingWindows" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Hotels", propOrder = {
    "hotelCode",
    "hotelName",
    "hotelDescription",
    "hotelBusinessDesc",
    "hotelAddress",
    "hotelPhone",
    "hotelFax",
    "restaurantName",
    "brand",
    "hotelBrand",
    "primaryArea",
    "secondaryArea",
    "midWeekRates",
    "weekendRates",
    "directions",
    "internetBookingsAllowed",
    "creditCardsAccepted",
    "maxRoomsBookable",
    "maxRoomsAmendable",
    "checkinKiosk",
    "checkinTime",
    "checkoutTime",
    "hotelDinner",
    "lastUpdatedDate",
    "lastUpdatedTime",
    "hotelBreakfasts",
    "hotelUpsellItems",
    "hotelFacilities",
    "restaurantOpeningTimes",
    "checkInEnabled",
    "hotelsError",
    "hotelDataError",
    "openingDate",
    "bookingWindows"
})
public class Hotels
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String hotelCode;
    @XmlElement(required = true)
    protected String hotelName;
    @XmlElement(required = true)
    protected String hotelDescription;
    @XmlElement(required = true)
    protected String hotelBusinessDesc;
    @XmlElement(required = true)
    protected HotelAddress hotelAddress;
    @XmlElement(required = true)
    protected String hotelPhone;
    @XmlElement(required = true)
    protected String hotelFax;
    @XmlElement(required = true)
    protected String restaurantName;
    @XmlElement(required = true)
    protected Brand brand;
    protected HotelBrands hotelBrand;
    @XmlElement(required = true)
    protected String primaryArea;
    @XmlElement(required = true)
    protected String secondaryArea;
    protected ArrayOfClassificationRateClass midWeekRates;
    protected ArrayOfClassificationRateClass weekendRates;
    @XmlElement(required = true)
    protected String directions;
    protected boolean internetBookingsAllowed;
    @XmlElement(required = true)
    protected ArrayOfAcceptedCreditCardAcceptedCreditCards creditCardsAccepted;
    protected long maxRoomsBookable;
    protected long maxRoomsAmendable;
    protected boolean checkinKiosk;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime checkinTime;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime checkoutTime;
    @XmlElement(required = true)
    protected HotelDinner hotelDinner;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate lastUpdatedDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime lastUpdatedTime;
    @XmlElement(required = true)
    protected ArrayOfHotelBreakfastHotelBreakfasts hotelBreakfasts;
    protected ArrayOfUpsellItemUpsellItem hotelUpsellItems;
    @XmlElement(required = true)
    protected ArrayOfHotelFacilityHotelFacilities hotelFacilities;
    protected ArrayOfRestaurantTimeRestaurantOpeningTimes restaurantOpeningTimes;
    protected Boolean checkInEnabled;
    protected String hotelsError;
    protected ErrorDetails hotelDataError;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate openingDate;
    protected ArrayOfBookingWindowBookingWindows bookingWindows;

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
     * Gets the value of the hotelDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelDescription() {
        return hotelDescription;
    }

    /**
     * Sets the value of the hotelDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelDescription(String value) {
        this.hotelDescription = value;
    }

    /**
     * Gets the value of the hotelBusinessDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelBusinessDesc() {
        return hotelBusinessDesc;
    }

    /**
     * Sets the value of the hotelBusinessDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelBusinessDesc(String value) {
        this.hotelBusinessDesc = value;
    }

    /**
     * Gets the value of the hotelAddress property.
     * 
     * @return
     *     possible object is
     *     {@link HotelAddress }
     *     
     */
    public HotelAddress getHotelAddress() {
        return hotelAddress;
    }

    /**
     * Sets the value of the hotelAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelAddress }
     *     
     */
    public void setHotelAddress(HotelAddress value) {
        this.hotelAddress = value;
    }

    /**
     * Gets the value of the hotelPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelPhone() {
        return hotelPhone;
    }

    /**
     * Sets the value of the hotelPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelPhone(String value) {
        this.hotelPhone = value;
    }

    /**
     * Gets the value of the hotelFax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelFax() {
        return hotelFax;
    }

    /**
     * Sets the value of the hotelFax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelFax(String value) {
        this.hotelFax = value;
    }

    /**
     * Gets the value of the restaurantName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Sets the value of the restaurantName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestaurantName(String value) {
        this.restaurantName = value;
    }

    /**
     * Gets the value of the brand property.
     * 
     * @return
     *     possible object is
     *     {@link Brand }
     *     
     */
    public Brand getBrand() {
        return brand;
    }

    /**
     * Sets the value of the brand property.
     * 
     * @param value
     *     allowed object is
     *     {@link Brand }
     *     
     */
    public void setBrand(Brand value) {
        this.brand = value;
    }

    /**
     * Gets the value of the hotelBrand property.
     * 
     * @return
     *     possible object is
     *     {@link HotelBrands }
     *     
     */
    public HotelBrands getHotelBrand() {
        return hotelBrand;
    }

    /**
     * Sets the value of the hotelBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelBrands }
     *     
     */
    public void setHotelBrand(HotelBrands value) {
        this.hotelBrand = value;
    }

    /**
     * Gets the value of the primaryArea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryArea() {
        return primaryArea;
    }

    /**
     * Sets the value of the primaryArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryArea(String value) {
        this.primaryArea = value;
    }

    /**
     * Gets the value of the secondaryArea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryArea() {
        return secondaryArea;
    }

    /**
     * Sets the value of the secondaryArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryArea(String value) {
        this.secondaryArea = value;
    }

    /**
     * Gets the value of the midWeekRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfClassificationRateClass }
     *     
     */
    public ArrayOfClassificationRateClass getMidWeekRates() {
        return midWeekRates;
    }

    /**
     * Sets the value of the midWeekRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfClassificationRateClass }
     *     
     */
    public void setMidWeekRates(ArrayOfClassificationRateClass value) {
        this.midWeekRates = value;
    }

    /**
     * Gets the value of the weekendRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfClassificationRateClass }
     *     
     */
    public ArrayOfClassificationRateClass getWeekendRates() {
        return weekendRates;
    }

    /**
     * Sets the value of the weekendRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfClassificationRateClass }
     *     
     */
    public void setWeekendRates(ArrayOfClassificationRateClass value) {
        this.weekendRates = value;
    }

    /**
     * Gets the value of the directions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirections() {
        return directions;
    }

    /**
     * Sets the value of the directions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirections(String value) {
        this.directions = value;
    }

    /**
     * Gets the value of the internetBookingsAllowed property.
     * 
     */
    public boolean isInternetBookingsAllowed() {
        return internetBookingsAllowed;
    }

    /**
     * Sets the value of the internetBookingsAllowed property.
     * 
     */
    public void setInternetBookingsAllowed(boolean value) {
        this.internetBookingsAllowed = value;
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
     * Gets the value of the maxRoomsBookable property.
     * 
     */
    public long getMaxRoomsBookable() {
        return maxRoomsBookable;
    }

    /**
     * Sets the value of the maxRoomsBookable property.
     * 
     */
    public void setMaxRoomsBookable(long value) {
        this.maxRoomsBookable = value;
    }

    /**
     * Gets the value of the maxRoomsAmendable property.
     * 
     */
    public long getMaxRoomsAmendable() {
        return maxRoomsAmendable;
    }

    /**
     * Sets the value of the maxRoomsAmendable property.
     * 
     */
    public void setMaxRoomsAmendable(long value) {
        this.maxRoomsAmendable = value;
    }

    /**
     * Gets the value of the checkinKiosk property.
     * 
     */
    public boolean isCheckinKiosk() {
        return checkinKiosk;
    }

    /**
     * Sets the value of the checkinKiosk property.
     * 
     */
    public void setCheckinKiosk(boolean value) {
        this.checkinKiosk = value;
    }

    /**
     * Gets the value of the checkinTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    /**
     * Sets the value of the checkinTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckinTime(LocalTime value) {
        this.checkinTime = value;
    }

    /**
     * Gets the value of the checkoutTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getCheckoutTime() {
        return checkoutTime;
    }

    /**
     * Sets the value of the checkoutTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckoutTime(LocalTime value) {
        this.checkoutTime = value;
    }

    /**
     * Gets the value of the hotelDinner property.
     * 
     * @return
     *     possible object is
     *     {@link HotelDinner }
     *     
     */
    public HotelDinner getHotelDinner() {
        return hotelDinner;
    }

    /**
     * Sets the value of the hotelDinner property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelDinner }
     *     
     */
    public void setHotelDinner(HotelDinner value) {
        this.hotelDinner = value;
    }

    /**
     * Gets the value of the lastUpdatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * Sets the value of the lastUpdatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdatedDate(LocalDate value) {
        this.lastUpdatedDate = value;
    }

    /**
     * Gets the value of the lastUpdatedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    /**
     * Sets the value of the lastUpdatedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdatedTime(LocalTime value) {
        this.lastUpdatedTime = value;
    }

    /**
     * Gets the value of the hotelBreakfasts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHotelBreakfastHotelBreakfasts }
     *     
     */
    public ArrayOfHotelBreakfastHotelBreakfasts getHotelBreakfasts() {
        return hotelBreakfasts;
    }

    /**
     * Sets the value of the hotelBreakfasts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHotelBreakfastHotelBreakfasts }
     *     
     */
    public void setHotelBreakfasts(ArrayOfHotelBreakfastHotelBreakfasts value) {
        this.hotelBreakfasts = value;
    }

    /**
     * Gets the value of the hotelUpsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellItemUpsellItem }
     *     
     */
    public ArrayOfUpsellItemUpsellItem getHotelUpsellItems() {
        return hotelUpsellItems;
    }

    /**
     * Sets the value of the hotelUpsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellItemUpsellItem }
     *     
     */
    public void setHotelUpsellItems(ArrayOfUpsellItemUpsellItem value) {
        this.hotelUpsellItems = value;
    }

    /**
     * Gets the value of the hotelFacilities property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHotelFacilityHotelFacilities }
     *     
     */
    public ArrayOfHotelFacilityHotelFacilities getHotelFacilities() {
        return hotelFacilities;
    }

    /**
     * Sets the value of the hotelFacilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHotelFacilityHotelFacilities }
     *     
     */
    public void setHotelFacilities(ArrayOfHotelFacilityHotelFacilities value) {
        this.hotelFacilities = value;
    }

    /**
     * Gets the value of the restaurantOpeningTimes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRestaurantTimeRestaurantOpeningTimes }
     *     
     */
    public ArrayOfRestaurantTimeRestaurantOpeningTimes getRestaurantOpeningTimes() {
        return restaurantOpeningTimes;
    }

    /**
     * Sets the value of the restaurantOpeningTimes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRestaurantTimeRestaurantOpeningTimes }
     *     
     */
    public void setRestaurantOpeningTimes(ArrayOfRestaurantTimeRestaurantOpeningTimes value) {
        this.restaurantOpeningTimes = value;
    }

    /**
     * Gets the value of the checkInEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckInEnabled() {
        return checkInEnabled;
    }

    /**
     * Sets the value of the checkInEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckInEnabled(Boolean value) {
        this.checkInEnabled = value;
    }

    /**
     * Gets the value of the hotelsError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelsError() {
        return hotelsError;
    }

    /**
     * Sets the value of the hotelsError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelsError(String value) {
        this.hotelsError = value;
    }

    /**
     * Gets the value of the hotelDataError property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getHotelDataError() {
        return hotelDataError;
    }

    /**
     * Sets the value of the hotelDataError property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setHotelDataError(ErrorDetails value) {
        this.hotelDataError = value;
    }

    /**
     * Gets the value of the openingDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Sets the value of the openingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpeningDate(LocalDate value) {
        this.openingDate = value;
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

}
