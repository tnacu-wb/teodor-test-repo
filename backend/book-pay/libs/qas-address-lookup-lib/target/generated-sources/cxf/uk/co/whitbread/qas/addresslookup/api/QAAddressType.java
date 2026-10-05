
package uk.co.whitbread.qas.addresslookup.api;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * MissingSubPremise: Please refer to the associated Pro On Demand web service documentation for information about this feature.
 * 
 * &lt;p&gt;Java class for QAAddressType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="QAAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AddressLine" type="{http://www.qas.com/OnDemand-2011-03}AddressLineType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Overflow" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="Truncated" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DPVStatus" type="{http://www.qas.com/OnDemand-2011-03}DPVStatusType" /&gt;
 *       &lt;attribute name="MissingSubPremise" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QAAddressType", propOrder = {
    "addressLine"
})
public class QAAddressType {

    @XmlElement(name = "AddressLine", required = true)
    protected List<AddressLineType> addressLine;
    @XmlAttribute(name = "Overflow")
    protected Boolean overflow;
    @XmlAttribute(name = "Truncated")
    protected Boolean truncated;
    @XmlAttribute(name = "DPVStatus")
    protected DPVStatusType dpvStatus;
    @XmlAttribute(name = "MissingSubPremise")
    protected Boolean missingSubPremise;

    /**
     * Gets the value of the addressLine property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the addressLine property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAddressLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressLineType }
     * </p>
     * 
     * 
     * @return
     *     The value of the addressLine property.
     */
    public List<AddressLineType> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<>();
        }
        return this.addressLine;
    }

    /**
     * Gets the value of the overflow property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOverflow() {
        if (overflow == null) {
            return false;
        } else {
            return overflow;
        }
    }

    /**
     * Sets the value of the overflow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOverflow(Boolean value) {
        this.overflow = value;
    }

    /**
     * Gets the value of the truncated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTruncated() {
        if (truncated == null) {
            return false;
        } else {
            return truncated;
        }
    }

    /**
     * Sets the value of the truncated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTruncated(Boolean value) {
        this.truncated = value;
    }

    /**
     * Gets the value of the dpvStatus property.
     * 
     * @return
     *     possible object is
     *     {@link DPVStatusType }
     *     
     */
    public DPVStatusType getDPVStatus() {
        return dpvStatus;
    }

    /**
     * Sets the value of the dpvStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DPVStatusType }
     *     
     */
    public void setDPVStatus(DPVStatusType value) {
        this.dpvStatus = value;
    }

    /**
     * Gets the value of the missingSubPremise property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isMissingSubPremise() {
        if (missingSubPremise == null) {
            return false;
        } else {
            return missingSubPremise;
        }
    }

    /**
     * Sets the value of the missingSubPremise property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMissingSubPremise(Boolean value) {
        this.missingSubPremise = value;
    }

}
