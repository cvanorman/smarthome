package org.eclipse.smarthome.binding.hue.internal;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.LIGHT_ID;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.thing.Thing;

import com.google.common.collect.ImmutableMap;
import com.philips.lighting.model.PHLight;

public class HueLight implements HueItem {

    // @formatter:off
    private final static Map<String, String> TYPE_TO_ZIGBEE_ID_MAP = new ImmutableMap.Builder<String, String>()
            .put("on_off_light", "0000")
            .put("dim_light", "0100")
            .put("color_light", "0200")
            .put("ct_color_light", "0210")
            .put("ct_light", "0220")
            .put("dimmable_plug_in_unit", "0100")
            .build();
    // @formatter:on

    private final static String OSRAM_PAR16_50_TW_MODEL_ID = "PAR16_50_TW";
    private static final String NORMALIZE_ID_REGEX = "[^a-zA-Z0-9_]";

    private PHLight fullLight;
    private Map<String, String> properties = new HashMap<>();
    private String thingTypeId;
    private String modelId;

    public HueLight(PHLight fullLight) {
        super();
        this.fullLight = fullLight;
        thingTypeId = TYPE_TO_ZIGBEE_ID_MAP
                .get(fullLight.getLightType().name().replaceAll(NORMALIZE_ID_REGEX, "_").toLowerCase());

        modelId = fullLight.getModelNumber().replaceAll(NORMALIZE_ID_REGEX, "_");
        properties.put(LIGHT_ID, fullLight.getIdentifier());
        properties.put(Thing.PROPERTY_FIRMWARE_VERSION, fullLight.getVersionNumber());
        properties.put(Thing.PROPERTY_MODEL_ID, modelId);
        properties.put(Thing.PROPERTY_VENDOR, fullLight.getManufacturerName());
        properties.put(Thing.PROPERTY_SERIAL_NUMBER, fullLight.getUniqueId());
    }

    @Override
    public String getId() {
        return fullLight.getIdentifier();
    }

    @Override
    public String getName() {
        return fullLight.getName();
    }

    @Override
    public String getTypeId() {
        return thingTypeId;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean isOsramPar16() {
        return OSRAM_PAR16_50_TW_MODEL_ID.equals(modelId);
    }

}
