
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BusinessAccount complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BusinessAccount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="purchaseOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cardNotPresentAuth" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="atosUsername" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="atosPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="breakfastCodeRequired" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAllowance" type="{http://bartws.micros.com/1.31}Price" minOccurs="0"/&gt;
 *         &lt;element name="alcoholAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carParkingAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="wifiAccessAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="otherChargesAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessAccount", propOrder = {
    "purchaseOrder",
    "customerReference",
    "cardNotPresentAuth",
    "atosUsername",
    "atosPassword",
    "breakfastCodeRequired",
    "dinnerAllowance",
    "alcoholAllowed",
    "carParkingAllowed",
    "wifiAccessAllowed",
    "otherChargesAllowed"
})
public class BusinessAccount
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String purchaseOrder;
    protected String customerReference;
    protected Boolean cardNotPresentAuth;
    protected String atosUsername;
    protected String atosPassword;
    protected Long breakfastCodeRequired;
    protected Price dinnerAllowance;
    protected Boolean alcoholAllowed;
    protected Boolean carParkingAllowed;
    protected Boolean wifiAccessAllowed;
    protected Boolean otherChargesAllowed;

    /**
     * Gets the value of the purchaseOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the value of the purchaseOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseOrder(String value) {
        this.purchaseOrder = value;
    }

    /**
     * Gets the value of the customerReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerReference() {
        return customerReference;
    }

    /**
     * Sets the value of the customerReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerReference(String value) {
        this.customerReference = value;
    }

    /**
     * Gets the value of the cardNotPresentAuth property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCardNotPresentAuth() {
        return cardNotPresentAuth;
    }

    /**
     * Sets the value of the cardNotPresentAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCardNotPresentAuth(Boolean value) {
        this.cardNotPresentAuth = value;
    }

    /**
     * Gets the value of the atosUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtosUsername() {
        return atosUsername;
    }

    /**
     * Sets the value of the atosUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtosUsername(String value) {
        this.atosUsername = value;
    }

    /**
     * Gets the value of the atosPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtosPassword() {
        return atosPassword;
    }

    /**
     * Sets the value of the atosPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtosPassword(String value) {
        this.atosPassword = value;
    }

    /**
     * Gets the value of the breakfastCodeRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBreakfastCodeRequired() {
        return breakfastCodeRequired;
    }

    /**
     * Sets the value of the breakfastCodeRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBreakfastCodeRequired(Long value) {
        this.breakfastCodeRequired = value;
    }

    /**
     * Gets the value of the dinnerAllowance property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getDinnerAllowance() {
        return dinnerAllowance;
    }

    /**
     * Sets the value of the dinnerAllowance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setDinnerAllowance(Price value) {
        this.dinnerAllowance = value;
    }

    /**
     * Gets the value of the alcoholAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlcoholAllowed() {
        return alcoholAllowed;
    }

    /**
     * Sets the value of the alcoholAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlcoholAllowed(Boolean value) {
        this.alcoholAllowed = value;
    }

    /**
     * Gets the value of the carParkingAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCarParkingAllowed() {
        return carParkingAllowed;
    }

    /**
     * Sets the value of the carParkingAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCarParkingAllowed(Boolean value) {
        this.carParkingAllowed = value;
    }

    /**
     * Gets the value of the wifiAccessAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWifiAccessAllowed() {
        return wifiAccessAllowed;
    }

    /**
     * Sets the value of the wifiAccessAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWifiAccessAllowed(Boolean value) {
        this.wifiAccessAllowed = value;
    }

    /**
     * Gets the value of the otherChargesAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOtherChargesAllowed() {
        return otherChargesAllowed;
    }

    /**
     * Sets the value of the otherChargesAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOtherChargesAllowed(Boolean value) {
        this.otherChargesAllowed = value;
    }

}
