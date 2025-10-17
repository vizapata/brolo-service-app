package co.ateunti.brolo.client.service;

import co.ateunti.brolo.client.model.StatusType;

public interface PublisherService {
    void sendMessage(StatusType type);
}
