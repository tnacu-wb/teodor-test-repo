
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for MonthlyRecurrence complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MonthlyRecurrence"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Recurrence"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MonthlyRecurrencePatternType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}MonthlyRecurrencePatternTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="MonthlyInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ScheduledDay" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ScheduledWeek" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}WeekOfMonthEnum" minOccurs="0"/&gt;
 *         &lt;element name="ScheduledDayOfWeek" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DayOfWeekEnum" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonthlyRecurrence", propOrder = {
    "monthlyRecurrencePatternType",
    "monthlyInterval",
    "scheduledDay",
    "scheduledWeek",
    "scheduledDayOfWeek"
})
public class MonthlyRecurrence
    extends Recurrence
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "MonthlyRecurrencePatternType")
    @XmlSchemaType(name = "string")
    protected MonthlyRecurrencePatternTypeEnum monthlyRecurrencePatternType;
    @XmlElement(name = "MonthlyInterval")
    protected Integer monthlyInterval;
    @XmlElement(name = "ScheduledDay")
    protected Integer scheduledDay;
    @XmlElement(name = "ScheduledWeek")
    @XmlSchemaType(name = "string")
    protected WeekOfMonthEnum scheduledWeek;
    @XmlElement(name = "ScheduledDayOfWeek")
    @XmlSchemaType(name = "string")
    protected DayOfWeekEnum scheduledDayOfWeek;

    /**
     * Gets the value of the monthlyRecurrencePatternType property.
     * 
     * @return
     *     possible object is
     *     {@link MonthlyRecurrencePatternTypeEnum }
     *     
     */
    public MonthlyRecurrencePatternTypeEnum getMonthlyRecurrencePatternType() {
        return monthlyRecurrencePatternType;
    }

    /**
     * Sets the value of the monthlyRecurrencePatternType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonthlyRecurrencePatternTypeEnum }
     *     
     */
    public void setMonthlyRecurrencePatternType(MonthlyRecurrencePatternTypeEnum value) {
        this.monthlyRecurrencePatternType = value;
    }

    /**
     * Gets the value of the monthlyInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMonthlyInterval() {
        return monthlyInterval;
    }

    /**
     * Sets the value of the monthlyInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMonthlyInterval(Integer value) {
        this.monthlyInterval = value;
    }

    /**
     * Gets the value of the scheduledDay property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getScheduledDay() {
        return scheduledDay;
    }

    /**
     * Sets the value of the scheduledDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setScheduledDay(Integer value) {
        this.scheduledDay = value;
    }

    /**
     * Gets the value of the scheduledWeek property.
     * 
     * @return
     *     possible object is
     *     {@link WeekOfMonthEnum }
     *     
     */
    public WeekOfMonthEnum getScheduledWeek() {
        return scheduledWeek;
    }

    /**
     * Sets the value of the scheduledWeek property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeekOfMonthEnum }
     *     
     */
    public void setScheduledWeek(WeekOfMonthEnum value) {
        this.scheduledWeek = value;
    }

    /**
     * Gets the value of the scheduledDayOfWeek property.
     * 
     * @return
     *     possible object is
     *     {@link DayOfWeekEnum }
     *     
     */
    public DayOfWeekEnum getScheduledDayOfWeek() {
        return scheduledDayOfWeek;
    }

    /**
     * Sets the value of the scheduledDayOfWeek property.
     * 
     * @param value
     *     allowed object is
     *     {@link DayOfWeekEnum }
     *     
     */
    public void setScheduledDayOfWeek(DayOfWeekEnum value) {
        this.scheduledDayOfWeek = value;
    }

}
