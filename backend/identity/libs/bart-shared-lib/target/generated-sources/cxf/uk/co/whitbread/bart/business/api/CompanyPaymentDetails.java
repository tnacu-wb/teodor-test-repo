
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CompanyPaymentDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CompanyPaymentDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="locked" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="individualCards" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="paymentCards" type="{http://corporate.micros.com/1.0}ArrayOfPaymentCardPaymentCard"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanyPaymentDetails", propOrder = {
    "locked",
    "individualCards",
    "paymentCards"
})
public class CompanyPaymentDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean locked;
    protected boolean individualCards;
    @XmlElement(required = true)
    protected ArrayOfPaymentCardPaymentCard paymentCards;

    /**
     * Gets the value of the locked property.
     * 
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     */
    public void setLocked(boolean value) {
        this.locked = value;
    }

    /**
     * Gets the value of the individualCards property.
     * 
     */
    public boolean isIndividualCards() {
        return individualCards;
    }

    /**
     * Sets the value of the individualCards property.
     * 
     */
    public void setIndividualCards(boolean value) {
        this.individualCards = value;
    }

    /**
     * Gets the value of the paymentCards property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPaymentCardPaymentCard }
     *     
     */
    public ArrayOfPaymentCardPaymentCard getPaymentCards() {
        return paymentCards;
    }

    /**
     * Sets the value of the paymentCards property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPaymentCardPaymentCard }
     *     
     */
    public void setPaymentCards(ArrayOfPaymentCardPaymentCard value) {
        this.paymentCards = value;
    }

}
