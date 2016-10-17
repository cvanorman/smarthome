package org.eclipse.smarthome.binding.hue.internal;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.handler.HueLightHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

import com.google.common.collect.Sets;

public class HueLightHandlerFactory implements HueHandlerFactory {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_COLOR_LIGHT,
            THING_TYPE_COLOR_TEMPERATURE_LIGHT, THING_TYPE_DIMMABLE_LIGHT, THING_TYPE_EXTENDED_COLOR_LIGHT,
            THING_TYPE_ON_OFF_LIGHT);

    @Override
    public boolean supports(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    public ThingHandler createHandler(Thing thing) {
        return new HueLightHandler(thing);
    }

    @Override
    public ThingUID createThingUID(ThingTypeUID thingTypeUID, Configuration configuration, String bridgeId) {
        return new ThingUID(thingTypeUID, (String) configuration.get(HueBindingConstants.LIGHT_ID), bridgeId);
    }

}
