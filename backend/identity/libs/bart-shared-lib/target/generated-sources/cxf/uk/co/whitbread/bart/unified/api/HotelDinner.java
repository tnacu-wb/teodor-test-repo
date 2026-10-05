
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import java.time.LocalTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for HotelDinner complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="HotelDinner"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dinnerAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="dinnerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dinnerStartTime" type="{http://bartws.micros.com/1.17}Time" minOccurs="0"/&gt;
 *         &lt;element name="dinnerEndTime" type="{http://bartws.micros.com/1.17}Time" minOccurs="0"/&gt;
 *         &lt;element name="maxCovers" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="dinnerInterval" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelDinner", propOrder = {
    "dinnerAvailable",
    "dinnerCode",
    "description",
    "dinnerStartTime",
    "dinnerEndTime",
    "maxCovers",
    "dinnerInterval"
})
public class HotelDinner
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean dinnerAvailable;
    protected String dinnerCode;
    protected String description;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime dinnerStartTime;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime dinnerEndTime;
    protected Long maxCovers;
    protected Long dinnerInterval;

    /**
     * Gets the value of the dinnerAvailable property.
     * 
     */
    public boolean isDinnerAvailable() {
        return dinnerAvailable;
    }

    /**
     * Sets the value of the dinnerAvailable property.
     * 
     */
    public void setDinnerAvailable(boolean value) {
        this.dinnerAvailable = value;
    }

    /**
     * Gets the value of the dinnerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDinnerCode() {
        return dinnerCode;
    }

    /**
     * Sets the value of the dinnerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinnerCode(String value) {
        this.dinnerCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the dinnerStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getDinnerStartTime() {
        return dinnerStartTime;
    }

    /**
     * Sets the value of the dinnerStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinnerStartTime(LocalTime value) {
        this.dinnerStartTime = value;
    }

    /**
     * Gets the value of the dinnerEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getDinnerEndTime() {
        return dinnerEndTime;
    }

    /**
     * Sets the value of the dinnerEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinnerEndTime(LocalTime value) {
        this.dinnerEndTime = value;
    }

    /**
     * Gets the value of the maxCovers property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxCovers() {
        return maxCovers;
    }

    /**
     * Sets the value of the maxCovers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxCovers(Long value) {
        this.maxCovers = value;
    }

    /**
     * Gets the value of the dinnerInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDinnerInterval() {
        return dinnerInterval;
    }

    /**
     * Sets the value of the dinnerInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDinnerInterval(Long value) {
        this.dinnerInterval = value;
    }

}
