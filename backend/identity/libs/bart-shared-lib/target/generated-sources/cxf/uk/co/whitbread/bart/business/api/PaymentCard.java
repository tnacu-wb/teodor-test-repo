
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
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
 *         &lt;element name="cardID" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="cardLabel" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cardType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cardNumber" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="nameOnCard" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="issueNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;maxInclusive value="99"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="billingAddress" type="{http://corporate.micros.com/1.0}Address"/&gt;
 *         &lt;element name="cardNotPresentRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cardNotPresent" type="{http://corporate.micros.com/1.0}CardNotPresent" minOccurs="0"/&gt;
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
    "cardID",
    "cardLabel",
    "cardType",
    "cardNumber",
    "nameOnCard",
    "startDate",
    "endDate",
    "issueNumber",
    "billingAddress",
    "cardNotPresentRequired",
    "cardNotPresent"
})
@XmlSeeAlso({
    ProfilePaymentOption.class
})
public class PaymentCard
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected BigDecimal cardID;
    @XmlElement(required = true)
    protected String cardLabel;
    @XmlElement(required = true)
    protected String cardType;
    @XmlElement(required = true)
    protected BigDecimal cardNumber;
    @XmlElement(required = true)
    protected String nameOnCard;
    protected String startDate;
    @XmlElement(required = true)
    protected String endDate;
    protected BigDecimal issueNumber;
    @XmlElement(required = true)
    protected Address billingAddress;
    protected boolean cardNotPresentRequired;
    protected CardNotPresent cardNotPresent;

    /**
     * Gets the value of the cardID property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCardID() {
        return cardID;
    }

    /**
     * Sets the value of the cardID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCardID(BigDecimal value) {
        this.cardID = value;
    }

    /**
     * Gets the value of the cardLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardLabel() {
        return cardLabel;
    }

    /**
     * Sets the value of the cardLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardLabel(String value) {
        this.cardLabel = value;
    }

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
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the value of the cardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCardNumber(BigDecimal value) {
        this.cardNumber = value;
    }

    /**
     * Gets the value of the nameOnCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOnCard() {
        return nameOnCard;
    }

    /**
     * Sets the value of the nameOnCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOnCard(String value) {
        this.nameOnCard = value;
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
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDate() {
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
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the issueNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIssueNumber() {
        return issueNumber;
    }

    /**
     * Sets the value of the issueNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIssueNumber(BigDecimal value) {
        this.issueNumber = value;
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
     * Gets the value of the cardNotPresentRequired property.
     * 
     */
    public boolean isCardNotPresentRequired() {
        return cardNotPresentRequired;
    }

    /**
     * Sets the value of the cardNotPresentRequired property.
     * 
     */
    public void setCardNotPresentRequired(boolean value) {
        this.cardNotPresentRequired = value;
    }

    /**
     * Gets the value of the cardNotPresent property.
     * 
     * @return
     *     possible object is
     *     {@link CardNotPresent }
     *     
     */
    public CardNotPresent getCardNotPresent() {
        return cardNotPresent;
    }

    /**
     * Sets the value of the cardNotPresent property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardNotPresent }
     *     
     */
    public void setCardNotPresent(CardNotPresent value) {
        this.cardNotPresent = value;
    }

}
