
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for QAInformation complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="QAInformation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StateTransition" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CreditsUsed" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QAInformation", propOrder = {
    "stateTransition",
    "creditsUsed"
})
public class QAInformation {

    @XmlElement(name = "StateTransition", required = true)
    protected String stateTransition;
    @XmlElement(name = "CreditsUsed")
    protected long creditsUsed;

    /**
     * Gets the value of the stateTransition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateTransition() {
        return stateTransition;
    }

    /**
     * Sets the value of the stateTransition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateTransition(String value) {
        this.stateTransition = value;
    }

    /**
     * Gets the value of the creditsUsed property.
     * 
     */
    public long getCreditsUsed() {
        return creditsUsed;
    }

    /**
     * Sets the value of the creditsUsed property.
     * 
     */
    public void setCreditsUsed(long value) {
        this.creditsUsed = value;
    }

}
