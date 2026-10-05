
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CBTDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CBTDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="purchaseOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cbtAnswers" type="{http://bartws.micros.com/1.31}ArrayOfcbtAnswerCBTAnswer" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CBTDetails", propOrder = {
    "purchaseOrder",
    "customerReference",
    "cbtAnswers"
})
public class CBTDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String purchaseOrder;
    protected String customerReference;
    protected ArrayOfcbtAnswerCBTAnswer cbtAnswers;

    /**
     * Gets the value of the purchaseOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the value of the purchaseOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseOrder(String value) {
        this.purchaseOrder = value;
    }

    /**
     * Gets the value of the customerReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerReference() {
        return customerReference;
    }

    /**
     * Sets the value of the customerReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerReference(String value) {
        this.customerReference = value;
    }

    /**
     * Gets the value of the cbtAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfcbtAnswerCBTAnswer }
     *     
     */
    public ArrayOfcbtAnswerCBTAnswer getCbtAnswers() {
        return cbtAnswers;
    }

    /**
     * Sets the value of the cbtAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfcbtAnswerCBTAnswer }
     *     
     */
    public void setCbtAnswers(ArrayOfcbtAnswerCBTAnswer value) {
        this.cbtAnswers = value;
    }

}
