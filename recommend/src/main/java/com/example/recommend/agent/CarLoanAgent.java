package com.example.recommend.agent;
import com.example.recommend.dto.ChatResponse;
import com.example.recommend.dto.LoanRecommendation;
import com.example.recommend.entity.ConversationSession;
import com.example.recommend.llm.LlmClient;
import com.example.recommend.repository.ConversationSessionRepository;
import com.example.recommend.service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Orchestrates the car-loan recommendation conversation.
 * Step 1: Understand user goal — ask the 7 relevant slot-filling questions.
 * Step 2: When all required slots are filled, compute recommendations.
 */
@Component
public class CarLoanAgent {
    private static final String SYSTEM_PROMPT =
            "You are a helpful Car Loan Recommendation assistant for the Indian market.\n" +
                    "Your job is to collect the user's requirements through a friendly conversation\n" +
                    "and then recommend the best car loan options.\n" +
                    "You MUST collect these slots, one or two questions at a time:\n" +
                    "  1. carModel       - Which car are you planning to buy?\n" +
                    "  2. carPrice       - On-road price of the car (in INR; accept \"15 lakh\" = 1500000).\n" +
                    "  3. carType        - New or Used? (and segment if relevant)\n" +
                    "  4. downPayment    - How much down payment can you make?\n" +
                    "  5. monthlyIncome  - What is your monthly income?\n" +
                    "  6. preferredEmi   - What is your preferred monthly EMI?\n" +
                    "  7. tenureMonths   - Preferred loan tenure in months (e.g. 36, 60, 84).\n" +
                    "  8. creditScore    - Credit score, if available (optional).\n" +
                    "\n" +
                    "After EVERY user message, respond in STRICT JSON with this shape and nothing else:\n" +
                    "{\n" +
                    "  \"extracted\": {\n" +
                    "    \"carModel\": string|null,\n" +
                    "    \"carPrice\": number|null,\n" +
                    "    \"carType\": string|null,\n" +
                    "    \"downPayment\": number|null,\n" +
                    "    \"monthlyIncome\": number|null,\n" +
                    "    \"preferredEmi\": number|null,\n" +
                    "    \"tenureMonths\": number|null,\n" +
                    "    \"creditScore\": number|null\n" +
                    "  },\n" +
                    "  \"reply\": \"your next message to the user — either the next question or a closing summary\",\n" +
                    "  \"complete\": boolean\n" +
                    "}\n" +
                    "Only set complete=true when carModel, carPrice, downPayment, monthlyIncome,\n" +
                    "preferredEmi, and tenureMonths are all known. creditScore is optional.\n" +
                    "Never include text outside the JSON object.";
    private final LlmClient llm;
    private final ConversationSessionRepository sessions;
    private final RecommendationService recommendationService;
    private final ObjectMapper mapper = new ObjectMapper();
    public CarLoanAgent(LlmClient llm,
                        ConversationSessionRepository sessions,
                        RecommendationService recommendationService) {
        this.llm = llm;
        this.sessions = sessions;
        this.recommendationService = recommendationService;
    }
    public ChatResponse handle(String sessionId, String userMessage) throws JsonMappingException, JsonProcessingException {
        ConversationSession session = (sessionId == null)
            ? new ConversationSession()
            : sessions.findById(sessionId).orElseGet(ConversationSession::new);
        UserProfile profile = loadProfile(session);
        List<LlmClient.ChatTurn> history = loadHistory(session);
        history.add(new LlmClient.ChatTurn("user", userMessage));
        String raw = llm.chat(SYSTEM_PROMPT, history);
        AgentJson parsed = parse(raw);
        mergeProfile(profile, parsed.extracted);
        history.add(new LlmClient.ChatTurn("assistant", parsed.reply));
        List<LoanRecommendation> recs = null;
        boolean complete = parsed.complete && allRequiredFilled(profile);
        if (complete) {
            recs = recommendationService.recommend(profile);
        }
        persist(session, profile, history);
        return new ChatResponse(session.getId(), parsed.reply, complete, recs);
    }
    /* ---------- helpers ---------- */
    private UserProfile loadProfile(ConversationSession s) {
        try {
            return s.getContextJson() == null
                ? new UserProfile()
                : mapper.readValue(s.getContextJson(), UserProfile.class);
        } catch (Exception e) {
            return new UserProfile();
        }
    }
    private List<LlmClient.ChatTurn> loadHistory(ConversationSession s) {
        try {
            if (s.getHistoryJson() == null) return new ArrayList<>();
            return mapper.readValue(s.getHistoryJson(), new TypeReference<List<LlmClient.ChatTurn>>() {});

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    private void persist(ConversationSession s, UserProfile p, List<LlmClient.ChatTurn> h) {
        try {
            s.setContextJson(mapper.writeValueAsString(p));
            s.setHistoryJson(mapper.writeValueAsString(h));
            s.setUpdatedAt(Instant.now());
            sessions.save(s);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist session", e);
        }
    }
    private AgentJson parse(String raw) {
        try {
            // Be defensive: strip any code fences or stray text.
            String json = raw.trim();
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start >= 0 && end > start) json = json.substring(start, end + 1);
            return mapper.readValue(json, AgentJson.class);
        } catch (Exception e) {
            AgentJson fallback = new AgentJson();
            fallback.reply = raw;
            fallback.complete = false;
            fallback.extracted = new UserProfile();
            return fallback;
        }
    }
    private void mergeProfile(UserProfile dst, UserProfile src) {
        if (src == null) return;
        if (src.getCarModel() != null)      dst.setCarModel(src.getCarModel());
        if (src.getCarPrice() != null)      dst.setCarPrice(src.getCarPrice());
        if (src.getCarType() != null)       dst.setCarType(src.getCarType());
        if (src.getDownPayment() != null)   dst.setDownPayment(src.getDownPayment());
        if (src.getMonthlyIncome() != null) dst.setMonthlyIncome(src.getMonthlyIncome());
        if (src.getPreferredEmi() != null)  dst.setPreferredEmi(src.getPreferredEmi());
        if (src.getTenureMonths() != null)  dst.setTenureMonths(src.getTenureMonths());
        if (src.getCreditScore() != null)   dst.setCreditScore(src.getCreditScore());
    }
    private boolean allRequiredFilled(UserProfile p) {
        return Optional.ofNullable(p.getCarModel()).isPresent()
            && p.getCarPrice() != null
            && p.getDownPayment() != null
            && p.getMonthlyIncome() != null
            && p.getPreferredEmi() != null
            && p.getTenureMonths() != null;
    }
    /** Wire shape returned by the LLM. */
    public static class AgentJson {
        public UserProfile extracted;
        public String reply;
        public boolean complete;
    }
}
