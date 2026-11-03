package dio.speech.shared.domain;

import java.util.UUID;

public final class UniqueId {
    private UniqueId() {}

    public static UUID generate() {
        return UUID.randomUUID();
    }
}
