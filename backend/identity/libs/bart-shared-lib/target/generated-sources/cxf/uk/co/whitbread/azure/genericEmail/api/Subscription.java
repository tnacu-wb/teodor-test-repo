
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for Subscription complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Subscription"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SubscriptionID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="EmailsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="AccountsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="AdvAccountsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="LPAccountsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="DOTOAccountsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="BUAccountsPurchased" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="BeginDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Period" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="NotificationTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NotificationMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NotificationFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NotificationExpDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ForAccounting" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HasPurchasedEmails" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="ContractNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContractModifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="NumberofEmails" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Subscription", propOrder = {
    "subscriptionID",
    "emailsPurchased",
    "accountsPurchased",
    "advAccountsPurchased",
    "lpAccountsPurchased",
    "dotoAccountsPurchased",
    "buAccountsPurchased",
    "beginDate",
    "endDate",
    "notes",
    "period",
    "notificationTitle",
    "notificationMessage",
    "notificationFlag",
    "notificationExpDate",
    "forAccounting",
    "hasPurchasedEmails",
    "contractNumber",
    "contractModifier",
    "isRenewal",
    "numberofEmails"
})
public class Subscription
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SubscriptionID", nillable = true)
    protected Integer subscriptionID;
    @XmlElement(name = "EmailsPurchased")
    protected int emailsPurchased;
    @XmlElement(name = "AccountsPurchased")
    protected int accountsPurchased;
    @XmlElement(name = "AdvAccountsPurchased")
    protected int advAccountsPurchased;
    @XmlElement(name = "LPAccountsPurchased")
    protected int lpAccountsPurchased;
    @XmlElement(name = "DOTOAccountsPurchased")
    protected int dotoAccountsPurchased;
    @XmlElement(name = "BUAccountsPurchased")
    protected int buAccountsPurchased;
    @XmlElement(name = "BeginDate", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime beginDate;
    @XmlElement(name = "EndDate", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime endDate;
    @XmlElement(name = "Notes", nillable = true)
    protected String notes;
    @XmlElement(name = "Period", required = true)
    protected String period;
    @XmlElement(name = "NotificationTitle", nillable = true)
    protected String notificationTitle;
    @XmlElement(name = "NotificationMessage", nillable = true)
    protected String notificationMessage;
    @XmlElement(name = "NotificationFlag", nillable = true)
    protected String notificationFlag;
    @XmlElement(name = "NotificationExpDate", type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime notificationExpDate;
    @XmlElement(name = "ForAccounting", nillable = true)
    protected String forAccounting;
    @XmlElement(name = "HasPurchasedEmails")
    protected boolean hasPurchasedEmails;
    @XmlElement(name = "ContractNumber")
    protected String contractNumber;
    @XmlElement(name = "ContractModifier")
    protected String contractModifier;
    @XmlElement(name = "IsRenewal")
    protected Boolean isRenewal;
    @XmlElement(name = "NumberofEmails")
    protected long numberofEmails;

    /**
     * Gets the value of the subscriptionID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * Sets the value of the subscriptionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSubscriptionID(Integer value) {
        this.subscriptionID = value;
    }

    /**
     * Gets the value of the emailsPurchased property.
     * 
     */
    public int getEmailsPurchased() {
        return emailsPurchased;
    }

    /**
     * Sets the value of the emailsPurchased property.
     * 
     */
    public void setEmailsPurchased(int value) {
        this.emailsPurchased = value;
    }

    /**
     * Gets the value of the accountsPurchased property.
     * 
     */
    public int getAccountsPurchased() {
        return accountsPurchased;
    }

    /**
     * Sets the value of the accountsPurchased property.
     * 
     */
    public void setAccountsPurchased(int value) {
        this.accountsPurchased = value;
    }

    /**
     * Gets the value of the advAccountsPurchased property.
     * 
     */
    public int getAdvAccountsPurchased() {
        return advAccountsPurchased;
    }

    /**
     * Sets the value of the advAccountsPurchased property.
     * 
     */
    public void setAdvAccountsPurchased(int value) {
        this.advAccountsPurchased = value;
    }

    /**
     * Gets the value of the lpAccountsPurchased property.
     * 
     */
    public int getLPAccountsPurchased() {
        return lpAccountsPurchased;
    }

    /**
     * Sets the value of the lpAccountsPurchased property.
     * 
     */
    public void setLPAccountsPurchased(int value) {
        this.lpAccountsPurchased = value;
    }

    /**
     * Gets the value of the dotoAccountsPurchased property.
     * 
     */
    public int getDOTOAccountsPurchased() {
        return dotoAccountsPurchased;
    }

    /**
     * Sets the value of the dotoAccountsPurchased property.
     * 
     */
    public void setDOTOAccountsPurchased(int value) {
        this.dotoAccountsPurchased = value;
    }

    /**
     * Gets the value of the buAccountsPurchased property.
     * 
     */
    public int getBUAccountsPurchased() {
        return buAccountsPurchased;
    }

    /**
     * Sets the value of the buAccountsPurchased property.
     * 
     */
    public void setBUAccountsPurchased(int value) {
        this.buAccountsPurchased = value;
    }

    /**
     * Gets the value of the beginDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    /**
     * Sets the value of the beginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeginDate(LocalDateTime value) {
        this.beginDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(LocalDateTime value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriod(String value) {
        this.period = value;
    }

    /**
     * Gets the value of the notificationTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationTitle() {
        return notificationTitle;
    }

    /**
     * Sets the value of the notificationTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationTitle(String value) {
        this.notificationTitle = value;
    }

    /**
     * Gets the value of the notificationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * Sets the value of the notificationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationMessage(String value) {
        this.notificationMessage = value;
    }

    /**
     * Gets the value of the notificationFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationFlag() {
        return notificationFlag;
    }

    /**
     * Sets the value of the notificationFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationFlag(String value) {
        this.notificationFlag = value;
    }

    /**
     * Gets the value of the notificationExpDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getNotificationExpDate() {
        return notificationExpDate;
    }

    /**
     * Sets the value of the notificationExpDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationExpDate(LocalDateTime value) {
        this.notificationExpDate = value;
    }

    /**
     * Gets the value of the forAccounting property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForAccounting() {
        return forAccounting;
    }

    /**
     * Sets the value of the forAccounting property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForAccounting(String value) {
        this.forAccounting = value;
    }

    /**
     * Gets the value of the hasPurchasedEmails property.
     * 
     */
    public boolean isHasPurchasedEmails() {
        return hasPurchasedEmails;
    }

    /**
     * Sets the value of the hasPurchasedEmails property.
     * 
     */
    public void setHasPurchasedEmails(boolean value) {
        this.hasPurchasedEmails = value;
    }

    /**
     * Gets the value of the contractNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractNumber() {
        return contractNumber;
    }

    /**
     * Sets the value of the contractNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractNumber(String value) {
        this.contractNumber = value;
    }

    /**
     * Gets the value of the contractModifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractModifier() {
        return contractModifier;
    }

    /**
     * Sets the value of the contractModifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractModifier(String value) {
        this.contractModifier = value;
    }

    /**
     * Gets the value of the isRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRenewal() {
        return isRenewal;
    }

    /**
     * Sets the value of the isRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRenewal(Boolean value) {
        this.isRenewal = value;
    }

    /**
     * Gets the value of the numberofEmails property.
     * 
     */
    public long getNumberofEmails() {
        return numberofEmails;
    }

    /**
     * Sets the value of the numberofEmails property.
     * 
     */
    public void setNumberofEmails(long value) {
        this.numberofEmails = value;
    }

}
