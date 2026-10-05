
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * Timeout : The time out period in milliseconds
 * 
 * &lt;p&gt;Java class for EngineType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EngineType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.qas.com/OnDemand-2011-03&gt;EngineEnumType"&gt;
 *       &lt;attribute name="Flatten" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="Intensity" type="{http://www.qas.com/OnDemand-2011-03}EngineIntensityType" /&gt;
 *       &lt;attribute name="PromptSet" type="{http://www.qas.com/OnDemand-2011-03}PromptSetType" /&gt;
 *       &lt;attribute name="Threshold" type="{http://www.qas.com/OnDemand-2011-03}ThresholdType" /&gt;
 *       &lt;attribute name="Timeout" type="{http://www.qas.com/OnDemand-2011-03}TimeoutType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EngineType", propOrder = {
    "value"
})
public class EngineType {

    /**
     * The available engines
     * 
     */
    @XmlValue
    protected EngineEnumType value;
    @XmlAttribute(name = "Flatten")
    protected Boolean flatten;
    @XmlAttribute(name = "Intensity")
    protected EngineIntensityType intensity;
    @XmlAttribute(name = "PromptSet")
    protected PromptSetType promptSet;
    @XmlAttribute(name = "Threshold")
    protected Integer threshold;
    @XmlAttribute(name = "Timeout")
    protected Integer timeout;

    /**
     * The available engines
     * 
     * @return
     *     possible object is
     *     {@link EngineEnumType }
     *     
     */
    public EngineEnumType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link EngineEnumType }
     *     
     * @see #getValue()
     */
    public void setValue(EngineEnumType value) {
        this.value = value;
    }

    /**
     * Gets the value of the flatten property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFlatten() {
        return flatten;
    }

    /**
     * Sets the value of the flatten property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFlatten(Boolean value) {
        this.flatten = value;
    }

    /**
     * Gets the value of the intensity property.
     * 
     * @return
     *     possible object is
     *     {@link EngineIntensityType }
     *     
     */
    public EngineIntensityType getIntensity() {
        return intensity;
    }

    /**
     * Sets the value of the intensity property.
     * 
     * @param value
     *     allowed object is
     *     {@link EngineIntensityType }
     *     
     */
    public void setIntensity(EngineIntensityType value) {
        this.intensity = value;
    }

    /**
     * Gets the value of the promptSet property.
     * 
     * @return
     *     possible object is
     *     {@link PromptSetType }
     *     
     */
    public PromptSetType getPromptSet() {
        return promptSet;
    }

    /**
     * Sets the value of the promptSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link PromptSetType }
     *     
     */
    public void setPromptSet(PromptSetType value) {
        this.promptSet = value;
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

}
