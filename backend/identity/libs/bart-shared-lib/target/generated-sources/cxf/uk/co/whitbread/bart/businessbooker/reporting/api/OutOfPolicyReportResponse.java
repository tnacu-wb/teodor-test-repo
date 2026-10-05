
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
 *         &lt;element name="OutOfPolicyReportResult" type="{http://corporate.micros.com/1.0}OutOfPolicyResponse"/&gt;
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
    "outOfPolicyReportResult"
})
@XmlRootElement(name = "OutOfPolicyReportResponse")
public class OutOfPolicyReportResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "OutOfPolicyReportResult", required = true)
    protected OutOfPolicyResponse outOfPolicyReportResult;

    /**
     * Gets the value of the outOfPolicyReportResult property.
     * 
     * @return
     *     possible object is
     *     {@link OutOfPolicyResponse }
     *     
     */
    public OutOfPolicyResponse getOutOfPolicyReportResult() {
        return outOfPolicyReportResult;
    }

    /**
     * Sets the value of the outOfPolicyReportResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutOfPolicyResponse }
     *     
     */
    public void setOutOfPolicyReportResult(OutOfPolicyResponse value) {
        this.outOfPolicyReportResult = value;
    }

}
