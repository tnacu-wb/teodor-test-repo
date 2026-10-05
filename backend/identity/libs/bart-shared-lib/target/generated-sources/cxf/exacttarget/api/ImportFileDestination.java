
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ImportFileDestination complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ImportFileDestination"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TemplateCustomObject" type="{http://exacttarget.com/wsdl/partnerAPI}DataExtension" minOccurs="0"/&gt;
 *         &lt;element name="FileTransferLocation" type="{http://exacttarget.com/wsdl/partnerAPI}FileTransferLocation" minOccurs="0"/&gt;
 *         &lt;element name="FileSpec" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EncodingCodePage" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="HasColumnHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="FieldDelimiter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RowDelimiter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NullValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BooleanFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateTimeFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StringIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EscapeSequence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImportFileDestination", propOrder = {
    "templateCustomObject",
    "fileTransferLocation",
    "fileSpec",
    "encodingCodePage",
    "hasColumnHeader",
    "fieldDelimiter",
    "rowDelimiter",
    "nullValue",
    "booleanFormat",
    "dateTimeFormat",
    "stringIdentifier",
    "escapeSequence"
})
public class ImportFileDestination
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "TemplateCustomObject")
    protected DataExtension templateCustomObject;
    @XmlElement(name = "FileTransferLocation")
    protected FileTransferLocation fileTransferLocation;
    @XmlElement(name = "FileSpec")
    protected String fileSpec;
    @XmlElement(name = "EncodingCodePage")
    protected Integer encodingCodePage;
    @XmlElement(name = "HasColumnHeader")
    protected Boolean hasColumnHeader;
    @XmlElement(name = "FieldDelimiter")
    protected String fieldDelimiter;
    @XmlElement(name = "RowDelimiter")
    protected String rowDelimiter;
    @XmlElement(name = "NullValue")
    protected String nullValue;
    @XmlElement(name = "BooleanFormat")
    protected String booleanFormat;
    @XmlElement(name = "DateTimeFormat")
    protected String dateTimeFormat;
    @XmlElement(name = "StringIdentifier")
    protected String stringIdentifier;
    @XmlElement(name = "EscapeSequence")
    protected String escapeSequence;

    /**
     * Gets the value of the templateCustomObject property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtension }
     *     
     */
    public DataExtension getTemplateCustomObject() {
        return templateCustomObject;
    }

    /**
     * Sets the value of the templateCustomObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtension }
     *     
     */
    public void setTemplateCustomObject(DataExtension value) {
        this.templateCustomObject = value;
    }

    /**
     * Gets the value of the fileTransferLocation property.
     * 
     * @return
     *     possible object is
     *     {@link FileTransferLocation }
     *     
     */
    public FileTransferLocation getFileTransferLocation() {
        return fileTransferLocation;
    }

    /**
     * Sets the value of the fileTransferLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileTransferLocation }
     *     
     */
    public void setFileTransferLocation(FileTransferLocation value) {
        this.fileTransferLocation = value;
    }

    /**
     * Gets the value of the fileSpec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileSpec() {
        return fileSpec;
    }

    /**
     * Sets the value of the fileSpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileSpec(String value) {
        this.fileSpec = value;
    }

    /**
     * Gets the value of the encodingCodePage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEncodingCodePage() {
        return encodingCodePage;
    }

    /**
     * Sets the value of the encodingCodePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEncodingCodePage(Integer value) {
        this.encodingCodePage = value;
    }

    /**
     * Gets the value of the hasColumnHeader property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasColumnHeader() {
        return hasColumnHeader;
    }

    /**
     * Sets the value of the hasColumnHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasColumnHeader(Boolean value) {
        this.hasColumnHeader = value;
    }

    /**
     * Gets the value of the fieldDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    /**
     * Sets the value of the fieldDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldDelimiter(String value) {
        this.fieldDelimiter = value;
    }

    /**
     * Gets the value of the rowDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowDelimiter() {
        return rowDelimiter;
    }

    /**
     * Sets the value of the rowDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowDelimiter(String value) {
        this.rowDelimiter = value;
    }

    /**
     * Gets the value of the nullValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNullValue() {
        return nullValue;
    }

    /**
     * Sets the value of the nullValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNullValue(String value) {
        this.nullValue = value;
    }

    /**
     * Gets the value of the booleanFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBooleanFormat() {
        return booleanFormat;
    }

    /**
     * Sets the value of the booleanFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBooleanFormat(String value) {
        this.booleanFormat = value;
    }

    /**
     * Gets the value of the dateTimeFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    /**
     * Sets the value of the dateTimeFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateTimeFormat(String value) {
        this.dateTimeFormat = value;
    }

    /**
     * Gets the value of the stringIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringIdentifier() {
        return stringIdentifier;
    }

    /**
     * Sets the value of the stringIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringIdentifier(String value) {
        this.stringIdentifier = value;
    }

    /**
     * Gets the value of the escapeSequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEscapeSequence() {
        return escapeSequence;
    }

    /**
     * Sets the value of the escapeSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEscapeSequence(String value) {
        this.escapeSequence = value;
    }

}
