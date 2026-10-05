
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ExtractParameterDescription complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExtractParameterDescription"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ParameterDescription"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DataType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractParameterDataType"/&gt;
 *         &lt;element name="DefaultValue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="IsOptional" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="DropDownList" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtractParameterDescription", propOrder = {
    "name",
    "dataType",
    "defaultValue",
    "isOptional",
    "dropDownList"
})
public class ExtractParameterDescription
    extends ParameterDescription
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "DataType", required = true)
    @XmlSchemaType(name = "string")
    protected ExtractParameterDataType dataType;
    @XmlElement(name = "DefaultValue", required = true)
    protected String defaultValue;
    @XmlElement(name = "IsOptional")
    protected boolean isOptional;
    @XmlElement(name = "DropDownList", required = true)
    protected String dropDownList;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractParameterDataType }
     *     
     */
    public ExtractParameterDataType getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractParameterDataType }
     *     
     */
    public void setDataType(ExtractParameterDataType value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the isOptional property.
     * 
     */
    public boolean isIsOptional() {
        return isOptional;
    }

    /**
     * Sets the value of the isOptional property.
     * 
     */
    public void setIsOptional(boolean value) {
        this.isOptional = value;
    }

    /**
     * Gets the value of the dropDownList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDropDownList() {
        return dropDownList;
    }

    /**
     * Sets the value of the dropDownList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDropDownList(String value) {
        this.dropDownList = value;
    }

}
