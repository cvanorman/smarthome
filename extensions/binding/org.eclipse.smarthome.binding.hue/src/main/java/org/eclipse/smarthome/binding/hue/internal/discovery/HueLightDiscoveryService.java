/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.internal.discovery;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.binding.hue.handler.CacheUpdatedListener;
import org.eclipse.smarthome.binding.hue.handler.HueBridgeHandler;
import org.eclipse.smarthome.binding.hue.internal.HueCache;
import org.eclipse.smarthome.binding.hue.internal.HueGroup;
import org.eclipse.smarthome.binding.hue.internal.HueItem;
import org.eclipse.smarthome.binding.hue.internal.HueLight;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * The {@link HueBridgeServiceTracker} tracks for hue lights which are connected
 * to a paired hue bridge. The default search time for hue is 60 seconds.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Andre Fuechsel - changed search timeout, changed discovery result creation to support generic thing types
 * @author Thomas Höfer - Added representation
 */
public class HueLightDiscoveryService extends AbstractDiscoveryService
        implements CacheUpdatedListener, PHLightListener {

    // private final Logger logger = LoggerFactory.getLogger(HueLightDiscoveryService.class);

    private final static int SEARCH_TIME = 60;

    private HueBridgeHandler hueBridgeHandler;

    public HueLightDiscoveryService(HueBridgeHandler hueBridgeHandler) {
        super(SEARCH_TIME);
        this.hueBridgeHandler = hueBridgeHandler;
    }

    public void activate() {
        hueBridgeHandler.registerCacheUpdatedListener(this);
    }

    @Override
    public void deactivate() {
        removeOlderResults(new Date().getTime());
        hueBridgeHandler.unregisterCacheUpdatedListener(this);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return Sets.union(HueGroup.getSupportedThingTypes(), HueLight.getSupportedThingTypes());
    }

    @Override
    public void startScan() {
        // search for unpaired lights
        hueBridgeHandler.startSearch(this);
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    private void onHueItemAddedInternal(HueItem hueItem) {
        ThingUID bridgeUID = hueBridgeHandler.getThing().getUID();
        ThingTypeUID thingTypeUID = hueItem.getThingTypeUID();
        ThingUID thingUID = new ThingUID(thingTypeUID, bridgeUID, hueItem.getId());
        Map<String, Object> properties = Maps.<String, Object> newHashMap(hueItem.getProperties());
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID)
                .withProperties(properties).withBridge(bridgeUID).withLabel(hueItem.getName()).build();

        thingDiscovered(discoveryResult);
    }

    @Override
    public void onError(int code, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStateUpdate(Map<String, String> successAttribute, List<PHHueError> errorAttribute) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceivingLightDetails(PHLight light) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceivingLights(List<PHBridgeResource> lights) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSearchComplete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void cacheUpdated(HueCache cache) {
        // removeOlderResults(System.currentTimeMillis());
        Collection<PHLight> lights = hueBridgeHandler.getFullLights();
        Collection<PHGroup> groups = hueBridgeHandler.getFullGroups();

        for (PHLight l : lights) {
            onHueItemAddedInternal(new HueLight(l));
        }

        if (groups != null) {
            for (PHGroup g : groups) {
                onHueItemAddedInternal(new HueGroup(g));
            }
        }
    }
}
