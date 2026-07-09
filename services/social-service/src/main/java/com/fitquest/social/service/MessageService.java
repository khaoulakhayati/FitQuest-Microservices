package com.fitquest.social.service;

import com.fitquest.social.dto.MessageDto;
import com.fitquest.social.dto.SendMessageRequest;
import com.fitquest.social.entity.Message;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.exception.BadRequestException;
import com.fitquest.social.exception.NotFoundException;
import com.fitquest.social.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    public List<MessageDto> conversation(Long userId, Long peerId) {
        List<Message> outbound = messageRepository.findBySenderIdAndReceiverIdOrderBySentAtAsc(userId, peerId);
        List<Message> inbound = messageRepository.findByReceiverIdAndSenderIdOrderBySentAtAsc(userId, peerId);

        List<Message> merged = new ArrayList<>(outbound.size() + inbound.size());
        merged.addAll(outbound);
        merged.addAll(inbound);
        merged.sort(Comparator.comparing(Message::getSentAt));

        return merged.stream().map(this::toDto).toList();
    }

    public MessageDto send(Long senderId, SendMessageRequest request) {
        if (senderId.equals(request.receiverId())) {
            throw new BadRequestException("Cannot message yourself");
        }

        Message message = messageRepository.save(Message.builder()
                .senderId(senderId)
                .receiverId(request.receiverId())
                .content(request.content())
                .read(false)
                .sentAt(Instant.now())
                .build());

        notificationService.createFromEvent(
                request.receiverId(),
                NotificationType.MESSAGE_RECEIVED,
                "New message",
                "You have a new message from user " + senderId,
                Map.of("messageId", message.getId(), "senderId", senderId),
                ActivityType.MESSAGE,
                "Message from user " + senderId
        );

        return toDto(message);
    }

    public MessageDto markRead(Long userId, String messageId) {
        Message message = messageRepository.findById(messageId)
                .filter(m -> userId.equals(m.getReceiverId()))
                .orElseThrow(() -> new NotFoundException("Message not found: " + messageId));
        message.setRead(true);
        return toDto(messageRepository.save(message));
    }

    public long unreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.isRead(),
                message.getSentAt()
        );
    }
}
