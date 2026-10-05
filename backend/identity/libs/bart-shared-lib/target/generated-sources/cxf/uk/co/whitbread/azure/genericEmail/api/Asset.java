
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
 * &lt;p&gt;Java class for Asset complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Asset"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ContentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Locked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ActiveDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ExpirationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="MemberId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="EnterpriseId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="CreatedBy" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UserBasicsEntity" minOccurs="0"/&gt;
 *         &lt;element name="ModifiedBy" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UserBasicsEntity" minOccurs="0"/&gt;
 *         &lt;element name="Content" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Design" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SuperContent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MinBlocks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="MaxBlocks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="File" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AssetType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}NameIdReference" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}NameIdReference" minOccurs="0"/&gt;
 *         &lt;element name="Thumbnail" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Thumbnail" minOccurs="0"/&gt;
 *         &lt;element name="GenerateFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Template" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Asset" minOccurs="0"/&gt;
 *         &lt;element name="Category" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}CategoryNameIdReference" minOccurs="0"/&gt;
 *         &lt;element name="Data" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="FileProperties" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="Meta" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="CustomFields" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="SharingProperties" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="Views" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="Blocks" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="Slots" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="Channels" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AssetAnyProperty" minOccurs="0"/&gt;
 *         &lt;element name="AllowedBlocks" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Block" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Tags" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Tag" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Attributes" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Attribute" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AttributeEntityV1" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "Asset", propOrder = {
    "contentType",
    "version",
    "locked",
    "name",
    "description",
    "activeDate",
    "expirationDate",
    "memberId",
    "enterpriseId",
    "createdBy",
    "modifiedBy",
    "content",
    "design",
    "superContent",
    "minBlocks",
    "maxBlocks",
    "file",
    "assetType",
    "status",
    "thumbnail",
    "generateFrom",
    "template",
    "category",
    "data",
    "fileProperties",
    "meta",
    "customFields",
    "sharingProperties",
    "views",
    "blocks",
    "slots",
    "channels",
    "allowedBlocks",
    "tags",
    "attributes"
})
public class Asset
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ContentType")
    protected String contentType;
    @XmlElement(name = "Version", nillable = true)
    protected Integer version;
    @XmlElement(name = "Locked", nillable = true)
    protected Boolean locked;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "ActiveDate", type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime activeDate;
    @XmlElement(name = "ExpirationDate", type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime expirationDate;
    @XmlElement(name = "MemberId", nillable = true)
    protected Long memberId;
    @XmlElement(name = "EnterpriseId", nillable = true)
    protected Long enterpriseId;
    @XmlElement(name = "CreatedBy")
    protected UserBasicsEntity createdBy;
    @XmlElement(name = "ModifiedBy")
    protected UserBasicsEntity modifiedBy;
    @XmlElement(name = "Content")
    protected String content;
    @XmlElement(name = "Design")
    protected String design;
    @XmlElement(name = "SuperContent")
    protected String superContent;
    @XmlElement(name = "MinBlocks", nillable = true)
    protected Integer minBlocks;
    @XmlElement(name = "MaxBlocks", nillable = true)
    protected Integer maxBlocks;
    @XmlElement(name = "File")
    protected String file;
    @XmlElement(name = "AssetType")
    protected NameIdReference assetType;
    @XmlElement(name = "Status")
    protected NameIdReference status;
    @XmlElement(name = "Thumbnail")
    protected Thumbnail thumbnail;
    @XmlElement(name = "GenerateFrom")
    protected String generateFrom;
    @XmlElement(name = "Template")
    protected Asset template;
    @XmlElement(name = "Category")
    protected CategoryNameIdReference category;
    @XmlElement(name = "Data")
    protected AssetAnyProperty data;
    @XmlElement(name = "FileProperties")
    protected AssetAnyProperty fileProperties;
    @XmlElement(name = "Meta")
    protected AssetAnyProperty meta;
    @XmlElement(name = "CustomFields")
    protected AssetAnyProperty customFields;
    @XmlElement(name = "SharingProperties")
    protected AssetAnyProperty sharingProperties;
    @XmlElement(name = "Views")
    protected AssetAnyProperty views;
    @XmlElement(name = "Blocks")
    protected AssetAnyProperty blocks;
    @XmlElement(name = "Slots")
    protected AssetAnyProperty slots;
    @XmlElement(name = "Channels")
    protected AssetAnyProperty channels;
    @XmlElement(name = "AllowedBlocks")
    protected Asset.AllowedBlocks allowedBlocks;
    @XmlElement(name = "Tags")
    protected Asset.Tags tags;
    @XmlElement(name = "Attributes")
    protected Asset.Attributes attributes;

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersion(Integer value) {
        this.version = value;
    }

    /**
     * Gets the value of the locked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLocked(Boolean value) {
        this.locked = value;
    }

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the activeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getActiveDate() {
        return activeDate;
    }

    /**
     * Sets the value of the activeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActiveDate(LocalDateTime value) {
        this.activeDate = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationDate(LocalDateTime value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the memberId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMemberId() {
        return memberId;
    }

    /**
     * Sets the value of the memberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMemberId(Long value) {
        this.memberId = value;
    }

    /**
     * Gets the value of the enterpriseId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEnterpriseId() {
        return enterpriseId;
    }

    /**
     * Sets the value of the enterpriseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEnterpriseId(Long value) {
        this.enterpriseId = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link UserBasicsEntity }
     *     
     */
    public UserBasicsEntity getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserBasicsEntity }
     *     
     */
    public void setCreatedBy(UserBasicsEntity value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the modifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link UserBasicsEntity }
     *     
     */
    public UserBasicsEntity getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the value of the modifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserBasicsEntity }
     *     
     */
    public void setModifiedBy(UserBasicsEntity value) {
        this.modifiedBy = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the design property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesign() {
        return design;
    }

    /**
     * Sets the value of the design property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesign(String value) {
        this.design = value;
    }

    /**
     * Gets the value of the superContent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperContent() {
        return superContent;
    }

    /**
     * Sets the value of the superContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperContent(String value) {
        this.superContent = value;
    }

    /**
     * Gets the value of the minBlocks property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinBlocks() {
        return minBlocks;
    }

    /**
     * Sets the value of the minBlocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinBlocks(Integer value) {
        this.minBlocks = value;
    }

    /**
     * Gets the value of the maxBlocks property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxBlocks() {
        return maxBlocks;
    }

    /**
     * Sets the value of the maxBlocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxBlocks(Integer value) {
        this.maxBlocks = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile(String value) {
        this.file = value;
    }

    /**
     * Gets the value of the assetType property.
     * 
     * @return
     *     possible object is
     *     {@link NameIdReference }
     *     
     */
    public NameIdReference getAssetType() {
        return assetType;
    }

    /**
     * Sets the value of the assetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameIdReference }
     *     
     */
    public void setAssetType(NameIdReference value) {
        this.assetType = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link NameIdReference }
     *     
     */
    public NameIdReference getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameIdReference }
     *     
     */
    public void setStatus(NameIdReference value) {
        this.status = value;
    }

    /**
     * Gets the value of the thumbnail property.
     * 
     * @return
     *     possible object is
     *     {@link Thumbnail }
     *     
     */
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the value of the thumbnail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Thumbnail }
     *     
     */
    public void setThumbnail(Thumbnail value) {
        this.thumbnail = value;
    }

    /**
     * Gets the value of the generateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerateFrom() {
        return generateFrom;
    }

    /**
     * Sets the value of the generateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerateFrom(String value) {
        this.generateFrom = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Asset }
     *     
     */
    public Asset getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset }
     *     
     */
    public void setTemplate(Asset value) {
        this.template = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryNameIdReference }
     *     
     */
    public CategoryNameIdReference getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryNameIdReference }
     *     
     */
    public void setCategory(CategoryNameIdReference value) {
        this.category = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setData(AssetAnyProperty value) {
        this.data = value;
    }

    /**
     * Gets the value of the fileProperties property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getFileProperties() {
        return fileProperties;
    }

    /**
     * Sets the value of the fileProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setFileProperties(AssetAnyProperty value) {
        this.fileProperties = value;
    }

    /**
     * Gets the value of the meta property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getMeta() {
        return meta;
    }

    /**
     * Sets the value of the meta property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setMeta(AssetAnyProperty value) {
        this.meta = value;
    }

    /**
     * Gets the value of the customFields property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getCustomFields() {
        return customFields;
    }

    /**
     * Sets the value of the customFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setCustomFields(AssetAnyProperty value) {
        this.customFields = value;
    }

    /**
     * Gets the value of the sharingProperties property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getSharingProperties() {
        return sharingProperties;
    }

    /**
     * Sets the value of the sharingProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setSharingProperties(AssetAnyProperty value) {
        this.sharingProperties = value;
    }

    /**
     * Gets the value of the views property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getViews() {
        return views;
    }

    /**
     * Sets the value of the views property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setViews(AssetAnyProperty value) {
        this.views = value;
    }

    /**
     * Gets the value of the blocks property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getBlocks() {
        return blocks;
    }

    /**
     * Sets the value of the blocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setBlocks(AssetAnyProperty value) {
        this.blocks = value;
    }

    /**
     * Gets the value of the slots property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getSlots() {
        return slots;
    }

    /**
     * Sets the value of the slots property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setSlots(AssetAnyProperty value) {
        this.slots = value;
    }

    /**
     * Gets the value of the channels property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAnyProperty }
     *     
     */
    public AssetAnyProperty getChannels() {
        return channels;
    }

    /**
     * Sets the value of the channels property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAnyProperty }
     *     
     */
    public void setChannels(AssetAnyProperty value) {
        this.channels = value;
    }

    /**
     * Gets the value of the allowedBlocks property.
     * 
     * @return
     *     possible object is
     *     {@link Asset.AllowedBlocks }
     *     
     */
    public Asset.AllowedBlocks getAllowedBlocks() {
        return allowedBlocks;
    }

    /**
     * Sets the value of the allowedBlocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset.AllowedBlocks }
     *     
     */
    public void setAllowedBlocks(Asset.AllowedBlocks value) {
        this.allowedBlocks = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link Asset.Tags }
     *     
     */
    public Asset.Tags getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset.Tags }
     *     
     */
    public void setTags(Asset.Tags value) {
        this.tags = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link Asset.Attributes }
     *     
     */
    public Asset.Attributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset.Attributes }
     *     
     */
    public void setAttributes(Asset.Attributes value) {
        this.attributes = value;
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
     *         &lt;element name="Block" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "block"
    })
    public static class AllowedBlocks
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Block")
        protected List<String> block;

        /**
         * Gets the value of the block property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the block property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getBlock().add(newItem);
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
         *     The value of the block property.
         */
        public List<String> getBlock() {
            if (block == null) {
                block = new ArrayList<>();
            }
            return this.block;
        }

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
     *         &lt;element name="Attribute" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AttributeEntityV1" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "attribute"
    })
    public static class Attributes
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Attribute")
        protected List<AttributeEntityV1> attribute;

        /**
         * Gets the value of the attribute property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the attribute property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getAttribute().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AttributeEntityV1 }
         * </p>
         * 
         * 
         * @return
         *     The value of the attribute property.
         */
        public List<AttributeEntityV1> getAttribute() {
            if (attribute == null) {
                attribute = new ArrayList<>();
            }
            return this.attribute;
        }

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
     *         &lt;element name="Tag" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "tag"
    })
    public static class Tags
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Tag")
        protected List<String> tag;

        /**
         * Gets the value of the tag property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the tag property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getTag().add(newItem);
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
         *     The value of the tag property.
         */
        public List<String> getTag() {
            if (tag == null) {
                tag = new ArrayList<>();
            }
            return this.tag;
        }

    }

}
