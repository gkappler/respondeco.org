package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the TextMessage entity.
 */
public interface TextMessageRepository extends JpaRepository<TextMessage, Long> {

    List<TextMessage> findByReceiverAndActiveIsTrue(User receiver);
    Long countByReceiverAndActiveIsTrueAndReadIsFalse(User receiver);

}
