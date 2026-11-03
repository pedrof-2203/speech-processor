package dio.speech.domain;

import dio.speech.shared.domain.UniqueId;

import java.util.UUID;

public record TransactionId(UUID uuid) {
    public TransactionId() {
        this(UniqueId.generate());
    }
}
