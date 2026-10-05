
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for SimpleFilterPart complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SimpleFilterPart"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Property" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SimpleOperator" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SimpleOperators"/&gt;
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DateValue" type="{http://www.w3.org/2001/XMLSchema}dateTime" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleFilterPart", propOrder = {
    "property",
    "simpleOperator",
    "value",
    "dateValue"
})
public class SimpleFilterPart
    extends FilterPart
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Property", required = true)
    protected String property;
    @XmlElement(name = "SimpleOperator", required = true)
    @XmlSchemaType(name = "string")
    protected SimpleOperators simpleOperator;
    @XmlElement(name = "Value")
    protected List<String> value;
    @XmlElement(name = "DateValue", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected List<LocalDateTime> dateValue;

    /**
     * Gets the value of the property property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the value of the property property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperty(String value) {
        this.property = value;
    }

    /**
     * Gets the value of the simpleOperator property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleOperators }
     *     
     */
    public SimpleOperators getSimpleOperator() {
        return simpleOperator;
    }

    /**
     * Sets the value of the simpleOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleOperators }
     *     
     */
    public void setSimpleOperator(SimpleOperators value) {
        this.simpleOperator = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the value property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the value property.
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return this.value;
    }

    /**
     * Gets the value of the dateValue property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the dateValue property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDateValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the dateValue property.
     */
    public List<LocalDateTime> getDateValue() {
        if (dateValue == null) {
            dateValue = new ArrayList<>();
        }
        return this.dateValue;
    }

}
