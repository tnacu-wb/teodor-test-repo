
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for FoodBeverageExclusions complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="FoodBeverageExclusions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="unavailableDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="breakfastPostingCode" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastPostingCodeItemString" minOccurs="0"/&gt;
 *         &lt;element name="breakfastAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="breakfastAlternatives" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastAlternativeBreakfastAlternative" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FoodBeverageExclusions", propOrder = {
    "unavailableDate",
    "breakfastPostingCode",
    "breakfastAvailable",
    "dinnerAvailable",
    "breakfastAlternatives"
})
public class FoodBeverageExclusions
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate unavailableDate;
    protected ArrayOfbreakfastPostingCodeItemString breakfastPostingCode;
    protected Boolean breakfastAvailable;
    protected Boolean dinnerAvailable;
    protected ArrayOfbreakfastAlternativeBreakfastAlternative breakfastAlternatives;

    /**
     * Gets the value of the unavailableDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getUnavailableDate() {
        return unavailableDate;
    }

    /**
     * Sets the value of the unavailableDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnavailableDate(LocalDate value) {
        this.unavailableDate = value;
    }

    /**
     * Gets the value of the breakfastPostingCode property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastPostingCodeItemString }
     *     
     */
    public ArrayOfbreakfastPostingCodeItemString getBreakfastPostingCode() {
        return breakfastPostingCode;
    }

    /**
     * Sets the value of the breakfastPostingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastPostingCodeItemString }
     *     
     */
    public void setBreakfastPostingCode(ArrayOfbreakfastPostingCodeItemString value) {
        this.breakfastPostingCode = value;
    }

    /**
     * Gets the value of the breakfastAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBreakfastAvailable() {
        return breakfastAvailable;
    }

    /**
     * Sets the value of the breakfastAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBreakfastAvailable(Boolean value) {
        this.breakfastAvailable = value;
    }

    /**
     * Gets the value of the dinnerAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailable() {
        return dinnerAvailable;
    }

    /**
     * Sets the value of the dinnerAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailable(Boolean value) {
        this.dinnerAvailable = value;
    }

    /**
     * Gets the value of the breakfastAlternatives property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastAlternativeBreakfastAlternative }
     *     
     */
    public ArrayOfbreakfastAlternativeBreakfastAlternative getBreakfastAlternatives() {
        return breakfastAlternatives;
    }

    /**
     * Sets the value of the breakfastAlternatives property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastAlternativeBreakfastAlternative }
     *     
     */
    public void setBreakfastAlternatives(ArrayOfbreakfastAlternativeBreakfastAlternative value) {
        this.breakfastAlternatives = value;
    }

}
