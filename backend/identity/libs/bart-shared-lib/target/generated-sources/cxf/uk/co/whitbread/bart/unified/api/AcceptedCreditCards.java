
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AcceptedCreditCards complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AcceptedCreditCards"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="creditCardCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="creditCardName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cardFee" type="{http://bartws.micros.com/1.17}Price" minOccurs="0"/&gt;
 *         &lt;element name="paymentOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="listOrder" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptedCreditCards", propOrder = {
    "creditCardCode",
    "creditCardName",
    "cardFee",
    "paymentOnly",
    "listOrder"
})
public class AcceptedCreditCards
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String creditCardCode;
    @XmlElement(required = true)
    protected String creditCardName;
    protected Price cardFee;
    protected boolean paymentOnly;
    protected long listOrder;

    /**
     * Gets the value of the creditCardCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardCode() {
        return creditCardCode;
    }

    /**
     * Sets the value of the creditCardCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardCode(String value) {
        this.creditCardCode = value;
    }

    /**
     * Gets the value of the creditCardName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardName() {
        return creditCardName;
    }

    /**
     * Sets the value of the creditCardName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardName(String value) {
        this.creditCardName = value;
    }

    /**
     * Gets the value of the cardFee property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getCardFee() {
        return cardFee;
    }

    /**
     * Sets the value of the cardFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setCardFee(Price value) {
        this.cardFee = value;
    }

    /**
     * Gets the value of the paymentOnly property.
     * 
     */
    public boolean isPaymentOnly() {
        return paymentOnly;
    }

    /**
     * Sets the value of the paymentOnly property.
     * 
     */
    public void setPaymentOnly(boolean value) {
        this.paymentOnly = value;
    }

    /**
     * Gets the value of the listOrder property.
     * 
     */
    public long getListOrder() {
        return listOrder;
    }

    /**
     * Sets the value of the listOrder property.
     * 
     */
    public void setListOrder(long value) {
        this.listOrder = value;
    }

}
