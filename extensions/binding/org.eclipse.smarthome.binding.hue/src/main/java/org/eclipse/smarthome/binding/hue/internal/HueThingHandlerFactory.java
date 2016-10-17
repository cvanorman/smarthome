/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.binding.hue.handler.HueBridgeHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * {@link HueThingHandlerFactory} is a factory for {@link HueBridgeHandler}s.
 *
 * @author Dennis Nobel - Initial contribution of hue binding
 * @author Kai Kreuzer - added supportsThingType method
 * @author Andre Fuechsel - implemented to use one discovery service per bridge
 *
 */
public class HueThingHandlerFactory extends BaseThingHandlerFactory {

    // private Logger logger = LoggerFactory.getLogger(HueThingHandlerFactory.class);

    private List<HueHandlerFactory> handlerFactories = new ArrayList<>();

    public HueThingHandlerFactory() {

        handlerFactories.add(new HueBridgeHandlerFactory());
        handlerFactories.add(new HueLightHandlerFactory());
        handlerFactories.add(new HueGroupHandlerFactory());
    }

    @Override
    public Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID,
            ThingUID bridgeUID) {

        if (thingUID == null) {
            String bridgeId = bridgeUID == null ? null : bridgeUID.getId();
            for (HueHandlerFactory factory : handlerFactories) {
                if (factory.supports(thingTypeUID)) {
                    thingUID = factory.createThingUID(thingTypeUID, configuration, bridgeId);
                }
            }
        }

        return super.createThing(thingTypeUID, configuration, thingUID, bridgeUID);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        for (HueHandlerFactory factory : handlerFactories) {
            if (factory.supports(thingTypeUID)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        for (HueHandlerFactory factory : handlerFactories) {
            if (factory.supports(thing.getThingTypeUID())) {
                return factory.createHandler(thing);
            }
        }

        return null;
    }
}
