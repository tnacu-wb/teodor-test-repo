
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MinutelyRecurrence complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="MinutelyRecurrence">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Recurrence">
 *       <sequence>
 *         <element name="MinutelyRecurrencePatternType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}MinutelyRecurrencePatternTypeEnum" minOccurs="0"/>
 *         <element name="MinuteInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
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
