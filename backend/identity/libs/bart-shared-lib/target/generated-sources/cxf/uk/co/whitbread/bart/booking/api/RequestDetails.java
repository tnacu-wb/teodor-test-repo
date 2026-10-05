
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for RequestDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RequestDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cbtCompanyId" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cbtEmployeeId" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cbtSalesManager" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="hotelCodes" type="{http://bartws.micros.com/1.31}ArrayOfhotelCodesItemString"/&gt;
 *         &lt;element name="arrivalDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="cellCodes" type="{http://bartws.micros.com/1.31}ArrayOfcellCodesItemString" minOccurs="0"/&gt;
 *         &lt;element name="noOfRooms" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="roomRequirements" type="{http://bartws.micros.com/1.31}ArrayOfroomRequirementRoomRequest" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestDetails", propOrder = {
    "confirmationNumber",
    "cbtCompanyId",
    "cbtEmployeeId",
    "cbtSalesManager",
    "hotelCodes",
    "arrivalDate",
    "departureDate",
    "cellCodes",
    "noOfRooms",
    "roomRequirements"
})
public class RequestDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String confirmationNumber;
    protected String cbtCompanyId;
    protected String cbtEmployeeId;
    protected String cbtSalesManager;
    @XmlElement(required = true)
    protected ArrayOfhotelCodesItemString hotelCodes;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate arrivalDate;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate departureDate;
    protected ArrayOfcellCodesItemString cellCodes;
    protected Long noOfRooms;
    protected ArrayOfroomRequirementRoomRequest roomRequirements;

    /**
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
    }

    /**
     * Gets the value of the cbtCompanyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtCompanyId() {
        return cbtCompanyId;
    }

    /**
     * Sets the value of the cbtCompanyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtCompanyId(String value) {
        this.cbtCompanyId = value;
    }

    /**
     * Gets the value of the cbtEmployeeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtEmployeeId() {
        return cbtEmployeeId;
    }

    /**
     * Sets the value of the cbtEmployeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtEmployeeId(String value) {
        this.cbtEmployeeId = value;
    }

    /**
     * Gets the value of the cbtSalesManager property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtSalesManager() {
        return cbtSalesManager;
    }

    /**
     * Sets the value of the cbtSalesManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtSalesManager(String value) {
        this.cbtSalesManager = value;
    }

    /**
     * Gets the value of the hotelCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfhotelCodesItemString }
     *     
     */
    public ArrayOfhotelCodesItemString getHotelCodes() {
        return hotelCodes;
    }

    /**
     * Sets the value of the hotelCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfhotelCodesItemString }
     *     
     */
    public void setHotelCodes(ArrayOfhotelCodesItemString value) {
        this.hotelCodes = value;
    }

    /**
     * Gets the value of the arrivalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    /**
     * Sets the value of the arrivalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalDate(LocalDate value) {
        this.arrivalDate = value;
    }

    /**
     * Gets the value of the departureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the value of the departureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDate(LocalDate value) {
        this.departureDate = value;
    }

    /**
     * Gets the value of the cellCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfcellCodesItemString }
     *     
     */
    public ArrayOfcellCodesItemString getCellCodes() {
        return cellCodes;
    }

    /**
     * Sets the value of the cellCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfcellCodesItemString }
     *     
     */
    public void setCellCodes(ArrayOfcellCodesItemString value) {
        this.cellCodes = value;
    }

    /**
     * Gets the value of the noOfRooms property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNoOfRooms() {
        return noOfRooms;
    }

    /**
     * Sets the value of the noOfRooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNoOfRooms(Long value) {
        this.noOfRooms = value;
    }

    /**
     * Gets the value of the roomRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfroomRequirementRoomRequest }
     *     
     */
    public ArrayOfroomRequirementRoomRequest getRoomRequirements() {
        return roomRequirements;
    }

    /**
     * Sets the value of the roomRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfroomRequirementRoomRequest }
     *     
     */
    public void setRoomRequirements(ArrayOfroomRequirementRoomRequest value) {
        this.roomRequirements = value;
    }

}
