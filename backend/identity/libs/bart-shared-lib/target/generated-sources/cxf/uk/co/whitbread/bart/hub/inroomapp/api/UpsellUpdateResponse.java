
package uk.co.whitbread.bart.hub.inroomapp.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="UpsellUpdateResult" type="{http://hub.micros.com/1.0}UpsellUpdateResponse"/&gt;
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
    "upsellUpdateResult"
})
@XmlRootElement(name = "UpsellUpdateResponse")
public class UpsellUpdateResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UpsellUpdateResult", required = true)
    protected UpsellUpdateResponse2 upsellUpdateResult;

    /**
     * Gets the value of the upsellUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpsellUpdateResponse2 }
     *     
     */
    public UpsellUpdateResponse2 getUpsellUpdateResult() {
        return upsellUpdateResult;
    }

    /**
     * Sets the value of the upsellUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsellUpdateResponse2 }
     *     
     */
    public void setUpsellUpdateResult(UpsellUpdateResponse2 value) {
        this.upsellUpdateResult = value;
    }

}
