
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Moniker" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Refinement" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Layout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FormattedAddressInPicklist" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Threshold" type="{http://www.qas.com/OnDemand-2011-03}ThresholdType" /&gt;
 *       &lt;attribute name="Timeout" type="{http://www.qas.com/OnDemand-2011-03}TimeoutType" /&gt;
 *       &lt;attribute name="Localisation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="RequestTag" type="{http://www.qas.com/OnDemand-2011-03}RequestTagType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "moniker",
    "refinement",
    "layout",
    "formattedAddressInPicklist"
})
@XmlRootElement(name = "QARefine")
public class QARefine {

    @XmlElement(name = "Moniker", required = true)
    protected String moniker;
    @XmlElement(name = "Refinement", required = true)
    protected String refinement;
    @XmlElement(name = "Layout")
    protected String layout;
    @XmlElement(name = "FormattedAddressInPicklist", defaultValue = "false")
    protected Boolean formattedAddressInPicklist;
    @XmlAttribute(name = "Threshold")
    protected Integer threshold;
    @XmlAttribute(name = "Timeout")
    protected Integer timeout;
    @XmlAttribute(name = "Localisation")
    protected String localisation;
    @XmlAttribute(name = "RequestTag")
    protected String requestTag;

    /**
     * Gets the value of the moniker property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoniker() {
        return moniker;
    }

    /**
     * Sets the value of the moniker property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoniker(String value) {
        this.moniker = value;
    }

    /**
     * Gets the value of the refinement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefinement() {
        return refinement;
    }

    /**
     * Sets the value of the refinement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefinement(String value) {
        this.refinement = value;
    }

    /**
     * Gets the value of the layout property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Sets the value of the layout property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayout(String value) {
        this.layout = value;
    }

    /**
     * Gets the value of the formattedAddressInPicklist property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFormattedAddressInPicklist() {
        return formattedAddressInPicklist;
    }

    /**
     * Sets the value of the formattedAddressInPicklist property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFormattedAddressInPicklist(Boolean value) {
        this.formattedAddressInPicklist = value;
    }

    /**
     * Gets the value of the threshold property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * Sets the value of the threshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setThreshold(Integer value) {
        this.threshold = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTimeout(Integer value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the localisation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalisation() {
        return localisation;
    }

    /**
     * Sets the value of the localisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalisation(String value) {
        this.localisation = value;
    }

    /**
     * Gets the value of the requestTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestTag() {
        return requestTag;
    }

    /**
     * Sets the value of the requestTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestTag(String value) {
        this.requestTag = value;
    }

}
