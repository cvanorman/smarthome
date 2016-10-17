package org.eclipse.smarthome.binding.hue.internal;

import java.util.HashMap;
import java.util.Map;

import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

public class HueCache {

    private Map<String, PHLight> lightMap = new HashMap<>();
    private Map<String, PHGroup> groupMap = new HashMap<>();

    public HueCache(PHBridgeResourcesCache cache) {
        if (cache != null) {
            for (PHLight light : cache.getAllLights()) {
                lightMap.put(light.getIdentifier(), light);
            }

            for (PHGroup group : cache.getAllGroups()) {
                groupMap.put(group.getIdentifier(), group);
            }
        }
    }

    public PHLight getLight(String id) {
        return lightMap.get(id);
    }

    public LightState getLightState(String id) {
        LightState lightState = null;
        if (lightMap.containsKey(id)) {
            lightState = new LightState(lightMap.get(id).getLastKnownLightState());
        }

        return lightState;
    }

    public LightState getGroupState(String id) {
        PHGroup group = groupMap.get(id);
        LightState lightState = null;

        if (group != null) {
            for (String lightId : group.getLightIdentifiers()) {
                lightState = getLightState(lightId);
            }
        }

        return lightState;
    }
}
