
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ComplexFilterPart complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ComplexFilterPart"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LeftOperand" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart"/&gt;
 *         &lt;element name="LogicalOperator" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}LogicalOperators"/&gt;
 *         &lt;element name="RightOperand" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart" minOccurs="0"/&gt;
 *         &lt;element name="AdditionalOperands" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Operand" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexFilterPart", propOrder = {
    "leftOperand",
    "logicalOperator",
    "rightOperand",
    "additionalOperands"
})
public class ComplexFilterPart
    extends FilterPart
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "LeftOperand", required = true)
    protected FilterPart leftOperand;
    @XmlElement(name = "LogicalOperator", required = true)
    @XmlSchemaType(name = "string")
    protected LogicalOperators logicalOperator;
    @XmlElement(name = "RightOperand")
    protected FilterPart rightOperand;
    @XmlElement(name = "AdditionalOperands")
    protected ComplexFilterPart.AdditionalOperands additionalOperands;

    /**
     * Gets the value of the leftOperand property.
     * 
     * @return
     *     possible object is
     *     {@link FilterPart }
     *     
     */
    public FilterPart getLeftOperand() {
        return leftOperand;
    }

    /**
     * Sets the value of the leftOperand property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterPart }
     *     
     */
    public void setLeftOperand(FilterPart value) {
        this.leftOperand = value;
    }

    /**
     * Gets the value of the logicalOperator property.
     * 
     * @return
     *     possible object is
     *     {@link LogicalOperators }
     *     
     */
    public LogicalOperators getLogicalOperator() {
        return logicalOperator;
    }

    /**
     * Sets the value of the logicalOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link LogicalOperators }
     *     
     */
    public void setLogicalOperator(LogicalOperators value) {
        this.logicalOperator = value;
    }

    /**
     * Gets the value of the rightOperand property.
     * 
     * @return
     *     possible object is
     *     {@link FilterPart }
     *     
     */
    public FilterPart getRightOperand() {
        return rightOperand;
    }

    /**
     * Sets the value of the rightOperand property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterPart }
     *     
     */
    public void setRightOperand(FilterPart value) {
        this.rightOperand = value;
    }

    /**
     * Gets the value of the additionalOperands property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexFilterPart.AdditionalOperands }
     *     
     */
    public ComplexFilterPart.AdditionalOperands getAdditionalOperands() {
        return additionalOperands;
    }

    /**
     * Sets the value of the additionalOperands property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexFilterPart.AdditionalOperands }
     *     
     */
    public void setAdditionalOperands(ComplexFilterPart.AdditionalOperands value) {
        this.additionalOperands = value;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Operand" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "operand"
    })
    public static class AdditionalOperands
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Operand")
        protected List<FilterPart> operand;

        /**
         * Gets the value of the operand property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the operand property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getOperand().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FilterPart }
         * </p>
         * 
         * 
         * @return
         *     The value of the operand property.
         */
        public List<FilterPart> getOperand() {
            if (operand == null) {
                operand = new ArrayList<>();
            }
            return this.operand;
        }

    }

}
