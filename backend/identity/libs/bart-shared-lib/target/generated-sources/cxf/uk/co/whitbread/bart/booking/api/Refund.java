
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Refund complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Refund"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="refundAttempted" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="refundSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="refundAmount" type="{http://bartws.micros.com/1.31}Price" minOccurs="0"/&gt;
 *         &lt;element name="refundText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Refund", propOrder = {
    "refundAttempted",
    "refundSuccessful",
    "refundAmount",
    "refundText"
})
public class Refund
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean refundAttempted;
    protected Boolean refundSuccessful;
    protected Price refundAmount;
    protected String refundText;

    /**
     * Gets the value of the refundAttempted property.
     * 
     */
    public boolean isRefundAttempted() {
        return refundAttempted;
    }

    /**
     * Sets the value of the refundAttempted property.
     * 
     */
    public void setRefundAttempted(boolean value) {
        this.refundAttempted = value;
    }

    /**
     * Gets the value of the refundSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRefundSuccessful() {
        return refundSuccessful;
    }

    /**
     * Sets the value of the refundSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRefundSuccessful(Boolean value) {
        this.refundSuccessful = value;
    }

    /**
     * Gets the value of the refundAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getRefundAmount() {
        return refundAmount;
    }

    /**
     * Sets the value of the refundAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setRefundAmount(Price value) {
        this.refundAmount = value;
    }

    /**
     * Gets the value of the refundText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundText() {
        return refundText;
    }

    /**
     * Sets the value of the refundText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundText(String value) {
        this.refundText = value;
    }

}
