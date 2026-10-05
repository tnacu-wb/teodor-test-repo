
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TotalReservation complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TotalReservation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cardNotPresentAuth" type="{https://dto.email.transact.comms.int.wtbapi.com}CardNotPresentAuth" minOccurs="0"/&gt;
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guaranteeMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guaranteed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paraId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paraText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="paymentMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="purchaseOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refundAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refundRequired" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refundSuccessful" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="totalValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TotalReservation", propOrder = {
    "cardNotPresentAuth",
    "customerReference",
    "guaranteeMethod",
    "guaranteed",
    "paraId",
    "paraText",
    "paymentMethod",
    "purchaseOrder",
    "refundAmount",
    "refundRequired",
    "refundSuccessful",
    "totalValue"
})
public class TotalReservation
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected CardNotPresentAuth cardNotPresentAuth;
    @XmlElement(nillable = true)
    protected String customerReference;
    @XmlElement(nillable = true)
    protected String guaranteeMethod;
    @XmlElement(nillable = true)
    protected String guaranteed;
    @XmlElement(nillable = true)
    protected String paraId;
    @XmlElement(nillable = true)
    protected String paraText;
    @XmlElement(nillable = true)
    protected String paymentMethod;
    @XmlElement(nillable = true)
    protected String purchaseOrder;
    @XmlElement(nillable = true)
    protected String refundAmount;
    @XmlElement(nillable = true)
    protected String refundRequired;
    @XmlElement(nillable = true)
    protected String refundSuccessful;
    @XmlElement(nillable = true)
    protected String totalValue;

    /**
     * Gets the value of the cardNotPresentAuth property.
     * 
     * @return
     *     possible object is
     *     {@link CardNotPresentAuth }
     *     
     */
    public CardNotPresentAuth getCardNotPresentAuth() {
        return cardNotPresentAuth;
    }

    /**
     * Sets the value of the cardNotPresentAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardNotPresentAuth }
     *     
     */
    public void setCardNotPresentAuth(CardNotPresentAuth value) {
        this.cardNotPresentAuth = value;
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
     * Gets the value of the guaranteeMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuaranteeMethod() {
        return guaranteeMethod;
    }

    /**
     * Sets the value of the guaranteeMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuaranteeMethod(String value) {
        this.guaranteeMethod = value;
    }

    /**
     * Gets the value of the guaranteed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuaranteed() {
        return guaranteed;
    }

    /**
     * Sets the value of the guaranteed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuaranteed(String value) {
        this.guaranteed = value;
    }

    /**
     * Gets the value of the paraId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParaId() {
        return paraId;
    }

    /**
     * Sets the value of the paraId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParaId(String value) {
        this.paraId = value;
    }

    /**
     * Gets the value of the paraText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParaText() {
        return paraText;
    }

    /**
     * Sets the value of the paraText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParaText(String value) {
        this.paraText = value;
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
     * Gets the value of the refundAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundAmount() {
        return refundAmount;
    }

    /**
     * Sets the value of the refundAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundAmount(String value) {
        this.refundAmount = value;
    }

    /**
     * Gets the value of the refundRequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundRequired() {
        return refundRequired;
    }

    /**
     * Sets the value of the refundRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundRequired(String value) {
        this.refundRequired = value;
    }

    /**
     * Gets the value of the refundSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundSuccessful() {
        return refundSuccessful;
    }

    /**
     * Sets the value of the refundSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundSuccessful(String value) {
        this.refundSuccessful = value;
    }

    /**
     * Gets the value of the totalValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalValue() {
        return totalValue;
    }

    /**
     * Sets the value of the totalValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalValue(String value) {
        this.totalValue = value;
    }

}
