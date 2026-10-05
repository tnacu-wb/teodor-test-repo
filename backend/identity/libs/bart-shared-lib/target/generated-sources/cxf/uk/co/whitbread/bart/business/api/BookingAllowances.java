
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingAllowances complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingAllowances"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="extrasCodes" type="{http://corporate.micros.com/1.0}ArrayOfextrasCodesItemString" minOccurs="0"/&gt;
 *         &lt;element name="maxDinnerBudgetUKWide" type="{http://corporate.micros.com/1.0}Price" minOccurs="0"/&gt;
 *         &lt;element name="maxDinnerBudgetGreaterLondon" type="{http://corporate.micros.com/1.0}Price" minOccurs="0"/&gt;
 *         &lt;element name="maxDinnerBudgetIreland" type="{http://corporate.micros.com/1.0}Price" minOccurs="0"/&gt;
 *         &lt;element name="allowAlcohol" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="allowCarParking" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="allowAdditionalCosts" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="allowPremierSaverRates" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="individualCards" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingAllowances", propOrder = {
    "extrasCodes",
    "maxDinnerBudgetUKWide",
    "maxDinnerBudgetGreaterLondon",
    "maxDinnerBudgetIreland",
    "allowAlcohol",
    "allowCarParking",
    "allowAdditionalCosts",
    "allowPremierSaverRates",
    "individualCards"
})
public class BookingAllowances
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected ArrayOfextrasCodesItemString extrasCodes;
    protected Price maxDinnerBudgetUKWide;
    protected Price maxDinnerBudgetGreaterLondon;
    protected Price maxDinnerBudgetIreland;
    protected Boolean allowAlcohol;
    protected Boolean allowCarParking;
    protected Boolean allowAdditionalCosts;
    protected Boolean allowPremierSaverRates;
    protected boolean individualCards;

    /**
     * Gets the value of the extrasCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfextrasCodesItemString }
     *     
     */
    public ArrayOfextrasCodesItemString getExtrasCodes() {
        return extrasCodes;
    }

    /**
     * Sets the value of the extrasCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfextrasCodesItemString }
     *     
     */
    public void setExtrasCodes(ArrayOfextrasCodesItemString value) {
        this.extrasCodes = value;
    }

    /**
     * Gets the value of the maxDinnerBudgetUKWide property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getMaxDinnerBudgetUKWide() {
        return maxDinnerBudgetUKWide;
    }

    /**
     * Sets the value of the maxDinnerBudgetUKWide property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setMaxDinnerBudgetUKWide(Price value) {
        this.maxDinnerBudgetUKWide = value;
    }

    /**
     * Gets the value of the maxDinnerBudgetGreaterLondon property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getMaxDinnerBudgetGreaterLondon() {
        return maxDinnerBudgetGreaterLondon;
    }

    /**
     * Sets the value of the maxDinnerBudgetGreaterLondon property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setMaxDinnerBudgetGreaterLondon(Price value) {
        this.maxDinnerBudgetGreaterLondon = value;
    }

    /**
     * Gets the value of the maxDinnerBudgetIreland property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getMaxDinnerBudgetIreland() {
        return maxDinnerBudgetIreland;
    }

    /**
     * Sets the value of the maxDinnerBudgetIreland property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setMaxDinnerBudgetIreland(Price value) {
        this.maxDinnerBudgetIreland = value;
    }

    /**
     * Gets the value of the allowAlcohol property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowAlcohol() {
        return allowAlcohol;
    }

    /**
     * Sets the value of the allowAlcohol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowAlcohol(Boolean value) {
        this.allowAlcohol = value;
    }

    /**
     * Gets the value of the allowCarParking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowCarParking() {
        return allowCarParking;
    }

    /**
     * Sets the value of the allowCarParking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowCarParking(Boolean value) {
        this.allowCarParking = value;
    }

    /**
     * Gets the value of the allowAdditionalCosts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowAdditionalCosts() {
        return allowAdditionalCosts;
    }

    /**
     * Sets the value of the allowAdditionalCosts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowAdditionalCosts(Boolean value) {
        this.allowAdditionalCosts = value;
    }

    /**
     * Gets the value of the allowPremierSaverRates property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowPremierSaverRates() {
        return allowPremierSaverRates;
    }

    /**
     * Sets the value of the allowPremierSaverRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowPremierSaverRates(Boolean value) {
        this.allowPremierSaverRates = value;
    }

    /**
     * Gets the value of the individualCards property.
     * 
     */
    public boolean isIndividualCards() {
        return individualCards;
    }

    /**
     * Sets the value of the individualCards property.
     * 
     */
    public void setIndividualCards(boolean value) {
        this.individualCards = value;
    }

}
