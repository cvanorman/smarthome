package org.eclipse.smarthome.binding.hue.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;
import com.philips.lighting.model.PHGroup;

public class HueGroup implements HueItem {

    private PHGroup fullGroup;
    private Map<String, String> properties = new HashMap<>();

    public HueGroup(PHGroup fullGroup) {
        super();
        this.fullGroup = fullGroup;

        properties.put(HueBindingConstants.LIGHT_ID, fullGroup.getIdentifier());
        properties.put(Thing.PROPERTY_MODEL_ID, fullGroup.getType());
    }

    @Override
    public String getId() {
        return fullGroup.getIdentifier();
    }

    @Override
    public String getName() {
        return fullGroup.getName();
    }

    @Override
    public ThingTypeUID getThingTypeUID() {
        return HueBindingConstants.THING_TYPE_GROUP;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public static Set<ThingTypeUID> getSupportedThingTypes() {
        return new ImmutableSet.Builder<ThingTypeUID>().add(HueBindingConstants.THING_TYPE_GROUP).build();
    }

}
