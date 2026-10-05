
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CompanyMIDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CompanyMIDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="purchaseOrderMIQuestion" type="{http://corporate.micros.com/1.0}MIQuestion"/&gt;
 *         &lt;element name="customerReferenceMIQuestion" type="{http://corporate.micros.com/1.0}MIQuestion"/&gt;
 *         &lt;element name="userDefinedMIQuestion" type="{http://corporate.micros.com/1.0}ArrayOfMIQuestionMIQuestion"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanyMIDetails", propOrder = {
    "purchaseOrderMIQuestion",
    "customerReferenceMIQuestion",
    "userDefinedMIQuestion"
})
public class CompanyMIDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected MIQuestion purchaseOrderMIQuestion;
    @XmlElement(required = true)
    protected MIQuestion customerReferenceMIQuestion;
    @XmlElement(required = true)
    protected ArrayOfMIQuestionMIQuestion userDefinedMIQuestion;

    /**
     * Gets the value of the purchaseOrderMIQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link MIQuestion }
     *     
     */
    public MIQuestion getPurchaseOrderMIQuestion() {
        return purchaseOrderMIQuestion;
    }

    /**
     * Sets the value of the purchaseOrderMIQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link MIQuestion }
     *     
     */
    public void setPurchaseOrderMIQuestion(MIQuestion value) {
        this.purchaseOrderMIQuestion = value;
    }

    /**
     * Gets the value of the customerReferenceMIQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link MIQuestion }
     *     
     */
    public MIQuestion getCustomerReferenceMIQuestion() {
        return customerReferenceMIQuestion;
    }

    /**
     * Sets the value of the customerReferenceMIQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link MIQuestion }
     *     
     */
    public void setCustomerReferenceMIQuestion(MIQuestion value) {
        this.customerReferenceMIQuestion = value;
    }

    /**
     * Gets the value of the userDefinedMIQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMIQuestionMIQuestion }
     *     
     */
    public ArrayOfMIQuestionMIQuestion getUserDefinedMIQuestion() {
        return userDefinedMIQuestion;
    }

    /**
     * Sets the value of the userDefinedMIQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMIQuestionMIQuestion }
     *     
     */
    public void setUserDefinedMIQuestion(ArrayOfMIQuestionMIQuestion value) {
        this.userDefinedMIQuestion = value;
    }

}
