
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
 * &lt;p&gt;Java class for CarParkOperators complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CarParkOperators"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="operatorCode"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="operatorName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="earliestTime" type="{http://bartws.micros.com/1.17}Time" minOccurs="0"/&gt;
 *         &lt;element name="latestTime" type="{http://bartws.micros.com/1.17}Time" minOccurs="0"/&gt;
 *         &lt;element name="terminals" type="{http://bartws.micros.com/1.17}ArrayOfterminalCarParkTerminals" minOccurs="0"/&gt;
 *         &lt;element name="inputFields" type="{http://bartws.micros.com/1.17}ArrayOfinputFieldCarParkInputFields" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarParkOperators", propOrder = {
    "operatorCode",
    "operatorName",
    "earliestTime",
    "latestTime",
    "terminals",
    "inputFields"
})
public class CarParkOperators
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String operatorCode;
    @XmlElement(required = true)
    protected String operatorName;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime earliestTime;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime latestTime;
    protected ArrayOfterminalCarParkTerminals terminals;
    protected ArrayOfinputFieldCarParkInputFields inputFields;

    /**
     * Gets the value of the operatorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorCode() {
        return operatorCode;
    }

    /**
     * Sets the value of the operatorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorCode(String value) {
        this.operatorCode = value;
    }

    /**
     * Gets the value of the operatorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * Sets the value of the operatorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorName(String value) {
        this.operatorName = value;
    }

    /**
     * Gets the value of the earliestTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getEarliestTime() {
        return earliestTime;
    }

    /**
     * Sets the value of the earliestTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEarliestTime(LocalTime value) {
        this.earliestTime = value;
    }

    /**
     * Gets the value of the latestTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getLatestTime() {
        return latestTime;
    }

    /**
     * Sets the value of the latestTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatestTime(LocalTime value) {
        this.latestTime = value;
    }

    /**
     * Gets the value of the terminals property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfterminalCarParkTerminals }
     *     
     */
    public ArrayOfterminalCarParkTerminals getTerminals() {
        return terminals;
    }

    /**
     * Sets the value of the terminals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfterminalCarParkTerminals }
     *     
     */
    public void setTerminals(ArrayOfterminalCarParkTerminals value) {
        this.terminals = value;
    }

    /**
     * Gets the value of the inputFields property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfinputFieldCarParkInputFields }
     *     
     */
    public ArrayOfinputFieldCarParkInputFields getInputFields() {
        return inputFields;
    }

    /**
     * Sets the value of the inputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfinputFieldCarParkInputFields }
     *     
     */
    public void setInputFields(ArrayOfinputFieldCarParkInputFields value) {
        this.inputFields = value;
    }

}
