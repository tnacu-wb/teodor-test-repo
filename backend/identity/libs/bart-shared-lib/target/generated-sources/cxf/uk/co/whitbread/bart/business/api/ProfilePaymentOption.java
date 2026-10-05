
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ProfilePaymentOption complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ProfilePaymentOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://corporate.micros.com/1.0}PaymentCard"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="prepay" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfilePaymentOption", propOrder = {
    "prepay"
})
public class ProfilePaymentOption
    extends PaymentCard
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean prepay;

    /**
     * Gets the value of the prepay property.
     * 
     */
    public boolean isPrepay() {
        return prepay;
    }

    /**
     * Sets the value of the prepay property.
     * 
     */
    public void setPrepay(boolean value) {
        this.prepay = value;
    }

}
