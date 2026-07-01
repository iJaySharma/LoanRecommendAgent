\# Car Loan Recommendation — Spring Boot AI Agent

Spring Boot 3 + Java 17 implementation of the Car Loan Recommendation AI agent

described in the requirements (Step 1: understand the user's goal and ask the 7

relevant slot-filling questions, then recommend loans and compute EMI).

\## Architecture

```

src/main/java/com/example/carloan/

├── CarLoanRecommendationApplication.java   # Spring Boot entry point

├── config/

│   ├── LlmProperties.java                  # @ConfigurationProperties for LLM

│   └── RestClientConfig.java               # RestTemplate bean

├── controller/

│   ├── CarLoanController.java              # POST /api/car-loan/chat

│   └── GlobalExceptionHandler.java

├── service/

│   ├── EmiCalculator.java                  # Standard EMI formula

│   └── RecommendationService.java          # Filters offers, ranks by EMI

├── agent/

│   ├── CarLoanAgent.java                   # Orchestrates LLM + slot filling

│   └── UserProfile.java                    # Collected slots

├── llm/

│   ├── LlmClient.java                      # Provider-agnostic interface

│   └── OpenAiLlmClient.java                # OpenAI-compatible REST client

├── repository/

│   ├── LoanOfferRepository.java            # JPA repo (loan\_offer)

│   └── ConversationSessionRepository.java  # JPA repo (conversation\_session)

├── entity/

│   ├── LoanOffer.java                      # @Entity

│   └── ConversationSession.java            # @Entity (stores slots + history)

└── dto/

&nbsp;   ├── ChatRequest.java

&nbsp;   ├── ChatResponse.java

&nbsp;   └── LoanRecommendation.java

src/main/resources/

├── application.yml                          # H2 by default, Postgres-ready

└── db/data.sql                              # Seed bank loan offers

```

\## Run

```bash

cd spring-boot

export LLM\_API\_KEY=sk-...          # OpenAI-compatible key

mvn spring-boot:run

```

H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:carloan`).

\## API

`POST /api/car-loan/chat`

```json

{ "sessionId": null, "message": "I want to buy a car worth 15 lakh" }

```

Response:

```json

{

&nbsp; "sessionId": "uuid",

&nbsp; "reply": "Great! Which car model are you planning to buy?",

&nbsp; "complete": false,

&nbsp; "recommendations": null

}

```

When the agent has all 7 slots, `complete: true` and `recommendations` is

populated with ranked bank offers (EMI, total interest, processing fee).

\## Configuration

Environment variables:

| Var | Default | Purpose |

|---|---|---|

| `LLM\_PROVIDER` | `openai` | Reserved for future providers |

| `LLM\_API\_KEY` | \_(required)\_ | Bearer token for the LLM |

| `LLM\_BASE\_URL` | `https://api.openai.com/v1` | OpenAI-compatible base URL |

| `LLM\_MODEL` | `gpt-4o-mini` | Model name |

Swap to Postgres by overriding `spring.datasource.\*` in `application.yml`.

