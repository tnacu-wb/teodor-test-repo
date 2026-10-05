
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HourlyRecurrence complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="HourlyRecurrence">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Recurrence">
 *       <sequence>
 *         <element name="HourlyRecurrencePatternType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}HourlyRecurrencePatternTypeEnum" minOccurs="0"/>
 *         <element name="HourInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HourlyRecurrence", propOrder = {
    "hourlyRecurrencePatternType",
    "hourInterval"
})
public class HourlyRecurrence
    extends Recurrence
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "HourlyRecurrencePatternType")
    @XmlSchemaType(name = "string")
    protected HourlyRecurrencePatternTypeEnum hourlyRecurrencePatternType;
    @XmlElement(name = "HourInterval")
    protected Integer hourInterval;

    /**
     * Gets the value of the hourlyRecurrencePatternType property.
     * 
     * @return
     *     possible object is
     *     {@link HourlyRecurrencePatternTypeEnum }
     *     
     */
    public HourlyRecurrencePatternTypeEnum getHourlyRecurrencePatternType() {
        return hourlyRecurrencePatternType;
    }

    /**
     * Sets the value of the hourlyRecurrencePatternType property.
     * 
     * @param value
     *     allowed object is
     *     {@link HourlyRecurrencePatternTypeEnum }
     *     
     */
    public void setHourlyRecurrencePatternType(HourlyRecurrencePatternTypeEnum value) {
        this.hourlyRecurrencePatternType = value;
    }

    /**
     * Gets the value of the hourInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHourInterval() {
        return hourInterval;
    }

    /**
     * Sets the value of the hourInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHourInterval(Integer value) {
        this.hourInterval = value;
    }

}
