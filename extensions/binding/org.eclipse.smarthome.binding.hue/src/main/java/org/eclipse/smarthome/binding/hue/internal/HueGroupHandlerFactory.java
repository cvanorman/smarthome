package org.eclipse.smarthome.binding.hue.internal;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.handler.HueGroupHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

public class HueGroupHandlerFactory implements HueHandlerFactory {

    @Override
    public boolean supports(ThingTypeUID thingTypeUID) {
        return HueBindingConstants.THING_TYPE_GROUP.equals(thingTypeUID);
    }

    @Override
    public ThingHandler createHandler(Thing thing) {
        return new HueGroupHandler(thing);
    }

    @Override
    public ThingUID createThingUID(ThingTypeUID thingTypeUID, Configuration configuration, String bridgeId) {
        return new ThingUID(thingTypeUID, (String) configuration.get(HueBindingConstants.LIGHT_ID), bridgeId);
    }

}
