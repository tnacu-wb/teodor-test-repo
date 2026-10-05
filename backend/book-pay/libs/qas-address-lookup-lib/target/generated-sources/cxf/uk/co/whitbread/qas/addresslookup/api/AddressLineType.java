
package uk.co.whitbread.qas.addresslookup.api;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Truncated : Truncation occurred on this line
 * 
 * &lt;p&gt;Java class for AddressLineType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AddressLineType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DataplusGroup" type="{http://www.qas.com/OnDemand-2011-03}DataplusGroupType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="LineContent" type="{http://www.qas.com/OnDemand-2011-03}LineContentType" default="Address" /&gt;
 *       &lt;attribute name="Overflow" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="Truncated" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressLineType", propOrder = {
    "label",
    "line",
    "dataplusGroup"
})
public class AddressLineType {

    @XmlElement(name = "Label")
    protected String label;
    @XmlElement(name = "Line")
    protected String line;
    @XmlElement(name = "DataplusGroup")
    protected List<DataplusGroupType> dataplusGroup;
    @XmlAttribute(name = "LineContent")
    protected LineContentType lineContent;
    @XmlAttribute(name = "Overflow")
    protected Boolean overflow;
    @XmlAttribute(name = "Truncated")
    protected Boolean truncated;

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine(String value) {
        this.line = value;
    }

    /**
     * Gets the value of the dataplusGroup property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the dataplusGroup property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDataplusGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataplusGroupType }
     * </p>
     * 
     * 
     * @return
     *     The value of the dataplusGroup property.
     */
    public List<DataplusGroupType> getDataplusGroup() {
        if (dataplusGroup == null) {
            dataplusGroup = new ArrayList<>();
        }
        return this.dataplusGroup;
    }

    /**
     * Gets the value of the lineContent property.
     * 
     * @return
     *     possible object is
     *     {@link LineContentType }
     *     
     */
    public LineContentType getLineContent() {
        if (lineContent == null) {
            return LineContentType.ADDRESS;
        } else {
            return lineContent;
        }
    }

    /**
     * Sets the value of the lineContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineContentType }
     *     
     */
    public void setLineContent(LineContentType value) {
        this.lineContent = value;
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

}
