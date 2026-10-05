
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for WeekendRewardsWelcome complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="WeekendRewardsWelcome"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contentWeekendRewardsWelcome" type="{https://dto.email.transact.comms.int.wtbapi.com}ContentWeekendRewardsWelcome" minOccurs="0"/&gt;
 *         &lt;element name="login" type="{https://dto.email.transact.comms.int.wtbapi.com}Login" minOccurs="0"/&gt;
 *         &lt;element name="template" type="{https://dto.email.transact.comms.int.wtbapi.com}Template" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeekendRewardsWelcome", propOrder = {
    "contentWeekendRewardsWelcome",
    "login",
    "template"
})
public class WeekendRewardsWelcome
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected ContentWeekendRewardsWelcome contentWeekendRewardsWelcome;
    @XmlElement(nillable = true)
    protected Login login;
    @XmlElement(nillable = true)
    protected Template template;

    /**
     * Gets the value of the contentWeekendRewardsWelcome property.
     * 
     * @return
     *     possible object is
     *     {@link ContentWeekendRewardsWelcome }
     *     
     */
    public ContentWeekendRewardsWelcome getContentWeekendRewardsWelcome() {
        return contentWeekendRewardsWelcome;
    }

    /**
     * Sets the value of the contentWeekendRewardsWelcome property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentWeekendRewardsWelcome }
     *     
     */
    public void setContentWeekendRewardsWelcome(ContentWeekendRewardsWelcome value) {
        this.contentWeekendRewardsWelcome = value;
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
