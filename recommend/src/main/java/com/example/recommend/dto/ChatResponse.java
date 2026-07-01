package com.example.recommend.dto;

import java.util.List;
public class ChatResponse {
    private String sessionId;
    private String reply;
    private boolean complete;
    private List<LoanRecommendation> recommendations;
    public ChatResponse() {}
    public ChatResponse(String sessionId, String reply, boolean complete, List<LoanRecommendation> recommendations) {
        this.sessionId = sessionId;
        this.reply = reply;
        this.complete = complete;
        this.recommendations = recommendations;
    }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public boolean isComplete() { return complete; }
    public void setComplete(boolean complete) { this.complete = complete; }
    public List<LoanRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<LoanRecommendation> recommendations) { this.recommendations = recommendations; }
}