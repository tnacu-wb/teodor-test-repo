
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for DataExtensionFieldType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="DataExtensionFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Text"/&gt;
 *     &lt;enumeration value="Number"/&gt;
 *     &lt;enumeration value="Date"/&gt;
 *     &lt;enumeration value="Boolean"/&gt;
 *     &lt;enumeration value="EmailAddress"/&gt;
 *     &lt;enumeration value="Phone"/&gt;
 *     &lt;enumeration value="Decimal"/&gt;
 *     &lt;enumeration value="Locale"/&gt;
 *     &lt;enumeration value="Base16Encrypted"/&gt;
 *     &lt;enumeration value="Base16EncryptedEmail"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "DataExtensionFieldType")
@XmlEnum
public enum DataExtensionFieldType {

    @XmlEnumValue("Text")
    TEXT("Text"),
    @XmlEnumValue("Number")
    NUMBER("Number"),
    @XmlEnumValue("Date")
    DATE("Date"),
    @XmlEnumValue("Boolean")
    BOOLEAN("Boolean"),
    @XmlEnumValue("EmailAddress")
    EMAIL_ADDRESS("EmailAddress"),
    @XmlEnumValue("Phone")
    PHONE("Phone"),
    @XmlEnumValue("Decimal")
    DECIMAL("Decimal"),
    @XmlEnumValue("Locale")
    LOCALE("Locale"),
    @XmlEnumValue("Base16Encrypted")
    BASE_16_ENCRYPTED("Base16Encrypted"),
    @XmlEnumValue("Base16EncryptedEmail")
    BASE_16_ENCRYPTED_EMAIL("Base16EncryptedEmail");
    private final String value;

    DataExtensionFieldType(String v) {
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
    public static DataExtensionFieldType fromValue(String v) {
        for (DataExtensionFieldType c: DataExtensionFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
