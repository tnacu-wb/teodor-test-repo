
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PaymentReceipt complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PaymentReceipt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="authCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cardHolder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cardType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="companyText1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="copyBill" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="copyBillText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="depositName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="depositNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="eftNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="expires" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentAddress1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentAddress2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentAddress3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentAddress4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentPostCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vatExcLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vatIncLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vatReg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vatTotalLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentReceipt", propOrder = {
    "amount",
    "authCode",
    "cardHolder",
    "cardNumber",
    "cardType",
    "comment",
    "companyText1",
    "copyBill",
    "copyBillText",
    "depositName",
    "depositNumber",
    "description",
    "eftNumber",
    "expires",
    "paymentAddress1",
    "paymentAddress2",
    "paymentAddress3",
    "paymentAddress4",
    "paymentMethod",
    "paymentPostCode",
    "tid",
    "transDate",
    "transTime",
    "transType",
    "vatExcLine",
    "vatIncLine",
    "vatReg",
    "vatTotalLine"
})
public class PaymentReceipt
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String amount;
    @XmlElement(nillable = true)
    protected String authCode;
    @XmlElement(nillable = true)
    protected String cardHolder;
    @XmlElement(nillable = true)
    protected String cardNumber;
    @XmlElement(nillable = true)
    protected String cardType;
    @XmlElement(nillable = true)
    protected String comment;
    @XmlElement(nillable = true)
    protected String companyText1;
    @XmlElement(nillable = true)
    protected String copyBill;
    @XmlElement(nillable = true)
    protected String copyBillText;
    @XmlElement(nillable = true)
    protected String depositName;
    @XmlElement(nillable = true)
    protected String depositNumber;
    @XmlElement(nillable = true)
    protected String description;
    @XmlElement(nillable = true)
    protected String eftNumber;
    @XmlElement(nillable = true)
    protected String expires;
    @XmlElement(nillable = true)
    protected String paymentAddress1;
    @XmlElement(nillable = true)
    protected String paymentAddress2;
    @XmlElement(nillable = true)
    protected String paymentAddress3;
    @XmlElement(nillable = true)
    protected String paymentAddress4;
    @XmlElement(nillable = true)
    protected String paymentMethod;
    @XmlElement(nillable = true)
    protected String paymentPostCode;
    @XmlElement(nillable = true)
    protected String tid;
    @XmlElement(nillable = true)
    protected String transDate;
    @XmlElement(nillable = true)
    protected String transTime;
    @XmlElement(nillable = true)
    protected String transType;
    @XmlElement(nillable = true)
    protected String vatExcLine;
    @XmlElement(nillable = true)
    protected String vatIncLine;
    @XmlElement(nillable = true)
    protected String vatReg;
    @XmlElement(nillable = true)
    protected String vatTotalLine;

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the authCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * Sets the value of the authCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthCode(String value) {
        this.authCode = value;
    }

    /**
     * Gets the value of the cardHolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardHolder() {
        return cardHolder;
    }

    /**
     * Sets the value of the cardHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardHolder(String value) {
        this.cardHolder = value;
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
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the companyText1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyText1() {
        return companyText1;
    }

    /**
     * Sets the value of the companyText1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyText1(String value) {
        this.companyText1 = value;
    }

    /**
     * Gets the value of the copyBill property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyBill() {
        return copyBill;
    }

    /**
     * Sets the value of the copyBill property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyBill(String value) {
        this.copyBill = value;
    }

    /**
     * Gets the value of the copyBillText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyBillText() {
        return copyBillText;
    }

    /**
     * Sets the value of the copyBillText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyBillText(String value) {
        this.copyBillText = value;
    }

    /**
     * Gets the value of the depositName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepositName() {
        return depositName;
    }

    /**
     * Sets the value of the depositName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepositName(String value) {
        this.depositName = value;
    }

    /**
     * Gets the value of the depositNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepositNumber() {
        return depositNumber;
    }

    /**
     * Sets the value of the depositNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepositNumber(String value) {
        this.depositNumber = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the eftNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEftNumber() {
        return eftNumber;
    }

    /**
     * Sets the value of the eftNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEftNumber(String value) {
        this.eftNumber = value;
    }

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpires() {
        return expires;
    }

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpires(String value) {
        this.expires = value;
    }

    /**
     * Gets the value of the paymentAddress1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAddress1() {
        return paymentAddress1;
    }

    /**
     * Sets the value of the paymentAddress1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAddress1(String value) {
        this.paymentAddress1 = value;
    }

    /**
     * Gets the value of the paymentAddress2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAddress2() {
        return paymentAddress2;
    }

    /**
     * Sets the value of the paymentAddress2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAddress2(String value) {
        this.paymentAddress2 = value;
    }

    /**
     * Gets the value of the paymentAddress3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAddress3() {
        return paymentAddress3;
    }

    /**
     * Sets the value of the paymentAddress3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAddress3(String value) {
        this.paymentAddress3 = value;
    }

    /**
     * Gets the value of the paymentAddress4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAddress4() {
        return paymentAddress4;
    }

    /**
     * Sets the value of the paymentAddress4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAddress4(String value) {
        this.paymentAddress4 = value;
    }

    /**
     * Gets the value of the paymentMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the value of the paymentMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    }

    /**
     * Gets the value of the paymentPostCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentPostCode() {
        return paymentPostCode;
    }

    /**
     * Sets the value of the paymentPostCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentPostCode(String value) {
        this.paymentPostCode = value;
    }

    /**
     * Gets the value of the tid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTid() {
        return tid;
    }

    /**
     * Sets the value of the tid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTid(String value) {
        this.tid = value;
    }

    /**
     * Gets the value of the transDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransDate() {
        return transDate;
    }

    /**
     * Sets the value of the transDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransDate(String value) {
        this.transDate = value;
    }

    /**
     * Gets the value of the transTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransTime() {
        return transTime;
    }

    /**
     * Sets the value of the transTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransTime(String value) {
        this.transTime = value;
    }

    /**
     * Gets the value of the transType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransType() {
        return transType;
    }

    /**
     * Sets the value of the transType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransType(String value) {
        this.transType = value;
    }

    /**
     * Gets the value of the vatExcLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatExcLine() {
        return vatExcLine;
    }

    /**
     * Sets the value of the vatExcLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatExcLine(String value) {
        this.vatExcLine = value;
    }

    /**
     * Gets the value of the vatIncLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatIncLine() {
        return vatIncLine;
    }

    /**
     * Sets the value of the vatIncLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatIncLine(String value) {
        this.vatIncLine = value;
    }

    /**
     * Gets the value of the vatReg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatReg() {
        return vatReg;
    }

    /**
     * Sets the value of the vatReg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatReg(String value) {
        this.vatReg = value;
    }

    /**
     * Gets the value of the vatTotalLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatTotalLine() {
        return vatTotalLine;
    }

    /**
     * Sets the value of the vatTotalLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatTotalLine(String value) {
        this.vatTotalLine = value;
    }

}
