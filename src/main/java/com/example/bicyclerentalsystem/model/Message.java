package com.example.bicyclerentalsystem.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int rentalId;
    private int senderId;
    private int receiverId;
    private String transactionId;
    private String messageText;
    private String response;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
    
    // Additional fields for display
    private String senderUsername;
    private String receiverUsername;
    private String bicycleModel;
    
    public Message() {}
    
    public Message(int id, int rentalId, int senderId, int receiverId, String transactionId, 
                   String messageText, String response, LocalDateTime sentAt, LocalDateTime respondedAt) {
        this.id = id;
        this.rentalId = rentalId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.transactionId = transactionId;
        this.messageText = messageText;
        this.response = response;
        this.sentAt = sentAt;
        this.respondedAt = respondedAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }
    
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    
    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }
    
    public String getBicycleModel() { return bicycleModel; }
    public void setBicycleModel(String bicycleModel) { this.bicycleModel = bicycleModel; }
}
