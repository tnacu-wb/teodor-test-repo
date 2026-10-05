
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for DinnerUpsell complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DinnerUpsell"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dinnerDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dinnerDiners" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="dinnerTime" type="{http://bartws.micros.com/1.31}Time" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DinnerUpsell", propOrder = {
    "dinnerDate",
    "dinnerDiners",
    "dinnerTime"
})
public class DinnerUpsell
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate dinnerDate;
    protected Long dinnerDiners;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime dinnerTime;

    /**
     * Gets the value of the dinnerDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getDinnerDate() {
        return dinnerDate;
    }

    /**
     * Sets the value of the dinnerDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinnerDate(LocalDate value) {
        this.dinnerDate = value;
    }

    /**
     * Gets the value of the dinnerDiners property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDinnerDiners() {
        return dinnerDiners;
    }

    /**
     * Sets the value of the dinnerDiners property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDinnerDiners(Long value) {
        this.dinnerDiners = value;
    }

    /**
     * Gets the value of the dinnerTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getDinnerTime() {
        return dinnerTime;
    }

    /**
     * Sets the value of the dinnerTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinnerTime(LocalTime value) {
        this.dinnerTime = value;
    }

}
