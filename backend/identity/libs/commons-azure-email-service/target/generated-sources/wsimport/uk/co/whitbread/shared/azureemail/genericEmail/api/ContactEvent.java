
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactEvent complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="ContactEvent">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject">
 *       <sequence>
 *         <element name="ContactID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="ContactKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="EventDefinitionKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Data" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="AttributeSet" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AttributeSet" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactEvent", propOrder = {
    "contactID",
    "contactKey",
    "eventDefinitionKey",
    "data"
})
public class ContactEvent
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ContactID")
    protected Long contactID;
    @XmlElement(name = "ContactKey")
    protected String contactKey;
    @XmlElement(name = "EventDefinitionKey")
    protected String eventDefinitionKey;
    @XmlElement(name = "Data")
    protected ContactEvent.Data data;

    /**
     * Gets the value of the contactID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getContactID() {
        return contactID;
    }

    /**
     * Sets the value of the contactID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContactID(Long value) {
        this.contactID = value;
    }

    /**
     * Gets the value of the contactKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactKey() {
        return contactKey;
    }

    /**
     * Sets the value of the contactKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactKey(String value) {
        this.contactKey = value;
    }

    /**
     * Gets the value of the eventDefinitionKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDefinitionKey() {
        return eventDefinitionKey;
    }

    /**
     * Sets the value of the eventDefinitionKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDefinitionKey(String value) {
        this.eventDefinitionKey = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link ContactEvent.Data }
     *     
     */
    public ContactEvent.Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactEvent.Data }
     *     
     */
    public void setData(ContactEvent.Data value) {
        this.data = value;
    }


    /**
     * <p>Java class for anonymous complex type</p>.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="AttributeSet" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AttributeSet" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "attributeSet"
    })
    public static class Data
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "AttributeSet")
        protected List<AttributeSet> attributeSet;

        /**
         * Gets the value of the attributeSet property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the attributeSet property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getAttributeSet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AttributeSet }
         * </p>
         * 
         * 
         * @return
         *     The value of the attributeSet property.
         */
        public List<AttributeSet> getAttributeSet() {
            if (attributeSet == null) {
                attributeSet = new ArrayList<>();
            }
            return this.attributeSet;
        }

    }

}
