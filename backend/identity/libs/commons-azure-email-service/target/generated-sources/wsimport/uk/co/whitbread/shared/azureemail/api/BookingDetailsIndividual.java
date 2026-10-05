
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BookingDetailsIndividual complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="BookingDetailsIndividual">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bookerDetails" type="{https://dto.email.transact.comms.int.wtbapi.com}BookerDetails" minOccurs="0"/>
 *         <element name="breakfastPostings" type="{https://dto.email.transact.comms.int.wtbapi.com}BreakfastPosting" minOccurs="0"/>
 *         <element name="checkInTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="checkOutTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="currencyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="guestDetails" type="{https://dto.email.transact.comms.int.wtbapi.com}GuestDetails" minOccurs="0"/>
 *         <element name="hotelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="languageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="login" type="{https://dto.email.transact.comms.int.wtbapi.com}Login" minOccurs="0"/>
 *         <element name="numberNights" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="paymentReceipt" type="{https://dto.email.transact.comms.int.wtbapi.com}PaymentReceipt" minOccurs="0"/>
 *         <element name="promotionalImages" type="{https://dto.email.transact.comms.int.wtbapi.com}PromotionalImages" minOccurs="0"/>
 *         <element name="resNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="restaurantBooking" type="{https://dto.email.transact.comms.int.wtbapi.com}RestaurantBooking" minOccurs="0"/>
 *         <element name="rooms" type="{https://dto.email.transact.comms.int.wtbapi.com}RoomsInd" minOccurs="0"/>
 *         <element name="sleepParkFly" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="supplementBooking" type="{https://dto.email.transact.comms.int.wtbapi.com}SupplementBooking" minOccurs="0"/>
 *         <element name="template" type="{https://dto.email.transact.comms.int.wtbapi.com}Template" minOccurs="0"/>
 *         <element name="totalReservation" type="{https://dto.email.transact.comms.int.wtbapi.com}TotalReservation" minOccurs="0"/>
 *         <element name="voucher" type="{https://dto.email.transact.comms.int.wtbapi.com}Voucher" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingDetailsIndividual", propOrder = {
    "arrivalDate",
    "bookerDetails",
    "breakfastPostings",
    "checkInTime",
    "checkOutTime",
    "currencyCode",
    "departureDate",
    "guestDetails",
    "hotelCode",
    "hotelName",
    "languageCode",
    "login",
    "numberNights",
    "paymentReceipt",
    "promotionalImages",
    "resNumber",
    "restaurantBooking",
    "rooms",
    "sleepParkFly",
    "supplementBooking",
    "template",
    "totalReservation",
    "voucher"
})
public class BookingDetailsIndividual
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String arrivalDate;
    @XmlElement(nillable = true)
    protected BookerDetails bookerDetails;
    @XmlElement(nillable = true)
    protected BreakfastPosting breakfastPostings;
    @XmlElement(nillable = true)
    protected String checkInTime;
    @XmlElement(nillable = true)
    protected String checkOutTime;
    @XmlElement(nillable = true)
    protected String currencyCode;
    @XmlElement(nillable = true)
    protected String departureDate;
    @XmlElement(nillable = true)
    protected GuestDetails guestDetails;
    @XmlElement(nillable = true)
    protected String hotelCode;
    @XmlElement(nillable = true)
    protected String hotelName;
    @XmlElement(nillable = true)
    protected String languageCode;
    @XmlElement(nillable = true)
    protected Login login;
    protected Integer numberNights;
    @XmlElement(nillable = true)
    protected PaymentReceipt paymentReceipt;
    @XmlElement(nillable = true)
    protected PromotionalImages promotionalImages;
    @XmlElement(nillable = true)
    protected String resNumber;
    @XmlElement(nillable = true)
    protected RestaurantBooking restaurantBooking;
    @XmlElement(nillable = true)
    protected RoomsInd rooms;
    @XmlElement(nillable = true)
    protected String sleepParkFly;
    @XmlElement(nillable = true)
    protected SupplementBooking supplementBooking;
    @XmlElement(nillable = true)
    protected Template template;
    @XmlElement(nillable = true)
    protected TotalReservation totalReservation;
    @XmlElement(nillable = true)
    protected Voucher voucher;

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
     * Gets the value of the breakfastPostings property.
     * 
     * @return
     *     possible object is
     *     {@link BreakfastPosting }
     *     
     */
    public BreakfastPosting getBreakfastPostings() {
        return breakfastPostings;
    }

    /**
     * Sets the value of the breakfastPostings property.
     * 
     * @param value
     *     allowed object is
     *     {@link BreakfastPosting }
     *     
     */
    public void setBreakfastPostings(BreakfastPosting value) {
        this.breakfastPostings = value;
    }

    /**
     * Gets the value of the checkInTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckInTime() {
        return checkInTime;
    }

    /**
     * Sets the value of the checkInTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckInTime(String value) {
        this.checkInTime = value;
    }

    /**
     * Gets the value of the checkOutTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckOutTime() {
        return checkOutTime;
    }

    /**
     * Sets the value of the checkOutTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckOutTime(String value) {
        this.checkOutTime = value;
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
     * Gets the value of the guestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link GuestDetails }
     *     
     */
    public GuestDetails getGuestDetails() {
        return guestDetails;
    }

    /**
     * Sets the value of the guestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuestDetails }
     *     
     */
    public void setGuestDetails(GuestDetails value) {
        this.guestDetails = value;
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
     * Gets the value of the login property.
     * 
     * @return
     *     possible object is
     *     {@link Login }
     *     
     */
    public Login getLogin() {
        return login;
    }

    /**
     * Sets the value of the login property.
     * 
     * @param value
     *     allowed object is
     *     {@link Login }
     *     
     */
    public void setLogin(Login value) {
        this.login = value;
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
     * Gets the value of the paymentReceipt property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentReceipt }
     *     
     */
    public PaymentReceipt getPaymentReceipt() {
        return paymentReceipt;
    }

    /**
     * Sets the value of the paymentReceipt property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentReceipt }
     *     
     */
    public void setPaymentReceipt(PaymentReceipt value) {
        this.paymentReceipt = value;
    }

    /**
     * Gets the value of the promotionalImages property.
     * 
     * @return
     *     possible object is
     *     {@link PromotionalImages }
     *     
     */
    public PromotionalImages getPromotionalImages() {
        return promotionalImages;
    }

    /**
     * Sets the value of the promotionalImages property.
     * 
     * @param value
     *     allowed object is
     *     {@link PromotionalImages }
     *     
     */
    public void setPromotionalImages(PromotionalImages value) {
        this.promotionalImages = value;
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

    /**
     * Gets the value of the restaurantBooking property.
     * 
     * @return
     *     possible object is
     *     {@link RestaurantBooking }
     *     
     */
    public RestaurantBooking getRestaurantBooking() {
        return restaurantBooking;
    }

    /**
     * Sets the value of the restaurantBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestaurantBooking }
     *     
     */
    public void setRestaurantBooking(RestaurantBooking value) {
        this.restaurantBooking = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link RoomsInd }
     *     
     */
    public RoomsInd getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomsInd }
     *     
     */
    public void setRooms(RoomsInd value) {
        this.rooms = value;
    }

    /**
     * Gets the value of the sleepParkFly property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSleepParkFly() {
        return sleepParkFly;
    }

    /**
     * Sets the value of the sleepParkFly property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSleepParkFly(String value) {
        this.sleepParkFly = value;
    }

    /**
     * Gets the value of the supplementBooking property.
     * 
     * @return
     *     possible object is
     *     {@link SupplementBooking }
     *     
     */
    public SupplementBooking getSupplementBooking() {
        return supplementBooking;
    }

    /**
     * Sets the value of the supplementBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupplementBooking }
     *     
     */
    public void setSupplementBooking(SupplementBooking value) {
        this.supplementBooking = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Template }
     *     
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Template }
     *     
     */
    public void setTemplate(Template value) {
        this.template = value;
    }

    /**
     * Gets the value of the totalReservation property.
     * 
     * @return
     *     possible object is
     *     {@link TotalReservation }
     *     
     */
    public TotalReservation getTotalReservation() {
        return totalReservation;
    }

    /**
     * Sets the value of the totalReservation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalReservation }
     *     
     */
    public void setTotalReservation(TotalReservation value) {
        this.totalReservation = value;
    }

    /**
     * Gets the value of the voucher property.
     * 
     * @return
     *     possible object is
     *     {@link Voucher }
     *     
     */
    public Voucher getVoucher() {
        return voucher;
    }

    /**
     * Sets the value of the voucher property.
     * 
     * @param value
     *     allowed object is
     *     {@link Voucher }
     *     
     */
    public void setVoucher(Voucher value) {
        this.voucher = value;
    }

}
