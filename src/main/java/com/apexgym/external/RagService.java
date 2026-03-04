/*
package com.apexgym.external;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    public String queryWithContext(String question) {
        // 1. Convert question to embedding
        float[] embedding = embeddingModel.embed(question);

        // 2. Search similar documents
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(5)
        );

        // 3. Build context
        String context = similarDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        // 4. Query with context
        String prompt = String.format("""
            Context:
            %s
            
            Question: %s
            
            Answer based on the context above.
            """, context, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
*/
