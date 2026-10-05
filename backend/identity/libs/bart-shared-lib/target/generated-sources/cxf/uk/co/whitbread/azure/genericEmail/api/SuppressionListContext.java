
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SuppressionListContext complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SuppressionListContext"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Context" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SuppressionListContextEnum" minOccurs="0"/&gt;
 *         &lt;element name="SendClassificationType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendClassificationTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="SendClassification" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendClassification" minOccurs="0"/&gt;
 *         &lt;element name="Send" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Send" minOccurs="0"/&gt;
 *         &lt;element name="Definition" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SuppressionListDefinition" minOccurs="0"/&gt;
 *         &lt;element name="AppliesToAllSends" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SenderProfile" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SenderProfile" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuppressionListContext", propOrder = {
    "context",
    "sendClassificationType",
    "sendClassification",
    "send",
    "definition",
    "appliesToAllSends",
    "senderProfile"
})
public class SuppressionListContext
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Context")
    @XmlSchemaType(name = "string")
    protected SuppressionListContextEnum context;
    @XmlElement(name = "SendClassificationType")
    @XmlSchemaType(name = "string")
    protected SendClassificationTypeEnum sendClassificationType;
    @XmlElement(name = "SendClassification")
    protected SendClassification sendClassification;
    @XmlElement(name = "Send")
    protected Send send;
    @XmlElement(name = "Definition")
    protected SuppressionListDefinition definition;
    @XmlElement(name = "AppliesToAllSends")
    protected Boolean appliesToAllSends;
    @XmlElement(name = "SenderProfile")
    protected SenderProfile senderProfile;

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link SuppressionListContextEnum }
     *     
     */
    public SuppressionListContextEnum getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuppressionListContextEnum }
     *     
     */
    public void setContext(SuppressionListContextEnum value) {
        this.context = value;
    }

    /**
     * Gets the value of the sendClassificationType property.
     * 
     * @return
     *     possible object is
     *     {@link SendClassificationTypeEnum }
     *     
     */
    public SendClassificationTypeEnum getSendClassificationType() {
        return sendClassificationType;
    }

    /**
     * Sets the value of the sendClassificationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendClassificationTypeEnum }
     *     
     */
    public void setSendClassificationType(SendClassificationTypeEnum value) {
        this.sendClassificationType = value;
    }

    /**
     * Gets the value of the sendClassification property.
     * 
     * @return
     *     possible object is
     *     {@link SendClassification }
     *     
     */
    public SendClassification getSendClassification() {
        return sendClassification;
    }

    /**
     * Sets the value of the sendClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendClassification }
     *     
     */
    public void setSendClassification(SendClassification value) {
        this.sendClassification = value;
    }

    /**
     * Gets the value of the send property.
     * 
     * @return
     *     possible object is
     *     {@link Send }
     *     
     */
    public Send getSend() {
        return send;
    }

    /**
     * Sets the value of the send property.
     * 
     * @param value
     *     allowed object is
     *     {@link Send }
     *     
     */
    public void setSend(Send value) {
        this.send = value;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link SuppressionListDefinition }
     *     
     */
    public SuppressionListDefinition getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuppressionListDefinition }
     *     
     */
    public void setDefinition(SuppressionListDefinition value) {
        this.definition = value;
    }

    /**
     * Gets the value of the appliesToAllSends property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAppliesToAllSends() {
        return appliesToAllSends;
    }

    /**
     * Sets the value of the appliesToAllSends property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAppliesToAllSends(Boolean value) {
        this.appliesToAllSends = value;
    }

    /**
     * Gets the value of the senderProfile property.
     * 
     * @return
     *     possible object is
     *     {@link SenderProfile }
     *     
     */
    public SenderProfile getSenderProfile() {
        return senderProfile;
    }

    /**
     * Sets the value of the senderProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link SenderProfile }
     *     
     */
    public void setSenderProfile(SenderProfile value) {
        this.senderProfile = value;
    }

}
