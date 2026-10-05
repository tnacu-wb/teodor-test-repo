
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
 * &lt;p&gt;Java class for SCADetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SCADetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tdsVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tdsOutcome"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="AUTHENTICATED"/&gt;
 *               &lt;enumeration value="ATTEMPTED"/&gt;
 *               &lt;enumeration value="NOTENROLLED"/&gt;
 *               &lt;enumeration value="BYPASSED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="authAmount" type="{http://bartws.micros.com/1.31}Price" minOccurs="0"/&gt;
 *         &lt;element name="authDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="authTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="scaTransRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SCADetails", propOrder = {
    "tdsVersion",
    "tdsOutcome",
    "authAmount",
    "authDate",
    "authTime",
    "scaTransRef"
})
public class SCADetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String tdsVersion;
    @XmlElement(required = true)
    protected String tdsOutcome;
    protected Price authAmount;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate authDate;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime authTime;
    protected String scaTransRef;

    /**
     * Gets the value of the tdsVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTdsVersion() {
        return tdsVersion;
    }

    /**
     * Sets the value of the tdsVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTdsVersion(String value) {
        this.tdsVersion = value;
    }

    /**
     * Gets the value of the tdsOutcome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTdsOutcome() {
        return tdsOutcome;
    }

    /**
     * Sets the value of the tdsOutcome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTdsOutcome(String value) {
        this.tdsOutcome = value;
    }

    /**
     * Gets the value of the authAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getAuthAmount() {
        return authAmount;
    }

    /**
     * Sets the value of the authAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setAuthAmount(Price value) {
        this.authAmount = value;
    }

    /**
     * Gets the value of the authDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getAuthDate() {
        return authDate;
    }

    /**
     * Sets the value of the authDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthDate(LocalDate value) {
        this.authDate = value;
    }

    /**
     * Gets the value of the authTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getAuthTime() {
        return authTime;
    }

    /**
     * Sets the value of the authTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthTime(LocalTime value) {
        this.authTime = value;
    }

    /**
     * Gets the value of the scaTransRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScaTransRef() {
        return scaTransRef;
    }

    /**
     * Sets the value of the scaTransRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScaTransRef(String value) {
        this.scaTransRef = value;
    }

}
