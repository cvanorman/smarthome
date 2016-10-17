package org.eclipse.smarthome.binding.hue.internal;

import java.util.Map;

public interface HueItem {

    String getId();

    String getName();

    String getTypeId();

    Map<String, String> getProperties();

}
