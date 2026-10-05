
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestaurantBooking complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="RestaurantBooking">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="brandCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dinnerArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfDinner" minOccurs="0"/>
 *         <element name="restaurantBrand" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="restaurantName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestaurantBooking", propOrder = {
    "brandCode",
    "dinnerArray",
    "restaurantBrand",
    "restaurantName"
})
public class RestaurantBooking
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String brandCode;
    @XmlElement(nillable = true)
    protected ArrayOfDinner dinnerArray;
    @XmlElement(nillable = true)
    protected String restaurantBrand;
    @XmlElement(nillable = true)
    protected String restaurantName;

    /**
     * Gets the value of the brandCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * Sets the value of the brandCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrandCode(String value) {
        this.brandCode = value;
    }

    /**
     * Gets the value of the dinnerArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDinner }
     *     
     */
    public ArrayOfDinner getDinnerArray() {
        return dinnerArray;
    }

    /**
     * Sets the value of the dinnerArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDinner }
     *     
     */
    public void setDinnerArray(ArrayOfDinner value) {
        this.dinnerArray = value;
    }

    /**
     * Gets the value of the restaurantBrand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestaurantBrand() {
        return restaurantBrand;
    }

    /**
     * Sets the value of the restaurantBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestaurantBrand(String value) {
        this.restaurantBrand = value;
    }

    /**
     * Gets the value of the restaurantName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Sets the value of the restaurantName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestaurantName(String value) {
        this.restaurantName = value;
    }

}
