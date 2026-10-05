
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for AccountTypeEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="AccountTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="EXACTTARGET"/&gt;
 *     &lt;enumeration value="PRO_CONNECT"/&gt;
 *     &lt;enumeration value="CHANNEL_CONNECT"/&gt;
 *     &lt;enumeration value="CONNECT"/&gt;
 *     &lt;enumeration value="PRO_CONNECT_CLIENT"/&gt;
 *     &lt;enumeration value="LP_MEMBER"/&gt;
 *     &lt;enumeration value="DOTO_MEMBER"/&gt;
 *     &lt;enumeration value="ENTERPRISE_2"/&gt;
 *     &lt;enumeration value="BUSINESS_UNIT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "AccountTypeEnum")
@XmlEnum
public enum AccountTypeEnum {

    @XmlEnumValue("None")
    NONE("None"),
    EXACTTARGET("EXACTTARGET"),
    PRO_CONNECT("PRO_CONNECT"),
    CHANNEL_CONNECT("CHANNEL_CONNECT"),
    CONNECT("CONNECT"),
    PRO_CONNECT_CLIENT("PRO_CONNECT_CLIENT"),
    LP_MEMBER("LP_MEMBER"),
    DOTO_MEMBER("DOTO_MEMBER"),
    ENTERPRISE_2("ENTERPRISE_2"),
    BUSINESS_UNIT("BUSINESS_UNIT");
    private final String value;

    AccountTypeEnum(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static AccountTypeEnum fromValue(String v) {
        for (AccountTypeEnum c: AccountTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
