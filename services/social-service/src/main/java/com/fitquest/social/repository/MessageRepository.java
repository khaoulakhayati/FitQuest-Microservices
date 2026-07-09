package com.fitquest.social.repository;

import com.fitquest.social.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findBySenderIdAndReceiverIdOrderBySentAtAsc(Long senderId, Long receiverId);

    List<Message> findByReceiverIdAndSenderIdOrderBySentAtAsc(Long receiverId, Long senderId);

    List<Message> findByReceiverIdAndReadFalse(Long receiverId);

    long countByReceiverIdAndReadFalse(Long receiverId);
}
