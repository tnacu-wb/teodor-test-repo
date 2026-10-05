
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Voucher complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="Voucher">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="totalVoucher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="voucherArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfVoucherDetails" minOccurs="0"/>
 *         <element name="voucherSum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Voucher", propOrder = {
    "totalVoucher",
    "voucherArray",
    "voucherSum"
})
public class Voucher
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String totalVoucher;
    @XmlElement(nillable = true)
    protected ArrayOfVoucherDetails voucherArray;
    @XmlElement(nillable = true)
    protected String voucherSum;

    /**
     * Gets the value of the totalVoucher property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalVoucher() {
        return totalVoucher;
    }

    /**
     * Sets the value of the totalVoucher property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalVoucher(String value) {
        this.totalVoucher = value;
    }

    /**
     * Gets the value of the voucherArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVoucherDetails }
     *     
     */
    public ArrayOfVoucherDetails getVoucherArray() {
        return voucherArray;
    }

    /**
     * Sets the value of the voucherArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVoucherDetails }
     *     
     */
    public void setVoucherArray(ArrayOfVoucherDetails value) {
        this.voucherArray = value;
    }

    /**
     * Gets the value of the voucherSum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoucherSum() {
        return voucherSum;
    }

    /**
     * Sets the value of the voucherSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherSum(String value) {
        this.voucherSum = value;
    }

}
