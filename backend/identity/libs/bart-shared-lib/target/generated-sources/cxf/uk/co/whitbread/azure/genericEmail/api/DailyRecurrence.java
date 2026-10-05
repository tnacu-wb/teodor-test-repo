
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for DailyRecurrence complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DailyRecurrence"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Recurrence"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DailyRecurrencePatternType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DailyRecurrencePatternTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="DayInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DailyRecurrence", propOrder = {
    "dailyRecurrencePatternType",
    "dayInterval"
})
public class DailyRecurrence
    extends Recurrence
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "DailyRecurrencePatternType")
    @XmlSchemaType(name = "string")
    protected DailyRecurrencePatternTypeEnum dailyRecurrencePatternType;
    @XmlElement(name = "DayInterval")
    protected Integer dayInterval;

    /**
     * Gets the value of the dailyRecurrencePatternType property.
     * 
     * @return
     *     possible object is
     *     {@link DailyRecurrencePatternTypeEnum }
     *     
     */
    public DailyRecurrencePatternTypeEnum getDailyRecurrencePatternType() {
        return dailyRecurrencePatternType;
    }

    /**
     * Sets the value of the dailyRecurrencePatternType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DailyRecurrencePatternTypeEnum }
     *     
     */
    public void setDailyRecurrencePatternType(DailyRecurrencePatternTypeEnum value) {
        this.dailyRecurrencePatternType = value;
    }

    /**
     * Gets the value of the dayInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDayInterval() {
        return dayInterval;
    }

    /**
     * Sets the value of the dayInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDayInterval(Integer value) {
        this.dayInterval = value;
    }

}
