
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfPaymentCardPaymentCard complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfPaymentCardPaymentCard"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PaymentCard" type="{http://corporate.micros.com/1.0}PaymentCard" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPaymentCardPaymentCard", propOrder = {
    "paymentCard"
})
public class ArrayOfPaymentCardPaymentCard
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "PaymentCard", nillable = true)
    protected List<PaymentCard> paymentCard;

    /**
     * Gets the value of the paymentCard property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the paymentCard property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPaymentCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentCard }
     * </p>
     * 
     * 
     * @return
     *     The value of the paymentCard property.
     */
    public List<PaymentCard> getPaymentCard() {
        if (paymentCard == null) {
            paymentCard = new ArrayList<>();
        }
        return this.paymentCard;
    }

}
