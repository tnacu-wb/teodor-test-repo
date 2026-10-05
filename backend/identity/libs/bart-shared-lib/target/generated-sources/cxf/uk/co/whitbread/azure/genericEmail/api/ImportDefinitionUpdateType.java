
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for ImportDefinitionUpdateType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="ImportDefinitionUpdateType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AddAndUpdate"/&gt;
 *     &lt;enumeration value="AddAndDoNotUpdate"/&gt;
 *     &lt;enumeration value="UpdateButDoNotAdd"/&gt;
 *     &lt;enumeration value="Merge"/&gt;
 *     &lt;enumeration value="Overwrite"/&gt;
 *     &lt;enumeration value="ColumnBased"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "ImportDefinitionUpdateType")
@XmlEnum
public enum ImportDefinitionUpdateType {

    @XmlEnumValue("AddAndUpdate")
    ADD_AND_UPDATE("AddAndUpdate"),
    @XmlEnumValue("AddAndDoNotUpdate")
    ADD_AND_DO_NOT_UPDATE("AddAndDoNotUpdate"),
    @XmlEnumValue("UpdateButDoNotAdd")
    UPDATE_BUT_DO_NOT_ADD("UpdateButDoNotAdd"),
    @XmlEnumValue("Merge")
    MERGE("Merge"),
    @XmlEnumValue("Overwrite")
    OVERWRITE("Overwrite"),
    @XmlEnumValue("ColumnBased")
    COLUMN_BASED("ColumnBased");
    private final String value;

    ImportDefinitionUpdateType(String v) {
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
    public static ImportDefinitionUpdateType fromValue(String v) {
        for (ImportDefinitionUpdateType c: ImportDefinitionUpdateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
