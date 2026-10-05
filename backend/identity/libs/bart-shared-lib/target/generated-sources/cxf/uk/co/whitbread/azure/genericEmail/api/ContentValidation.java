
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ContentValidation complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ContentValidation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ValidationAction" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ValidationAction" minOccurs="0"/&gt;
 *         &lt;element name="Email" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Email" minOccurs="0"/&gt;
 *         &lt;element name="Subscribers" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Subscriber" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Subscriber" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentValidation", propOrder = {
    "validationAction",
    "email",
    "subscribers"
})
public class ContentValidation
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ValidationAction")
    protected ValidationAction validationAction;
    @XmlElement(name = "Email")
    protected Email email;
    @XmlElement(name = "Subscribers")
    protected ContentValidation.Subscribers subscribers;

    /**
     * Gets the value of the validationAction property.
     * 
     * @return
     *     possible object is
     *     {@link ValidationAction }
     *     
     */
    public ValidationAction getValidationAction() {
        return validationAction;
    }

    /**
     * Sets the value of the validationAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidationAction }
     *     
     */
    public void setValidationAction(ValidationAction value) {
        this.validationAction = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link Email }
     *     
     */
    public Email getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link Email }
     *     
     */
    public void setEmail(Email value) {
        this.email = value;
    }

    /**
     * Gets the value of the subscribers property.
     * 
     * @return
     *     possible object is
     *     {@link ContentValidation.Subscribers }
     *     
     */
    public ContentValidation.Subscribers getSubscribers() {
        return subscribers;
    }

    /**
     * Sets the value of the subscribers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentValidation.Subscribers }
     *     
     */
    public void setSubscribers(ContentValidation.Subscribers value) {
        this.subscribers = value;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Subscriber" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Subscriber" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "subscriber"
    })
    public static class Subscribers
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Subscriber")
        protected List<Subscriber> subscriber;

        /**
         * Gets the value of the subscriber property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the subscriber property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getSubscriber().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Subscriber }
         * </p>
         * 
         * 
         * @return
         *     The value of the subscriber property.
         */
        public List<Subscriber> getSubscriber() {
            if (subscriber == null) {
                subscriber = new ArrayList<>();
            }
            return this.subscriber;
        }

    }

}
