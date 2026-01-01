package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.Message;
import com.example.bicyclerentalsystem.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessagesController {

    @FXML private TableView<Message> sentMessagesTable;
    @FXML private TableColumn<Message, String> colSentBicycle;
    @FXML private TableColumn<Message, String> colSentTo;
    @FXML private TableColumn<Message, String> colSentTxId;
    @FXML private TableColumn<Message, String> colSentResponse;
    @FXML private TableColumn<Message, String> colSentDate;
    
    @FXML private TableView<Message> receivedMessagesTable;
    @FXML private TableColumn<Message, String> colReceivedBicycle;
    @FXML private TableColumn<Message, String> colReceivedFrom;
    @FXML private TableColumn<Message, String> colReceivedTxId;
    @FXML private TableColumn<Message, String> colReceivedMessage;
    @FXML private TableColumn<Message, String> colReceivedDate;
    
    @FXML private ComboBox<String> rentalDropdown;
    @FXML private TextField txIdField;
    @FXML private TextArea messageArea;
    @FXML private Label sentCountLabel;
    @FXML private Label receivedCountLabel;
    
    private final ObservableList<Message> sentMessages = FXCollections.observableArrayList();
    private final ObservableList<Message> receivedMessages = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Setup sent messages table
        colSentBicycle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        colSentTo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getReceiverUsername()));
        colSentTxId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTransactionId()));
        colSentResponse.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getResponse() != null ? c.getValue().getResponse() : "Pending"));
        colSentDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        
        // Setup received messages table
        colReceivedBicycle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        colReceivedFrom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSenderUsername()));
        colReceivedTxId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTransactionId()));
        colReceivedMessage.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMessageText()));
        colReceivedDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        
        loadActiveRentals();
        loadSentMessages();
        loadReceivedMessages();
    }
    
    private void loadActiveRentals() {
        rentalDropdown.getItems().clear();
        
        String sql = """
            SELECT r.id, b.model, u.username as owner_name
            FROM rentals r
            JOIN bicycles b ON r.bicycle_id = b.id
            JOIN users u ON b.owner_id = u.id
            WHERE r.user_id = ? AND r.status = 'pending'
            """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getInt("id") + " - " + rs.getString("model") + " (Owner: " + rs.getString("owner_name") + ")";
                    rentalDropdown.getItems().add(item);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadSentMessages() {
        sentMessages.clear();
        
        String sql = """
            SELECT m.*, 
                   u_receiver.username as receiver_name,
                   b.model as bike_model
            FROM messages m
            JOIN users u_receiver ON m.receiver_id = u_receiver.id
            JOIN rentals r ON m.rental_id = r.id
            JOIN bicycles b ON r.bicycle_id = b.id
            WHERE m.sender_id = ?
            ORDER BY m.sent_at DESC
            """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setRentalId(rs.getInt("rental_id"));
                    msg.setTransactionId(rs.getString("transaction_id"));
                    msg.setMessageText(rs.getString("message_text"));
                    msg.setResponse(rs.getString("response"));
                    msg.setSentAt(LocalDateTime.parse(rs.getString("sent_at")));
                    msg.setReceiverUsername(rs.getString("receiver_name"));
                    msg.setBicycleModel(rs.getString("bike_model"));
                    sentMessages.add(msg);
                }
            }
            
            sentMessagesTable.setItems(sentMessages);
            
            // Update count label
            if (sentCountLabel != null) {
                sentCountLabel.setText("(" + sentMessages.size() + ")");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadReceivedMessages() {
        receivedMessages.clear();
        
        String sql = """
            SELECT m.*, 
                   u_sender.username as sender_name,
                   COALESCE(b.model, 'Overdue Payment') as bike_model
            FROM messages m
            JOIN users u_sender ON m.sender_id = u_sender.id
            LEFT JOIN rentals r ON m.rental_id = r.id
            LEFT JOIN bicycles b ON r.bicycle_id = b.id
            WHERE m.receiver_id = ? AND m.response IS NULL
            ORDER BY m.sent_at DESC
            """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setRentalId(rs.getInt("rental_id"));
                    msg.setTransactionId(rs.getString("transaction_id"));
                    msg.setMessageText(rs.getString("message_text"));
                    msg.setSentAt(LocalDateTime.parse(rs.getString("sent_at")));
                    msg.setSenderUsername(rs.getString("sender_name"));
                    msg.setBicycleModel(rs.getString("bike_model"));
                    receivedMessages.add(msg);
                }
            }
            
            receivedMessagesTable.setItems(receivedMessages);
            
            // Update count label
            if (receivedCountLabel != null) {
                receivedCountLabel.setText("(" + receivedMessages.size() + " pending)");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void sendMessage() {
        // Validate all fields
        if (rentalDropdown.getSelectionModel().isEmpty()) {
            showAlert("No Rental Selected", "Please select an active rental from the dropdown.", Alert.AlertType.WARNING);
            return;
        }
        
        if (txIdField.getText().trim().isEmpty()) {
            showAlert("Missing Transaction ID", "Please enter a transaction ID (e.g., TX:12345).", Alert.AlertType.WARNING);
            txIdField.requestFocus();
            return;
        }
        
        if (messageArea.getText().trim().isEmpty()) {
            showAlert("Empty Message", "Please write a message to the bicycle owner.", Alert.AlertType.WARNING);
            messageArea.requestFocus();
            return;
        }
        
        try {
            int rentalId = Integer.parseInt(rentalDropdown.getValue().split(" - ")[0]);
            String txId = txIdField.getText().trim();
            String message = messageArea.getText().trim();
            
            // Validate transaction ID format
            if (!txId.toUpperCase().startsWith("TX:") && !txId.toUpperCase().startsWith("TX")) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Transaction ID Format");
                confirmAlert.setHeaderText("Unusual Transaction ID Format");
                confirmAlert.setContentText("Transaction ID should typically start with 'TX:'. Do you want to continue anyway?");
                if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }
            }
            
            // Get owner ID from the rental
            String getOwnerSql = "SELECT b.owner_id, u.username FROM rentals r JOIN bicycles b ON r.bicycle_id = b.id JOIN users u ON b.owner_id = u.id WHERE r.id = ?";
            
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement ps = conn.prepareStatement(getOwnerSql)) {
                
                ps.setInt(1, rentalId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    int ownerId = rs.getInt("owner_id");
                    String ownerName = rs.getString("username");
                    
                    // Check if user is trying to message themselves
                    if (ownerId == UserSession.getUserId()) {
                        showAlert("Invalid Action", "You cannot send a message to yourself.", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    // Insert message
                    String insertSql = "INSERT INTO messages(rental_id, sender_id, receiver_id, transaction_id, message_text, sent_at) VALUES(?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, rentalId);
                        insertPs.setInt(2, UserSession.getUserId());
                        insertPs.setInt(3, ownerId);
                        insertPs.setString(4, txId);
                        insertPs.setString(5, message);
                        insertPs.setString(6, LocalDateTime.now().toString());
                        insertPs.executeUpdate();
                        
                        showAlert("Message Sent! ✅", 
                                 "Your message has been successfully sent to " + ownerName + ".\n\n" +
                                 "Transaction ID: " + txId + "\n" +
                                 "They will be notified and can respond with payment status.", 
                                 Alert.AlertType.INFORMATION);
                        
                        // Clear fields
                        rentalDropdown.getSelectionModel().clearSelection();
                        txIdField.clear();
                        messageArea.clear();
                        
                        // Reload messages
                        loadSentMessages();
                        loadActiveRentals();
                    }
                } else {
                    showAlert("Rental Not Found", "The selected rental could not be found.", Alert.AlertType.ERROR);
                }
            }
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Selection", "Please select a valid rental from the dropdown.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to send message: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    public void respondYes() {
        respondToMessage("Yes");
    }
    
    @FXML
    public void respondNo() {
        respondToMessage("No");
    }
    
    @FXML
    public void sendOverduePayment() {
        // Check if user has overdue charges
        double overdueCharges = getOverdueCharges();
        
        if (overdueCharges <= 0) {
            showAlert("No Overdue Charges", 
                     "You don't have any overdue charges to pay.", 
                     Alert.AlertType.INFORMATION);
            return;
        }
        
        // Validate fields
        if (txIdField.getText().trim().isEmpty()) {
            showAlert("Missing Transaction ID", "Please enter a transaction ID for the overdue payment (e.g., TX:12345).", Alert.AlertType.WARNING);
            txIdField.requestFocus();
            return;
        }
        
        String txId = txIdField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (message.isEmpty()) {
            message = "Overdue payment of " + String.format("%.2f", overdueCharges) + " Taka. Transaction ID: " + txId;
        }
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Find the most recent overdue rental to get the bicycle owner
            String findOwnerSql = """
                SELECT DISTINCT b.owner_id, u.username as owner_name, b.model as bike_model, r.id as rental_id
                FROM rentals r
                JOIN bicycles b ON r.bicycle_id = b.id
                JOIN users u ON b.owner_id = u.id
                WHERE r.user_id = ? 
                AND r.return_date IS NOT NULL 
                AND r.due_date IS NOT NULL
                AND r.return_date > r.due_date
                ORDER BY r.return_date DESC
                LIMIT 1
                """;
            
            int ownerId = 0;
            String ownerName = "";
            String bikeModel = "";
            int rentalId = 0;
            
            try (PreparedStatement ps = conn.prepareStatement(findOwnerSql)) {
                ps.setInt(1, UserSession.getUserId());
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    ownerId = rs.getInt("owner_id");
                    ownerName = rs.getString("owner_name");
                    bikeModel = rs.getString("bike_model");
                    rentalId = rs.getInt("rental_id");
                } else {
                    showAlert("No Overdue Rentals Found", 
                             "Could not find the bicycle owner for your overdue charges.\n" +
                             "Please contact support for assistance.", 
                             Alert.AlertType.ERROR);
                    return;
                }
            }
            
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Overdue Payment");
            confirmAlert.setHeaderText("Send Overdue Payment Confirmation");
            confirmAlert.setContentText("Overdue Amount: " + String.format("%.2f", overdueCharges) + " Taka\n" +
                                       "Transaction ID: " + txId + "\n" +
                                       "Bicycle: " + bikeModel + "\n" +
                                       "Owner: " + ownerName + "\n\n" +
                                       "This payment confirmation will be sent to the bicycle owner.\n" +
                                       "Once approved, your overdue charges will be cleared.\n\n" +
                                       "Continue?");
            
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
            
            // Insert overdue payment message with the rental_id of the overdue rental
            String insertSql = "INSERT INTO messages(rental_id, sender_id, receiver_id, transaction_id, message_text, sent_at) VALUES(?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, rentalId); // Use the actual rental_id that caused the overdue
                insertPs.setInt(2, UserSession.getUserId());
                insertPs.setInt(3, ownerId); // Send to the bicycle owner
                insertPs.setString(4, txId);
                insertPs.setString(5, "[OVERDUE PAYMENT] " + message + " | Amount: " + String.format("%.2f", overdueCharges) + " Taka");
                insertPs.setString(6, LocalDateTime.now().toString());
                insertPs.executeUpdate();
                
                showAlert("Payment Confirmation Sent! ✅", 
                         "Your overdue payment confirmation has been sent to " + ownerName + ".\n\n" +
                         "Amount: " + String.format("%.2f", overdueCharges) + " Taka\n" +
                         "Transaction ID: " + txId + "\n" +
                         "Bicycle: " + bikeModel + "\n\n" +
                         "Once the owner approves your payment, your overdue charges will be cleared and you can rent bicycles again.", 
                         Alert.AlertType.INFORMATION);
                
                // Clear fields
                txIdField.clear();
                messageArea.clear();
                
                // Reload messages
                loadSentMessages();
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to send overdue payment confirmation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private double getOverdueCharges() {
        String sql = "SELECT COALESCE(overdue_charges, 0) as charges FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("charges");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    private void respondToMessage(String response) {
        Message selectedMessage = receivedMessagesTable.getSelectionModel().getSelectedItem();
        
        if (selectedMessage == null) {
            showAlert("No Message Selected", 
                     "Please select a message from the table above to respond to.", 
                     Alert.AlertType.WARNING);
            return;
        }
        
        // Confirm the response
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Response");
        confirmAlert.setHeaderText("Send Response to " + selectedMessage.getSenderUsername());
        
        // Check if this is an overdue payment message (check message text for "[OVERDUE PAYMENT]")
        boolean isOverduePayment = selectedMessage.getMessageText() != null && 
                                   selectedMessage.getMessageText().contains("[OVERDUE PAYMENT]");
        String contentText;
        
        if (isOverduePayment) {
            contentText = "Transaction ID: " + selectedMessage.getTransactionId() + "\n" +
                         "Message: " + selectedMessage.getMessageText() + "\n\n" +
                         "Response: " + response + "\n\n";
            if (response.equals("Yes")) {
                contentText += "⚠️ This will CLEAR the sender's overdue charges.\n\n";
            }
            contentText += "Are you sure you want to send this response?";
        } else {
            contentText = "Transaction ID: " + selectedMessage.getTransactionId() + "\n" +
                         "Bicycle: " + selectedMessage.getBicycleModel() + "\n\n" +
                         "Response: " + response + "\n\n" +
                         "Are you sure you want to send this response?";
        }
        
        confirmAlert.setContentText(contentText);
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        
        String updateMessageSql = "UPDATE messages SET response = ?, responded_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(updateMessageSql)) {
                ps.setString(1, response);
                ps.setString(2, LocalDateTime.now().toString());
                ps.setInt(3, selectedMessage.getId());
                ps.executeUpdate();
                
                // If response is Yes, check if it's an overdue payment or rental approval
                if (response.equals("Yes")) {
                    // Use the isOverduePayment variable already declared above
                    if (isOverduePayment) {
                        // This is an overdue payment message - clear the sender's overdue charges
                        String clearOverdueSql = "UPDATE users SET overdue_charges = 0 WHERE id = (SELECT sender_id FROM messages WHERE id = ?)";
                        try (PreparedStatement clearPs = conn.prepareStatement(clearOverdueSql)) {
                            clearPs.setInt(1, selectedMessage.getId());
                            clearPs.executeUpdate();
                        }
                    } else {
                        // This is a regular rental approval - activate the rental
                        String getBicycleAndUserIdSql = "SELECT bicycle_id, user_id FROM rentals WHERE id = ?";
                        String checkActiveRentalSql = "SELECT COUNT(*) as count FROM rentals WHERE user_id = ? AND status = 'active' AND return_date IS NULL";
                        String updateRentalSql = "UPDATE rentals SET status = 'active' WHERE id = ?";
                        String invalidateThisRentalSql = "UPDATE rentals SET status = 'invalid' WHERE id = ?";
                        String updateBicycleSql = "UPDATE bicycles SET isAvailable = 0 WHERE id = ?";
                        String invalidateOtherRequestsSql = "UPDATE rentals SET status = 'invalid' WHERE user_id = ? AND id != ? AND status = 'pending'";
                        
                        // Get bicycle ID and user ID from rental
                        int bicycleId = 0;
                        int userId = 0;
                        try (PreparedStatement getBikePs = conn.prepareStatement(getBicycleAndUserIdSql)) {
                            getBikePs.setInt(1, selectedMessage.getRentalId());
                            ResultSet rs = getBikePs.executeQuery();
                            if (rs.next()) {
                                bicycleId = rs.getInt("bicycle_id");
                                userId = rs.getInt("user_id");
                            }
                        }
                        
                        // Check if user already has an active rental
                    boolean hasActiveRental = false;
                    if (userId > 0) {
                        try (PreparedStatement checkPs = conn.prepareStatement(checkActiveRentalSql)) {
                            checkPs.setInt(1, userId);
                            ResultSet rs = checkPs.executeQuery();
                            if (rs.next()) {
                                hasActiveRental = rs.getInt("count") > 0;
                            }
                        }
                    }
                    
                    if (hasActiveRental) {
                        // User already has an active rental, invalidate this request
                        try (PreparedStatement invalidatePs = conn.prepareStatement(invalidateThisRentalSql)) {
                            invalidatePs.setInt(1, selectedMessage.getRentalId());
                            invalidatePs.executeUpdate();
                        }
                        
                        conn.commit();
                        
                        showAlert("Request No Longer Valid ⚠️", 
                                 "This rental request cannot be approved because " + selectedMessage.getSenderUsername() + 
                                 " already has an active rental.\n\n" +
                                 "The request has been marked as invalid.\n" +
                                 "Transaction ID: " + selectedMessage.getTransactionId(), 
                                 Alert.AlertType.WARNING);
                        
                        loadReceivedMessages();
                        receivedMessagesTable.getSelectionModel().clearSelection();
                        return;
                    }
                    
                    // Update rental status to active
                    try (PreparedStatement updateRentalPs = conn.prepareStatement(updateRentalSql)) {
                        updateRentalPs.setInt(1, selectedMessage.getRentalId());
                        updateRentalPs.executeUpdate();
                    }
                    
                    // Mark bicycle as unavailable
                    if (bicycleId > 0) {
                        try (PreparedStatement updateBikePs = conn.prepareStatement(updateBicycleSql)) {
                            updateBikePs.setInt(1, bicycleId);
                            updateBikePs.executeUpdate();
                        }
                    }
                    
                    // Invalidate all other pending rental requests for this user
                    if (userId > 0) {
                        try (PreparedStatement invalidatePs = conn.prepareStatement(invalidateOtherRequestsSql)) {
                            invalidatePs.setInt(1, userId);
                            invalidatePs.setInt(2, selectedMessage.getRentalId());
                            int invalidatedCount = invalidatePs.executeUpdate();
                            if (invalidatedCount > 0) {
                                System.out.println("Invalidated " + invalidatedCount + " other pending rental requests for user " + userId);
                            }
                        }
                    }
                    }  // End of else block for rental approval
                }
                
                conn.commit();
                
                String icon = response.equals("Yes") ? "✅" : "❌";
                String additionalMsg = "";
                
                if (response.equals("Yes")) {
                    // Use the isOverduePayment variable already declared above
                    if (isOverduePayment) {
                        additionalMsg = "\n\nThe user's overdue charges have been cleared and they can now rent bicycles again.";
                    } else {
                        additionalMsg = "\n\nThe bicycle has been added to the user's rental list.";
                    }
                }
                
                showAlert("Response Sent! " + icon, 
                         "Your response has been sent to " + selectedMessage.getSenderUsername() + ".\n\n" +
                         "Response: " + response + "\n" +
                         "Transaction ID: " + selectedMessage.getTransactionId() + additionalMsg, 
                         Alert.AlertType.INFORMATION);
                
                loadReceivedMessages();
                receivedMessagesTable.getSelectionModel().clearSelection();
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send response: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void backToDashboard() {
        try {
            // Load dashboard content
            Pane view = FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/dashboard_content.fxml"));
            
            // Get the root BorderPane from the scene (dashboard_view.fxml)
            BorderPane root = (BorderPane) sentMessagesTable.getScene().getRoot();
            
            // Get the contentPane which is the center of the root
            Object center = root.getCenter();
            if (center instanceof BorderPane) {
                // contentPane is still a BorderPane
                ((BorderPane) center).setCenter(view);
            } else {
                // contentPane was replaced, so set view directly to root's center
                root.setCenter(view);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
