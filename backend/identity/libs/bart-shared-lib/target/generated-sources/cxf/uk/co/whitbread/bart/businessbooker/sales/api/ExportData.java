
package uk.co.whitbread.bart.businessbooker.sales.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ExportData complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExportData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="records" type="{http://corporate.micros.com/1.0}ArrayOfrecordsItemString" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportData", propOrder = {
    "records"
})
public class ExportData
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected ArrayOfrecordsItemString records;

    /**
     * Gets the value of the records property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfrecordsItemString }
     *     
     */
    public ArrayOfrecordsItemString getRecords() {
        return records;
    }

    /**
     * Sets the value of the records property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfrecordsItemString }
     *     
     */
    public void setRecords(ArrayOfrecordsItemString value) {
        this.records = value;
    }

}
