
package uk.co.whitbread.bart.business.api;

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
 *         &lt;element name="UpdateDashboardMessageResult" type="{http://corporate.micros.com/1.0}UpdateDashboardMessageResponse"/&gt;
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
    "updateDashboardMessageResult"
})
@XmlRootElement(name = "UpdateDashboardMessageResponse")
public class UpdateDashboardMessageResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UpdateDashboardMessageResult", required = true)
    protected UpdateDashboardMessageResponse2 updateDashboardMessageResult;

    /**
     * Gets the value of the updateDashboardMessageResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateDashboardMessageResponse2 }
     *     
     */
    public UpdateDashboardMessageResponse2 getUpdateDashboardMessageResult() {
        return updateDashboardMessageResult;
    }

    /**
     * Sets the value of the updateDashboardMessageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateDashboardMessageResponse2 }
     *     
     */
    public void setUpdateDashboardMessageResult(UpdateDashboardMessageResponse2 value) {
        this.updateDashboardMessageResult = value;
    }

}
