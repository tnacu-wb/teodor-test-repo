
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PerformResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PerformResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}Result"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Object" type="{http://exacttarget.com/wsdl/partnerAPI}APIObject"/&gt;
 *         &lt;element name="Task" type="{http://exacttarget.com/wsdl/partnerAPI}TaskResult"/&gt;
 *         &lt;element name="ProgramActivityInstanceId" type="{http://exacttarget.com/wsdl/partnerAPI}instanceid" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerformResult", propOrder = {
    "object",
    "task",
    "programActivityInstanceId"
})
@XmlSeeAlso({
    ContentValidationResult.class
})
public class PerformResult
    extends Result
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Object", required = true)
    protected APIObject object;
    @XmlElement(name = "Task", required = true)
    protected TaskResult task;
    @XmlElement(name = "ProgramActivityInstanceId")
    protected String programActivityInstanceId;

    /**
     * Gets the value of the object property.
     * 
     * @return
     *     possible object is
     *     {@link APIObject }
     *     
     */
    public APIObject getObject() {
        return object;
    }

    /**
     * Sets the value of the object property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIObject }
     *     
     */
    public void setObject(APIObject value) {
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

    /**
     * Gets the value of the programActivityInstanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramActivityInstanceId() {
        return programActivityInstanceId;
    }

    /**
     * Sets the value of the programActivityInstanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramActivityInstanceId(String value) {
        this.programActivityInstanceId = value;
    }

}
