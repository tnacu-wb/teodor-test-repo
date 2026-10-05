
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PaymentCard complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PaymentCard"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cardType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cardNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="issueNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cardholderName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cscCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PARes" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="99999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="prepaymentRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="businessAccountDetails" type="{http://bartws.micros.com/1.0}BusinessAccount" minOccurs="0"/&gt;
 *         &lt;element name="useExistingCard" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="billingAddress" type="{http://bartws.micros.com/1.0}Address" minOccurs="0"/&gt;
 *         &lt;element name="cbtCentralCardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cbtEmployeeCardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentCard", propOrder = {
    "cardType",
    "cardNumber",
    "expiryDate",
    "startDate",
    "issueNumber",
    "cardholderName",
    "cscCode",
    "paRes",
    "prepaymentRequired",
    "businessAccountDetails",
    "useExistingCard",
    "billingAddress",
    "cbtCentralCardId",
    "cbtEmployeeCardId"
})
public class PaymentCard
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String cardType;
    @XmlElement(required = true)
    protected String cardNumber;
    @XmlElement(required = true)
    protected String expiryDate;
    protected String startDate;
    protected String issueNumber;
    @XmlElement(required = true)
    protected String cardholderName;
    protected String cscCode;
    @XmlElement(name = "PARes")
    protected String paRes;
    protected boolean prepaymentRequired;
    protected BusinessAccount businessAccountDetails;
    protected Boolean useExistingCard;
    protected Address billingAddress;
    protected String cbtCentralCardId;
    protected String cbtEmployeeCardId;

    /**
     * Gets the value of the cardType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the value of the cardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardType(String value) {
        this.cardType = value;
    }

    /**
     * Gets the value of the cardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the value of the cardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardNumber(String value) {
        this.cardNumber = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDate(String value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the issueNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssueNumber() {
        return issueNumber;
    }

    /**
     * Sets the value of the issueNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssueNumber(String value) {
        this.issueNumber = value;
    }

    /**
     * Gets the value of the cardholderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardholderName() {
        return cardholderName;
    }

    /**
     * Sets the value of the cardholderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardholderName(String value) {
        this.cardholderName = value;
    }

    /**
     * Gets the value of the cscCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCscCode() {
        return cscCode;
    }

    /**
     * Sets the value of the cscCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCscCode(String value) {
        this.cscCode = value;
    }

    /**
     * Gets the value of the paRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPARes() {
        return paRes;
    }

    /**
     * Sets the value of the paRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPARes(String value) {
        this.paRes = value;
    }

    /**
     * Gets the value of the prepaymentRequired property.
     * 
     */
    public boolean isPrepaymentRequired() {
        return prepaymentRequired;
    }

    /**
     * Sets the value of the prepaymentRequired property.
     * 
     */
    public void setPrepaymentRequired(boolean value) {
        this.prepaymentRequired = value;
    }

    /**
     * Gets the value of the businessAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessAccount }
     *     
     */
    public BusinessAccount getBusinessAccountDetails() {
        return businessAccountDetails;
    }

    /**
     * Sets the value of the businessAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessAccount }
     *     
     */
    public void setBusinessAccountDetails(BusinessAccount value) {
        this.businessAccountDetails = value;
    }

    /**
     * Gets the value of the useExistingCard property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseExistingCard() {
        return useExistingCard;
    }

    /**
     * Sets the value of the useExistingCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseExistingCard(Boolean value) {
        this.useExistingCard = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the value of the billingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setBillingAddress(Address value) {
        this.billingAddress = value;
    }

    /**
     * Gets the value of the cbtCentralCardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtCentralCardId() {
        return cbtCentralCardId;
    }

    /**
     * Sets the value of the cbtCentralCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtCentralCardId(String value) {
        this.cbtCentralCardId = value;
    }

    /**
     * Gets the value of the cbtEmployeeCardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtEmployeeCardId() {
        return cbtEmployeeCardId;
    }

    /**
     * Sets the value of the cbtEmployeeCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtEmployeeCardId(String value) {
        this.cbtEmployeeCardId = value;
    }

}
