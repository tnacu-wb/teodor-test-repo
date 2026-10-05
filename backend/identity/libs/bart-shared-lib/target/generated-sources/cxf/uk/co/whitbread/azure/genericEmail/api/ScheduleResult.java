
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ScheduleResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ScheduleResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Result"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Object" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ScheduleDefinition"/&gt;
 *         &lt;element name="Task" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TaskResult"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScheduleResult", propOrder = {
    "object",
    "task"
})
public class ScheduleResult
    extends Result
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Object", required = true)
    protected ScheduleDefinition object;
    @XmlElement(name = "Task", required = true)
    protected TaskResult task;

    /**
     * Gets the value of the object property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleDefinition }
     *     
     */
    public ScheduleDefinition getObject() {
        return object;
    }

    /**
     * Sets the value of the object property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleDefinition }
     *     
     */
    public void setObject(ScheduleDefinition value) {
        this.object = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link TaskResult }
     *     
     */
    public TaskResult getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskResult }
     *     
     */
    public void setTask(TaskResult value) {
        this.task = value;
    }

}
