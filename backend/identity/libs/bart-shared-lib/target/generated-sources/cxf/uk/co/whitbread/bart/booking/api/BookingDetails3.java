
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for BookingDetails3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingDetails3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="arrivalTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="cellCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cellCodeLegend" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ratePlan" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rateClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="isPackage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="upsellBreakdown" type="{http://bartws.micros.com/1.31}UpsellBreakdown3" minOccurs="0"/&gt;
 *         &lt;element name="payment" type="{http://bartws.micros.com/1.31}PaymentCard" minOccurs="0"/&gt;
 *         &lt;element name="rooms" type="{http://bartws.micros.com/1.31}ArrayOfRoom"/&gt;
 *         &lt;element name="packageItems" type="{http://bartws.micros.com/1.31}ArrayOfpackageItemPackageItem" minOccurs="0"/&gt;
 *         &lt;element name="roomBreakdown" type="{http://bartws.micros.com/1.31}ArrayOfroomCostRoomBreakdown3" minOccurs="0"/&gt;
 *         &lt;element name="bookerDetails" type="{http://bartws.micros.com/1.31}BookerDetails"/&gt;
 *         &lt;element name="address" type="{http://bartws.micros.com/1.31}Address" minOccurs="0"/&gt;
 *         &lt;element name="totalCost" type="{http://bartws.micros.com/1.31}Price3"/&gt;
 *         &lt;element name="prepaid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="prepayTopup" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="prepaidAmount" type="{http://bartws.micros.com/1.31}Price" minOccurs="0"/&gt;
 *         &lt;element name="promotionText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dinnerUpsell" type="{http://bartws.micros.com/1.31}ArrayOfdinnerDinnerUpsell" minOccurs="0"/&gt;
 *         &lt;element name="bookingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cancelable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cancelableText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="amendable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="amendableText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="amendOnline" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="amendOnlineText" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="BOOKING_RULES_APPLY"/&gt;
 *               &lt;enumeration value="NON-ACTIVE_BOOKING"/&gt;
 *               &lt;enumeration value="NO_CREDIT_CARD"/&gt;
 *               &lt;enumeration value="NO_EMAIL_ADDRESS"/&gt;
 *               &lt;enumeration value="EXCEEDS_MAXIMUM_NIGHTS"/&gt;
 *               &lt;enumeration value="EXCEEDS_MAXIMUM_ROOMS"/&gt;
 *               &lt;enumeration value="NON-INTERNET_UPSELL"/&gt;
 *               &lt;enumeration value="NON-INTERNET_DEPOSIT"/&gt;
 *               &lt;enumeration value="NON-INTERNET_PROMOTION"/&gt;
 *               &lt;enumeration value="GROUP_BOOKING"/&gt;
 *               &lt;enumeration value="3RD_PARTY_CHANNEL"/&gt;
 *               &lt;enumeration value="OTHER"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="amendRestrictions" type="{http://bartws.micros.com/1.31}AmendRestrictions" minOccurs="0"/&gt;
 *         &lt;element name="checkInOnline" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="checkInText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkInDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="electronicInvoiceRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="isBusinessTrip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="smsConfirmationRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="emailConfirmationSent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="optionalCNP" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="changeCard" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cardFeeApplies" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carData" type="{http://bartws.micros.com/1.31}CarData" minOccurs="0"/&gt;
 *         &lt;element name="wifiRecommend" type="{http://bartws.micros.com/1.31}ArrayOfWifiRecommend" minOccurs="0"/&gt;
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
@XmlType(name = "BookingDetails3", propOrder = {
    "confirmationNumber",
    "hotelCode",
    "arrivalDate",
    "departureDate",
    "arrivalTime",
    "cellCode",
    "cellCodeLegend",
    "ratePlan",
    "rateClass",
    "isPackage",
    "upsellBreakdown",
    "payment",
    "rooms",
    "packageItems",
    "roomBreakdown",
    "bookerDetails",
    "address",
    "totalCost",
    "prepaid",
    "prepayTopup",
    "prepaidAmount",
    "promotionText",
    "dinnerUpsell",
    "bookingType",
    "cancelable",
    "cancelableText",
    "amendable",
    "amendableText",
    "amendOnline",
    "amendOnlineText",
    "amendRestrictions",
    "checkInOnline",
    "checkInText",
    "checkInDate",
    "electronicInvoiceRequired",
    "isBusinessTrip",
    "smsConfirmationRequired",
    "emailConfirmationSent",
    "optionalCNP",
    "changeCard",
    "cardFeeApplies",
    "carData",
    "wifiRecommend",
    "paymentProvider"
})
public class BookingDetails3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String confirmationNumber;
    @XmlElement(required = true)
    protected String hotelCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate arrivalDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate departureDate;
    protected Long arrivalTime;
    protected String cellCode;
    protected String cellCodeLegend;
    @XmlElement(required = true)
    protected String ratePlan;
    protected String rateClass;
    protected Boolean isPackage;
    protected UpsellBreakdown3 upsellBreakdown;
    protected PaymentCard payment;
    @XmlElement(required = true)
    protected ArrayOfRoom rooms;
    protected ArrayOfpackageItemPackageItem packageItems;
    protected ArrayOfroomCostRoomBreakdown3 roomBreakdown;
    @XmlElement(required = true)
    protected BookerDetails bookerDetails;
    protected Address address;
    @XmlElement(required = true)
    protected Price3 totalCost;
    protected Boolean prepaid;
    protected Boolean prepayTopup;
    protected Price prepaidAmount;
    protected String promotionText;
    protected ArrayOfdinnerDinnerUpsell dinnerUpsell;
    protected String bookingType;
    protected Boolean cancelable;
    protected String cancelableText;
    protected Boolean amendable;
    protected String amendableText;
    protected Boolean amendOnline;
    protected String amendOnlineText;
    protected AmendRestrictions amendRestrictions;
    protected Boolean checkInOnline;
    protected String checkInText;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate checkInDate;
    protected Boolean electronicInvoiceRequired;
    protected Boolean isBusinessTrip;
    protected Boolean smsConfirmationRequired;
    protected Boolean emailConfirmationSent;
    protected Boolean optionalCNP;
    protected Boolean changeCard;
    protected Boolean cardFeeApplies;
    protected CarData carData;
    protected ArrayOfWifiRecommend wifiRecommend;
    protected String paymentProvider;

    /**
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
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
     * Gets the value of the arrivalTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the value of the arrivalTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setArrivalTime(Long value) {
        this.arrivalTime = value;
    }

    /**
     * Gets the value of the cellCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellCode() {
        return cellCode;
    }

    /**
     * Sets the value of the cellCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellCode(String value) {
        this.cellCode = value;
    }

    /**
     * Gets the value of the cellCodeLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellCodeLegend() {
        return cellCodeLegend;
    }

    /**
     * Sets the value of the cellCodeLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellCodeLegend(String value) {
        this.cellCodeLegend = value;
    }

    /**
     * Gets the value of the ratePlan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRatePlan() {
        return ratePlan;
    }

    /**
     * Sets the value of the ratePlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRatePlan(String value) {
        this.ratePlan = value;
    }

    /**
     * Gets the value of the rateClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateClass() {
        return rateClass;
    }

    /**
     * Sets the value of the rateClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateClass(String value) {
        this.rateClass = value;
    }

    /**
     * Gets the value of the isPackage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsPackage() {
        return isPackage;
    }

    /**
     * Sets the value of the isPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPackage(Boolean value) {
        this.isPackage = value;
    }

    /**
     * Gets the value of the upsellBreakdown property.
     * 
     * @return
     *     possible object is
     *     {@link UpsellBreakdown3 }
     *     
     */
    public UpsellBreakdown3 getUpsellBreakdown() {
        return upsellBreakdown;
    }

    /**
     * Sets the value of the upsellBreakdown property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsellBreakdown3 }
     *     
     */
    public void setUpsellBreakdown(UpsellBreakdown3 value) {
        this.upsellBreakdown = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard }
     *     
     */
    public PaymentCard getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard }
     *     
     */
    public void setPayment(PaymentCard value) {
        this.payment = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRoom }
     *     
     */
    public ArrayOfRoom getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRoom }
     *     
     */
    public void setRooms(ArrayOfRoom value) {
        this.rooms = value;
    }

    /**
     * Gets the value of the packageItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfpackageItemPackageItem }
     *     
     */
    public ArrayOfpackageItemPackageItem getPackageItems() {
        return packageItems;
    }

    /**
     * Sets the value of the packageItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfpackageItemPackageItem }
     *     
     */
    public void setPackageItems(ArrayOfpackageItemPackageItem value) {
        this.packageItems = value;
    }

    /**
     * Gets the value of the roomBreakdown property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfroomCostRoomBreakdown3 }
     *     
     */
    public ArrayOfroomCostRoomBreakdown3 getRoomBreakdown() {
        return roomBreakdown;
    }

    /**
     * Sets the value of the roomBreakdown property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfroomCostRoomBreakdown3 }
     *     
     */
    public void setRoomBreakdown(ArrayOfroomCostRoomBreakdown3 value) {
        this.roomBreakdown = value;
    }

    /**
     * Gets the value of the bookerDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookerDetails }
     *     
     */
    public BookerDetails getBookerDetails() {
        return bookerDetails;
    }

    /**
     * Sets the value of the bookerDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookerDetails }
     *     
     */
    public void setBookerDetails(BookerDetails value) {
        this.bookerDetails = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the totalCost property.
     * 
     * @return
     *     possible object is
     *     {@link Price3 }
     *     
     */
    public Price3 getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the value of the totalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price3 }
     *     
     */
    public void setTotalCost(Price3 value) {
        this.totalCost = value;
    }

    /**
     * Gets the value of the prepaid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrepaid() {
        return prepaid;
    }

    /**
     * Sets the value of the prepaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrepaid(Boolean value) {
        this.prepaid = value;
    }

    /**
     * Gets the value of the prepayTopup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrepayTopup() {
        return prepayTopup;
    }

    /**
     * Sets the value of the prepayTopup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrepayTopup(Boolean value) {
        this.prepayTopup = value;
    }

    /**
     * Gets the value of the prepaidAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getPrepaidAmount() {
        return prepaidAmount;
    }

    /**
     * Sets the value of the prepaidAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setPrepaidAmount(Price value) {
        this.prepaidAmount = value;
    }

    /**
     * Gets the value of the promotionText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromotionText() {
        return promotionText;
    }

    /**
     * Sets the value of the promotionText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromotionText(String value) {
        this.promotionText = value;
    }

    /**
     * Gets the value of the dinnerUpsell property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public ArrayOfdinnerDinnerUpsell getDinnerUpsell() {
        return dinnerUpsell;
    }

    /**
     * Sets the value of the dinnerUpsell property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public void setDinnerUpsell(ArrayOfdinnerDinnerUpsell value) {
        this.dinnerUpsell = value;
    }

    /**
     * Gets the value of the bookingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingType() {
        return bookingType;
    }

    /**
     * Sets the value of the bookingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingType(String value) {
        this.bookingType = value;
    }

    /**
     * Gets the value of the cancelable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCancelable() {
        return cancelable;
    }

    /**
     * Sets the value of the cancelable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCancelable(Boolean value) {
        this.cancelable = value;
    }

    /**
     * Gets the value of the cancelableText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancelableText() {
        return cancelableText;
    }

    /**
     * Sets the value of the cancelableText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelableText(String value) {
        this.cancelableText = value;
    }

    /**
     * Gets the value of the amendable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAmendable() {
        return amendable;
    }

    /**
     * Sets the value of the amendable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAmendable(Boolean value) {
        this.amendable = value;
    }

    /**
     * Gets the value of the amendableText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmendableText() {
        return amendableText;
    }

    /**
     * Sets the value of the amendableText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmendableText(String value) {
        this.amendableText = value;
    }

    /**
     * Gets the value of the amendOnline property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAmendOnline() {
        return amendOnline;
    }

    /**
     * Sets the value of the amendOnline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAmendOnline(Boolean value) {
        this.amendOnline = value;
    }

    /**
     * Gets the value of the amendOnlineText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmendOnlineText() {
        return amendOnlineText;
    }

    /**
     * Sets the value of the amendOnlineText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmendOnlineText(String value) {
        this.amendOnlineText = value;
    }

    /**
     * Gets the value of the amendRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link AmendRestrictions }
     *     
     */
    public AmendRestrictions getAmendRestrictions() {
        return amendRestrictions;
    }

    /**
     * Sets the value of the amendRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmendRestrictions }
     *     
     */
    public void setAmendRestrictions(AmendRestrictions value) {
        this.amendRestrictions = value;
    }

    /**
     * Gets the value of the checkInOnline property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckInOnline() {
        return checkInOnline;
    }

    /**
     * Sets the value of the checkInOnline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckInOnline(Boolean value) {
        this.checkInOnline = value;
    }

    /**
     * Gets the value of the checkInText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckInText() {
        return checkInText;
    }

    /**
     * Sets the value of the checkInText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckInText(String value) {
        this.checkInText = value;
    }

    /**
     * Gets the value of the checkInDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    /**
     * Sets the value of the checkInDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckInDate(LocalDate value) {
        this.checkInDate = value;
    }

    /**
     * Gets the value of the electronicInvoiceRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isElectronicInvoiceRequired() {
        return electronicInvoiceRequired;
    }

    /**
     * Sets the value of the electronicInvoiceRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setElectronicInvoiceRequired(Boolean value) {
        this.electronicInvoiceRequired = value;
    }

    /**
     * Gets the value of the isBusinessTrip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsBusinessTrip() {
        return isBusinessTrip;
    }

    /**
     * Sets the value of the isBusinessTrip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsBusinessTrip(Boolean value) {
        this.isBusinessTrip = value;
    }

    /**
     * Gets the value of the smsConfirmationRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmsConfirmationRequired() {
        return smsConfirmationRequired;
    }

    /**
     * Sets the value of the smsConfirmationRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmsConfirmationRequired(Boolean value) {
        this.smsConfirmationRequired = value;
    }

    /**
     * Gets the value of the emailConfirmationSent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmailConfirmationSent() {
        return emailConfirmationSent;
    }

    /**
     * Sets the value of the emailConfirmationSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmailConfirmationSent(Boolean value) {
        this.emailConfirmationSent = value;
    }

    /**
     * Gets the value of the optionalCNP property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptionalCNP() {
        return optionalCNP;
    }

    /**
     * Sets the value of the optionalCNP property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptionalCNP(Boolean value) {
        this.optionalCNP = value;
    }

    /**
     * Gets the value of the changeCard property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isChangeCard() {
        return changeCard;
    }

    /**
     * Sets the value of the changeCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChangeCard(Boolean value) {
        this.changeCard = value;
    }

    /**
     * Gets the value of the cardFeeApplies property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCardFeeApplies() {
        return cardFeeApplies;
    }

    /**
     * Sets the value of the cardFeeApplies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCardFeeApplies(Boolean value) {
        this.cardFeeApplies = value;
    }

    /**
     * Gets the value of the carData property.
     * 
     * @return
     *     possible object is
     *     {@link CarData }
     *     
     */
    public CarData getCarData() {
        return carData;
    }

    /**
     * Sets the value of the carData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarData }
     *     
     */
    public void setCarData(CarData value) {
        this.carData = value;
    }

    /**
     * Gets the value of the wifiRecommend property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWifiRecommend }
     *     
     */
    public ArrayOfWifiRecommend getWifiRecommend() {
        return wifiRecommend;
    }

    /**
     * Sets the value of the wifiRecommend property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWifiRecommend }
     *     
     */
    public void setWifiRecommend(ArrayOfWifiRecommend value) {
        this.wifiRecommend = value;
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
