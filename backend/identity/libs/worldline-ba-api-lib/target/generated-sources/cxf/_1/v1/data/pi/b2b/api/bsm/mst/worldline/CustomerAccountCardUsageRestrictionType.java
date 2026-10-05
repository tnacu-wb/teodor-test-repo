
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardUsageRestrictionType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardUsageRestrictionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RestrictCardUsage" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="RestrictionStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="RestrictionEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardUsageRestrictionType", propOrder = {
    "restrictCardUsage",
    "restrictionStart",
    "restrictionEnd",
    "customAttributes"
})
public class CustomerAccountCardUsageRestrictionType {

    @XmlElement(name = "RestrictCardUsage")
    protected boolean restrictCardUsage;
    @XmlElementRef(name = "RestrictionStart", namespace = "worldline.mst.bsm.api.b2b.pi.data.v1.1", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> restrictionStart;
    @XmlElementRef(name = "RestrictionEnd", namespace = "worldline.mst.bsm.api.b2b.pi.data.v1.1", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> restrictionEnd;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the restrictCardUsage property.
     * 
     */
    public boolean isRestrictCardUsage() {
        return restrictCardUsage;
    }

    /**
     * Sets the value of the restrictCardUsage property.
     * 
     */
    public void setRestrictCardUsage(boolean value) {
        this.restrictCardUsage = value;
    }

    /**
     * Gets the value of the restrictionStart property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getRestrictionStart() {
        return restrictionStart;
    }

    /**
     * Sets the value of the restrictionStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setRestrictionStart(JAXBElement<XMLGregorianCalendar> value) {
        this.restrictionStart = value;
    }

    /**
     * Gets the value of the restrictionEnd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getRestrictionEnd() {
        return restrictionEnd;
    }

    /**
     * Sets the value of the restrictionEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setRestrictionEnd(JAXBElement<XMLGregorianCalendar> value) {
        this.restrictionEnd = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customAttributes property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomAttributeType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<CustomAttributeType> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
