
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BrowserDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BrowserDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="windowSize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="colourDepth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="javaEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="screenHeight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="screenWidth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowserDetails", propOrder = {
    "windowSize",
    "colourDepth",
    "javaEnabled",
    "language",
    "screenHeight",
    "screenWidth",
    "timeZone"
})
public class BrowserDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String windowSize;
    protected String colourDepth;
    protected Boolean javaEnabled;
    protected String language;
    protected String screenHeight;
    protected String screenWidth;
    protected String timeZone;

    /**
     * Gets the value of the windowSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWindowSize() {
        return windowSize;
    }

    /**
     * Sets the value of the windowSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWindowSize(String value) {
        this.windowSize = value;
    }

    /**
     * Gets the value of the colourDepth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColourDepth() {
        return colourDepth;
    }

    /**
     * Sets the value of the colourDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColourDepth(String value) {
        this.colourDepth = value;
    }

    /**
     * Gets the value of the javaEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isJavaEnabled() {
        return javaEnabled;
    }

    /**
     * Sets the value of the javaEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setJavaEnabled(Boolean value) {
        this.javaEnabled = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the screenHeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScreenHeight() {
        return screenHeight;
    }

    /**
     * Sets the value of the screenHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScreenHeight(String value) {
        this.screenHeight = value;
    }

    /**
     * Gets the value of the screenWidth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScreenWidth() {
        return screenWidth;
    }

    /**
     * Sets the value of the screenWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScreenWidth(String value) {
        this.screenWidth = value;
    }

    /**
     * Gets the value of the timeZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the value of the timeZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeZone(String value) {
        this.timeZone = value;
    }

}
