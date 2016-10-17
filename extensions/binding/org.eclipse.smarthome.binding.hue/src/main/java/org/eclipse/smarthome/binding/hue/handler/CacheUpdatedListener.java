package org.eclipse.smarthome.binding.hue.handler;

import org.eclipse.smarthome.binding.hue.internal.HueCache;

public interface CacheUpdatedListener {

    void cacheUpdated(HueCache cache);
}
