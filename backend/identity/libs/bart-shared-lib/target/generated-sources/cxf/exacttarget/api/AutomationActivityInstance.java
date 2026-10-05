
package exacttarget.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for AutomationActivityInstance complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AutomationActivityInstance"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}AutomationActivity"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ActivityID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AutomationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SequenceID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="StatusLastUpdate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="StatusMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ActivityDefinition" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationActivity" minOccurs="0"/&gt;
 *         &lt;element name="AutomationInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationInstance" minOccurs="0"/&gt;
 *         &lt;element name="AutomationTaskInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationTaskInstance" minOccurs="0"/&gt;
 *         &lt;element name="ScheduledTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CompletedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationActivityInstance", propOrder = {
    "activityID",
    "automationID",
    "sequenceID",
    "status",
    "statusLastUpdate",
    "statusMessage",
    "activityDefinition",
    "automationInstance",
    "automationTaskInstance",
    "scheduledTime",
    "startTime",
    "completedTime"
})
public class AutomationActivityInstance
    extends AutomationActivity
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ActivityID")
    protected String activityID;
    @XmlElement(name = "AutomationID")
    protected String automationID;
    @XmlElement(name = "SequenceID")
    protected Integer sequenceID;
    @XmlElement(name = "Status")
    protected Integer status;
    @XmlElement(name = "StatusLastUpdate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime statusLastUpdate;
    @XmlElement(name = "StatusMessage")
    protected String statusMessage;
    @XmlElement(name = "ActivityDefinition")
    protected AutomationActivity activityDefinition;
    @XmlElement(name = "AutomationInstance")
    protected AutomationInstance automationInstance;
    @XmlElement(name = "AutomationTaskInstance")
    protected AutomationTaskInstance automationTaskInstance;
    @XmlElement(name = "ScheduledTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime scheduledTime;
    @XmlElement(name = "StartTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime startTime;
    @XmlElement(name = "CompletedTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime completedTime;

    /**
     * Gets the value of the activityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityID() {
        return activityID;
    }

    /**
     * Sets the value of the activityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityID(String value) {
        this.activityID = value;
    }

    /**
     * Gets the value of the automationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutomationID() {
        return automationID;
    }

    /**
     * Sets the value of the automationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutomationID(String value) {
        this.automationID = value;
    }

    /**
     * Gets the value of the sequenceID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSequenceID() {
        return sequenceID;
    }

    /**
     * Sets the value of the sequenceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSequenceID(Integer value) {
        this.sequenceID = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the statusLastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getStatusLastUpdate() {
        return statusLastUpdate;
    }

    /**
     * Sets the value of the statusLastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusLastUpdate(LocalDateTime value) {
        this.statusLastUpdate = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Gets the value of the activityDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationActivity }
     *     
     */
    public AutomationActivity getActivityDefinition() {
        return activityDefinition;
    }

    /**
     * Sets the value of the activityDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationActivity }
     *     
     */
    public void setActivityDefinition(AutomationActivity value) {
        this.activityDefinition = value;
    }

    /**
     * Gets the value of the automationInstance property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationInstance }
     *     
     */
    public AutomationInstance getAutomationInstance() {
        return automationInstance;
    }

    /**
     * Sets the value of the automationInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationInstance }
     *     
     */
    public void setAutomationInstance(AutomationInstance value) {
        this.automationInstance = value;
    }

    /**
     * Gets the value of the automationTaskInstance property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationTaskInstance }
     *     
     */
    public AutomationTaskInstance getAutomationTaskInstance() {
        return automationTaskInstance;
    }

    /**
     * Sets the value of the automationTaskInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationTaskInstance }
     *     
     */
    public void setAutomationTaskInstance(AutomationTaskInstance value) {
        this.automationTaskInstance = value;
    }

    /**
     * Gets the value of the scheduledTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Sets the value of the scheduledTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduledTime(LocalDateTime value) {
        this.scheduledTime = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(LocalDateTime value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the completedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    /**
     * Sets the value of the completedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompletedTime(LocalDateTime value) {
        this.completedTime = value;
    }

}
