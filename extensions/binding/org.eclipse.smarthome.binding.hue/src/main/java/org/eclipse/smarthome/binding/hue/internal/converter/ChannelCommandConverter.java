package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public interface ChannelCommandConverter {

    String getChannelId();

    LightState convert(Command command);

    State convert(LightState lightState);
}
