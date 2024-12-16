package org.by1337.bmenuparser.event;


@FunctionalInterface
public interface SoundEventListener {
    void on(SoundEvent event);
}
