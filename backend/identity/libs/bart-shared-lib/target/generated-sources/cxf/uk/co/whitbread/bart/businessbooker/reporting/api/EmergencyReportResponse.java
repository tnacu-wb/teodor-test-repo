
package uk.co.whitbread.bart.businessbooker.reporting.api;

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
 *         &lt;element name="EmergencyReportResult" type="{http://corporate.micros.com/1.0}EmergencyReportResponse"/&gt;
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
    "emergencyReportResult"
})
@XmlRootElement(name = "EmergencyReportResponse")
public class EmergencyReportResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "EmergencyReportResult", required = true)
    protected EmergencyReportResponse2 emergencyReportResult;

    /**
     * Gets the value of the emergencyReportResult property.
     * 
     * @return
     *     possible object is
     *     {@link EmergencyReportResponse2 }
     *     
     */
    public EmergencyReportResponse2 getEmergencyReportResult() {
        return emergencyReportResult;
    }

    /**
     * Sets the value of the emergencyReportResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmergencyReportResponse2 }
     *     
     */
    public void setEmergencyReportResult(EmergencyReportResponse2 value) {
        this.emergencyReportResult = value;
    }

}
