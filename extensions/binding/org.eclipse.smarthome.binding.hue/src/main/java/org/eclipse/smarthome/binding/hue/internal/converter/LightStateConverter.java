/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.internal.converter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

/**
 * The {@link LightStateConverter} is responsible for mapping Eclipse SmartHome
 * types to jue types and vice versa.
 *
 * @author Dennis Nobel - Initial contribution
 * @author Oliver Libutzki - Adjustments
 * @author Kai Kreuzer - made code static
 * @author Andre Fuechsel - added method for brightness
 * @author Yordan Zhelev - added method for alert
 *
 */
public class LightStateConverter {

    private Map<String, ChannelCommandConverter> converters = new HashMap<>();

    // private Logger logger = LoggerFactory.getLogger(LightStateConverter.class);
    private boolean isOsramPar16 = false;

    public LightStateConverter() {

        converters.put(HueBindingConstants.CHANNEL_SWITCH, new SwitchConverter());
        converters.put(HueBindingConstants.CHANNEL_EFFECT, new EffectConverter());
        converters.put(HueBindingConstants.CHANNEL_COLORTEMPERATURE, new ColorTemperatureConverter());
        converters.put(HueBindingConstants.CHANNEL_COLOR, new ColorConverter());
        converters.put(HueBindingConstants.CHANNEL_BRIGHTNESS, new BrightnessConverter());
        converters.put(HueBindingConstants.CHANNEL_ALERT, new AlertConverter());

    }

    public LightState convert(String channelId, Command command) {
        LightState lightState = null;

        if (converters.containsKey(channelId)) {
            lightState = converters.get(channelId).convert(command);
        }

        return lightState;
    }

    public State convert(String channelId, LightState lightState) {
        State state = null;

        if (converters.containsKey(channelId)) {
            state = converters.get(channelId).convert(lightState);
        }

        return state;
    }

    public boolean isOsramPar16() {
        return isOsramPar16;
    }

    public void setOsramPar16(boolean isOsramPar16) {
        this.isOsramPar16 = isOsramPar16;
    }
}
