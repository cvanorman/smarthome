package org.eclipse.smarthome.binding.hue.internal;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.LIGHT_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.philips.lighting.model.PHLight;

public class HueLight implements HueItem {

    // @formatter:off
    private final static Map<String, ThingTypeUID> TYPE_TO_ZIGBEE_ID_MAP = new ImmutableMap.Builder<String, ThingTypeUID>()
            .put("on_off_light", HueBindingConstants.THING_TYPE_ON_OFF_LIGHT)
            .put("dim_light", HueBindingConstants.THING_TYPE_DIMMABLE_LIGHT)
            .put("color_light",HueBindingConstants.THING_TYPE_COLOR_LIGHT)
            .put("ct_color_light", HueBindingConstants.THING_TYPE_EXTENDED_COLOR_LIGHT)
            .put("ct_light",HueBindingConstants.THING_TYPE_COLOR_TEMPERATURE_LIGHT)
            .put("dimmable_plug_in_unit", HueBindingConstants.THING_TYPE_DIMMABLE_LIGHT)
            .build();
    // @formatter:on

    private final static String OSRAM_PAR16_50_TW_MODEL_ID = "PAR16_50_TW";
    private static final String NORMALIZE_ID_REGEX = "[^a-zA-Z0-9_]";

    private PHLight fullLight;
    private Map<String, String> properties = new HashMap<>();
    private ThingTypeUID thingTypeId;
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
    public ThingTypeUID getThingTypeUID() {
        return thingTypeId;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean isOsramPar16() {
        return OSRAM_PAR16_50_TW_MODEL_ID.equals(modelId);
    }

    public static Set<ThingTypeUID> getSupportedThingTypes() {
        return new ImmutableSet.Builder<ThingTypeUID>().addAll(TYPE_TO_ZIGBEE_ID_MAP.values()).build();
    }
}
