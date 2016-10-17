package org.eclipse.smarthome.binding.hue.internal;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.handler.HueBridgeHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

public class HueBridgeHandlerFactory implements HueHandlerFactory {

    @Override
    public boolean supports(ThingTypeUID thingTypeUID) {
        return HueBindingConstants.THING_TYPE_BRIDGE.equals(thingTypeUID);
    }

    @Override
    public ThingHandler createHandler(Thing thing) {
        return new HueBridgeHandler((Bridge) thing);
    }

    @Override
    public ThingUID createThingUID(ThingTypeUID thingTypeUID, Configuration configuration, String bridgeId) {
        return new ThingUID(thingTypeUID, (String) configuration.get(HueBindingConstants.SERIAL_NUMBER));
    }

}
