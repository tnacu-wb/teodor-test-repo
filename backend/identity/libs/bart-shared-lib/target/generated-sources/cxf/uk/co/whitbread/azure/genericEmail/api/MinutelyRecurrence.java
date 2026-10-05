
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for MinutelyRecurrence complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MinutelyRecurrence"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Recurrence"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MinutelyRecurrencePatternType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}MinutelyRecurrencePatternTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="MinuteInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinutelyRecurrence", propOrder = {
    "minutelyRecurrencePatternType",
    "minuteInterval"
})
public class MinutelyRecurrence
    extends Recurrence
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "MinutelyRecurrencePatternType")
    @XmlSchemaType(name = "string")
    protected MinutelyRecurrencePatternTypeEnum minutelyRecurrencePatternType;
    @XmlElement(name = "MinuteInterval")
    protected Integer minuteInterval;

    /**
     * Gets the value of the minutelyRecurrencePatternType property.
     * 
     * @return
     *     possible object is
     *     {@link MinutelyRecurrencePatternTypeEnum }
     *     
     */
    public MinutelyRecurrencePatternTypeEnum getMinutelyRecurrencePatternType() {
        return minutelyRecurrencePatternType;
    }

    /**
     * Sets the value of the minutelyRecurrencePatternType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MinutelyRecurrencePatternTypeEnum }
     *     
     */
    public void setMinutelyRecurrencePatternType(MinutelyRecurrencePatternTypeEnum value) {
        this.minutelyRecurrencePatternType = value;
    }

    /**
     * Gets the value of the minuteInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinuteInterval() {
        return minuteInterval;
    }

    /**
     * Sets the value of the minuteInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinuteInterval(Integer value) {
        this.minuteInterval = value;
    }

}
