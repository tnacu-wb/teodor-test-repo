
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NewPremierGuestCCC complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="NewPremierGuestCCC">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="contentPremierGuestCCC" type="{https://dto.email.transact.comms.int.wtbapi.com}ContentPremierGuestCCC" minOccurs="0"/>
 *         <element name="login" type="{https://dto.email.transact.comms.int.wtbapi.com}Login" minOccurs="0"/>
 *         <element name="promotionalImages" type="{https://dto.email.transact.comms.int.wtbapi.com}PromotionalImages" minOccurs="0"/>
 *         <element name="template" type="{https://dto.email.transact.comms.int.wtbapi.com}Template" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewPremierGuestCCC", propOrder = {
    "contentPremierGuestCCC",
    "login",
    "promotionalImages",
    "template"
})
public class NewPremierGuestCCC
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected ContentPremierGuestCCC contentPremierGuestCCC;
    @XmlElement(nillable = true)
    protected Login login;
    @XmlElement(nillable = true)
    protected PromotionalImages promotionalImages;
    @XmlElement(nillable = true)
    protected Template template;

    /**
     * Gets the value of the contentPremierGuestCCC property.
     * 
     * @return
     *     possible object is
     *     {@link ContentPremierGuestCCC }
     *     
     */
    public ContentPremierGuestCCC getContentPremierGuestCCC() {
        return contentPremierGuestCCC;
    }

    /**
     * Sets the value of the contentPremierGuestCCC property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentPremierGuestCCC }
     *     
     */
    public void setContentPremierGuestCCC(ContentPremierGuestCCC value) {
        this.contentPremierGuestCCC = value;
    }

    /**
     * Gets the value of the login property.
     * 
     * @return
     *     possible object is
     *     {@link Login }
     *     
     */
    public Login getLogin() {
        return login;
    }

    /**
     * Sets the value of the login property.
     * 
     * @param value
     *     allowed object is
     *     {@link Login }
     *     
     */
    public void setLogin(Login value) {
        this.login = value;
    }

    /**
     * Gets the value of the promotionalImages property.
     * 
     * @return
     *     possible object is
     *     {@link PromotionalImages }
     *     
     */
    public PromotionalImages getPromotionalImages() {
        return promotionalImages;
    }

    /**
     * Sets the value of the promotionalImages property.
     * 
     * @param value
     *     allowed object is
     *     {@link PromotionalImages }
     *     
     */
    public void setPromotionalImages(PromotionalImages value) {
        this.promotionalImages = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Template }
     *     
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Template }
     *     
     */
    public void setTemplate(Template value) {
        this.template = value;
    }

}
