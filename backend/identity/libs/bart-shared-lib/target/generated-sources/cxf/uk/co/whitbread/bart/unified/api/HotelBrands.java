
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for HotelBrands complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="HotelBrands"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelBrandCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hotelBrandLegend" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="titles" type="{http://bartws.micros.com/1.17}ArrayOftitlesItemString"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelBrands", propOrder = {
    "hotelBrandCode",
    "hotelBrandLegend",
    "titles"
})
public class HotelBrands
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String hotelBrandCode;
    @XmlElement(required = true)
    protected String hotelBrandLegend;
    @XmlElement(required = true)
    protected ArrayOftitlesItemString titles;

    /**
     * Gets the value of the hotelBrandCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelBrandCode() {
        return hotelBrandCode;
    }

    /**
     * Sets the value of the hotelBrandCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelBrandCode(String value) {
        this.hotelBrandCode = value;
    }

    /**
     * Gets the value of the hotelBrandLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelBrandLegend() {
        return hotelBrandLegend;
    }

    /**
     * Sets the value of the hotelBrandLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelBrandLegend(String value) {
        this.hotelBrandLegend = value;
    }

    /**
     * Gets the value of the titles property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOftitlesItemString }
     *     
     */
    public ArrayOftitlesItemString getTitles() {
        return titles;
    }

    /**
     * Sets the value of the titles property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOftitlesItemString }
     *     
     */
    public void setTitles(ArrayOftitlesItemString value) {
        this.titles = value;
    }

}
