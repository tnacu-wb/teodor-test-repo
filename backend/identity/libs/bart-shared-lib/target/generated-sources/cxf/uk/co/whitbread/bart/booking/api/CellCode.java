
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CellCode complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CellCode"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cellCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cellCodeLegend" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cellCodeRateAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CellCode", propOrder = {
    "cellCode",
    "cellCodeLegend",
    "cellCodeRateAvailable"
})
public class CellCode
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String cellCode;
    protected String cellCodeLegend;
    protected Boolean cellCodeRateAvailable;

    /**
     * Gets the value of the cellCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellCode() {
        return cellCode;
    }

    /**
     * Sets the value of the cellCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellCode(String value) {
        this.cellCode = value;
    }

    /**
     * Gets the value of the cellCodeLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellCodeLegend() {
        return cellCodeLegend;
    }

    /**
     * Sets the value of the cellCodeLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellCodeLegend(String value) {
        this.cellCodeLegend = value;
    }

    /**
     * Gets the value of the cellCodeRateAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCellCodeRateAvailable() {
        return cellCodeRateAvailable;
    }

    /**
     * Sets the value of the cellCodeRateAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCellCodeRateAvailable(Boolean value) {
        this.cellCodeRateAvailable = value;
    }

}
