
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SendEmailMOKeyword complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SendEmailMOKeyword"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}BaseMOKeyword"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SuccessMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MissingEmailMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FailureMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TriggeredSend" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TriggeredSendDefinition" minOccurs="0"/&gt;
 *         &lt;element name="NextMOKeyword" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}BaseMOKeyword" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendEmailMOKeyword", propOrder = {
    "successMessage",
    "missingEmailMessage",
    "failureMessage",
    "triggeredSend",
    "nextMOKeyword"
})
public class SendEmailMOKeyword
    extends BaseMOKeyword
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SuccessMessage")
    protected String successMessage;
    @XmlElement(name = "MissingEmailMessage")
    protected String missingEmailMessage;
    @XmlElement(name = "FailureMessage")
    protected String failureMessage;
    @XmlElement(name = "TriggeredSend")
    protected TriggeredSendDefinition triggeredSend;
    @XmlElement(name = "NextMOKeyword")
    protected BaseMOKeyword nextMOKeyword;

    /**
     * Gets the value of the successMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Sets the value of the successMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuccessMessage(String value) {
        this.successMessage = value;
    }

    /**
     * Gets the value of the missingEmailMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMissingEmailMessage() {
        return missingEmailMessage;
    }

    /**
     * Sets the value of the missingEmailMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMissingEmailMessage(String value) {
        this.missingEmailMessage = value;
    }

    /**
     * Gets the value of the failureMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Sets the value of the failureMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailureMessage(String value) {
        this.failureMessage = value;
    }

    /**
     * Gets the value of the triggeredSend property.
     * 
     * @return
     *     possible object is
     *     {@link TriggeredSendDefinition }
     *     
     */
    public TriggeredSendDefinition getTriggeredSend() {
        return triggeredSend;
    }

    /**
     * Sets the value of the triggeredSend property.
     * 
     * @param value
     *     allowed object is
     *     {@link TriggeredSendDefinition }
     *     
     */
    public void setTriggeredSend(TriggeredSendDefinition value) {
        this.triggeredSend = value;
    }

    /**
     * Gets the value of the nextMOKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link BaseMOKeyword }
     *     
     */
    public BaseMOKeyword getNextMOKeyword() {
        return nextMOKeyword;
    }

    /**
     * Sets the value of the nextMOKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseMOKeyword }
     *     
     */
    public void setNextMOKeyword(BaseMOKeyword value) {
        this.nextMOKeyword = value;
    }

}
