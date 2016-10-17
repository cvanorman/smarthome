package org.eclipse.smarthome.binding.hue.internal;

import java.util.Map;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

public interface HueItem {

    String getId();

    String getName();

    ThingTypeUID getThingTypeUID();

    Map<String, String> getProperties();

}
