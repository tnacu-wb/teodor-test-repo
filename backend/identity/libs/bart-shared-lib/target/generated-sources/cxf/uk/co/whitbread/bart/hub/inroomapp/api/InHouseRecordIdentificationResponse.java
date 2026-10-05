
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
 *         &lt;element name="InHouseRecordIdentificationResult" type="{http://hub.micros.com/1.0}InHouseIdentificationResponse"/&gt;
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
    "inHouseRecordIdentificationResult"
})
@XmlRootElement(name = "InHouseRecordIdentificationResponse")
public class InHouseRecordIdentificationResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InHouseRecordIdentificationResult", required = true)
    protected InHouseIdentificationResponse inHouseRecordIdentificationResult;

    /**
     * Gets the value of the inHouseRecordIdentificationResult property.
     * 
     * @return
     *     possible object is
     *     {@link InHouseIdentificationResponse }
     *     
     */
    public InHouseIdentificationResponse getInHouseRecordIdentificationResult() {
        return inHouseRecordIdentificationResult;
    }

    /**
     * Sets the value of the inHouseRecordIdentificationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link InHouseIdentificationResponse }
     *     
     */
    public void setInHouseRecordIdentificationResult(InHouseIdentificationResponse value) {
        this.inHouseRecordIdentificationResult = value;
    }

}
