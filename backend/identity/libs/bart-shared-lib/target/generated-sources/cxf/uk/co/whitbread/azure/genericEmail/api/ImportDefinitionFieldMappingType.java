
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for ImportDefinitionFieldMappingType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="ImportDefinitionFieldMappingType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="InferFromColumnHeadings"/&gt;
 *     &lt;enumeration value="MapByOrdinal"/&gt;
 *     &lt;enumeration value="ManualMap"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "ImportDefinitionFieldMappingType")
@XmlEnum
public enum ImportDefinitionFieldMappingType {

    @XmlEnumValue("InferFromColumnHeadings")
    INFER_FROM_COLUMN_HEADINGS("InferFromColumnHeadings"),
    @XmlEnumValue("MapByOrdinal")
    MAP_BY_ORDINAL("MapByOrdinal"),
    @XmlEnumValue("ManualMap")
    MANUAL_MAP("ManualMap");
    private final String value;

    ImportDefinitionFieldMappingType(String v) {
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
    public static ImportDefinitionFieldMappingType fromValue(String v) {
        for (ImportDefinitionFieldMappingType c: ImportDefinitionFieldMappingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
