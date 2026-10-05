
package uk.co.whitbread.bart.marketing.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfSubscriptionElementSubscriptionElement complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfSubscriptionElementSubscriptionElement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SubscriptionElement" type="{http://bartws.micros.com/1.0}SubscriptionElement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSubscriptionElementSubscriptionElement", propOrder = {
    "subscriptionElement"
})
public class ArrayOfSubscriptionElementSubscriptionElement
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SubscriptionElement", nillable = true)
    protected List<SubscriptionElement> subscriptionElement;

    /**
     * Gets the value of the subscriptionElement property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the subscriptionElement property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSubscriptionElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubscriptionElement }
     * </p>
     * 
     * 
     * @return
     *     The value of the subscriptionElement property.
     */
    public List<SubscriptionElement> getSubscriptionElement() {
        if (subscriptionElement == null) {
            subscriptionElement = new ArrayList<>();
        }
        return this.subscriptionElement;
    }

}
