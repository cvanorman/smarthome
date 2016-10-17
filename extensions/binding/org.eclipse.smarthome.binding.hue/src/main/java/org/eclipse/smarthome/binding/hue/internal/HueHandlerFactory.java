package org.eclipse.smarthome.binding.hue.internal;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

public interface HueHandlerFactory {

    boolean supports(ThingTypeUID thingTypeUID);

    ThingHandler createHandler(Thing thing);

    ThingUID createThingUID(ThingTypeUID thingTypeUID, Configuration configuration, String bridgeId);
}
