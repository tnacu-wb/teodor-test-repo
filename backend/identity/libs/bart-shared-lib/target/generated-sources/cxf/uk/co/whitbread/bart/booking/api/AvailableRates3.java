
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AvailableRates3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AvailableRates3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rateCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rateDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rateDialogueText" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rateClassification" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cellCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bookingRules" type="{http://bartws.micros.com/1.31}ArrayOfbookingRuleBookingRule"/&gt;
 *         &lt;element name="roomDetails" type="{http://bartws.micros.com/1.31}ArrayOfroomAvailableRooms3"/&gt;
 *         &lt;element name="numberOfRooms"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;maxInclusive value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="packageIncluded" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="upsellItemsAvailable" type="{http://bartws.micros.com/1.31}ArrayOfupsellItemUpsellItem" minOccurs="0"/&gt;
 *         &lt;element name="packageDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="packageBreakfastIncluded" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="packageElements" type="{http://bartws.micros.com/1.31}ArrayOfpackageElementPackageElements" minOccurs="0"/&gt;
 *         &lt;element name="cardFeeApplies" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="prepaymentRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guaranteeRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="wifiRecommend" type="{http://bartws.micros.com/1.31}ArrayOfWifiRecommendWifiRecommend" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailableRates3", propOrder = {
    "rateCode",
    "rateDescription",
    "rateDialogueText",
    "rateClassification",
    "cellCode",
    "bookingRules",
    "roomDetails",
    "numberOfRooms",
    "packageIncluded",
    "upsellItemsAvailable",
    "packageDescription",
    "packageBreakfastIncluded",
    "packageElements",
    "cardFeeApplies",
    "prepaymentRequired",
    "guaranteeRequired",
    "wifiRecommend"
})
public class AvailableRates3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String rateCode;
    @XmlElement(required = true)
    protected String rateDescription;
    @XmlElement(required = true)
    protected String rateDialogueText;
    @XmlElement(required = true)
    protected String rateClassification;
    @XmlElement(required = true)
    protected String cellCode;
    @XmlElement(required = true)
    protected ArrayOfbookingRuleBookingRule bookingRules;
    @XmlElement(required = true)
    protected ArrayOfroomAvailableRooms3 roomDetails;
    protected long numberOfRooms;
    protected Boolean packageIncluded;
    protected ArrayOfupsellItemUpsellItem upsellItemsAvailable;
    protected String packageDescription;
    protected Boolean packageBreakfastIncluded;
    protected ArrayOfpackageElementPackageElements packageElements;
    protected Boolean cardFeeApplies;
    protected Boolean prepaymentRequired;
    protected Boolean guaranteeRequired;
    protected ArrayOfWifiRecommendWifiRecommend wifiRecommend;

    /**
     * Gets the value of the rateCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateCode() {
        return rateCode;
    }

    /**
     * Sets the value of the rateCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateCode(String value) {
        this.rateCode = value;
    }

    /**
     * Gets the value of the rateDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateDescription() {
        return rateDescription;
    }

    /**
     * Sets the value of the rateDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateDescription(String value) {
        this.rateDescription = value;
    }

    /**
     * Gets the value of the rateDialogueText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateDialogueText() {
        return rateDialogueText;
    }

    /**
     * Sets the value of the rateDialogueText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateDialogueText(String value) {
        this.rateDialogueText = value;
    }

    /**
     * Gets the value of the rateClassification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateClassification() {
        return rateClassification;
    }

    /**
     * Sets the value of the rateClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateClassification(String value) {
        this.rateClassification = value;
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
     * Gets the value of the bookingRules property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbookingRuleBookingRule }
     *     
     */
    public ArrayOfbookingRuleBookingRule getBookingRules() {
        return bookingRules;
    }

    /**
     * Sets the value of the bookingRules property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbookingRuleBookingRule }
     *     
     */
    public void setBookingRules(ArrayOfbookingRuleBookingRule value) {
        this.bookingRules = value;
    }

    /**
     * Gets the value of the roomDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfroomAvailableRooms3 }
     *     
     */
    public ArrayOfroomAvailableRooms3 getRoomDetails() {
        return roomDetails;
    }

    /**
     * Sets the value of the roomDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfroomAvailableRooms3 }
     *     
     */
    public void setRoomDetails(ArrayOfroomAvailableRooms3 value) {
        this.roomDetails = value;
    }

    /**
     * Gets the value of the numberOfRooms property.
     * 
     */
    public long getNumberOfRooms() {
        return numberOfRooms;
    }

    /**
     * Sets the value of the numberOfRooms property.
     * 
     */
    public void setNumberOfRooms(long value) {
        this.numberOfRooms = value;
    }

    /**
     * Gets the value of the packageIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPackageIncluded() {
        return packageIncluded;
    }

    /**
     * Sets the value of the packageIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPackageIncluded(Boolean value) {
        this.packageIncluded = value;
    }

    /**
     * Gets the value of the upsellItemsAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public ArrayOfupsellItemUpsellItem getUpsellItemsAvailable() {
        return upsellItemsAvailable;
    }

    /**
     * Sets the value of the upsellItemsAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public void setUpsellItemsAvailable(ArrayOfupsellItemUpsellItem value) {
        this.upsellItemsAvailable = value;
    }

    /**
     * Gets the value of the packageDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackageDescription() {
        return packageDescription;
    }

    /**
     * Sets the value of the packageDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackageDescription(String value) {
        this.packageDescription = value;
    }

    /**
     * Gets the value of the packageBreakfastIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPackageBreakfastIncluded() {
        return packageBreakfastIncluded;
    }

    /**
     * Sets the value of the packageBreakfastIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPackageBreakfastIncluded(Boolean value) {
        this.packageBreakfastIncluded = value;
    }

    /**
     * Gets the value of the packageElements property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfpackageElementPackageElements }
     *     
     */
    public ArrayOfpackageElementPackageElements getPackageElements() {
        return packageElements;
    }

    /**
     * Sets the value of the packageElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfpackageElementPackageElements }
     *     
     */
    public void setPackageElements(ArrayOfpackageElementPackageElements value) {
        this.packageElements = value;
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
     * Gets the value of the prepaymentRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrepaymentRequired() {
        return prepaymentRequired;
    }

    /**
     * Sets the value of the prepaymentRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrepaymentRequired(Boolean value) {
        this.prepaymentRequired = value;
    }

    /**
     * Gets the value of the guaranteeRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGuaranteeRequired() {
        return guaranteeRequired;
    }

    /**
     * Sets the value of the guaranteeRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGuaranteeRequired(Boolean value) {
        this.guaranteeRequired = value;
    }

    /**
     * Gets the value of the wifiRecommend property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWifiRecommendWifiRecommend }
     *     
     */
    public ArrayOfWifiRecommendWifiRecommend getWifiRecommend() {
        return wifiRecommend;
    }

    /**
     * Sets the value of the wifiRecommend property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWifiRecommendWifiRecommend }
     *     
     */
    public void setWifiRecommend(ArrayOfWifiRecommendWifiRecommend value) {
        this.wifiRecommend = value;
    }

}
