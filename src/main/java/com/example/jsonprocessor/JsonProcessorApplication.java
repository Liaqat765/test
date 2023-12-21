package com.example.jsonprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication
public class JsonProcessorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JsonProcessorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java -jar app.jar <json-file-path> <operation>");
            System.exit(1);
        }

        String filePath = args[0];
        String operation = args[1];

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));

            if ("print".equals(operation)) {
                printJson(jsonNode, "");
            } else if ("findMax".equals(operation)) {
                findMax(jsonNode);
            } else {
                System.err.println("Invalid operation: " + operation);
            }

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    private void printJson(JsonNode jsonNode, String indent) {
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                System.out.println(indent + field.getKey() + ": {");
                printJson(field.getValue(), indent + "    ");
                System.out.println(indent + "}");
            }
        } else if (jsonNode.isArray()) {
            for (JsonNode arrayNode : jsonNode) {
                printJson(arrayNode, indent + "    ");
            }
        } else {
            System.out.println(indent + jsonNode.asText());
        }
    }

    private void findMax(JsonNode jsonNode) {
        Deque<String> path = new ArrayDeque<>();
        findMaxHelper(jsonNode, path);
    }

    private void findMaxHelper(JsonNode jsonNode, Deque<String> path) {
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                path.addLast(field.getKey());
                findMaxHelper(field.getValue(), path);
                path.removeLast();
            }
        } else if (jsonNode.isArray()) {
            int index = 0;
            for (JsonNode arrayNode : jsonNode) {
                path.addLast(String.valueOf(index++));
                findMaxHelper(arrayNode, path);
                path.removeLast();
            }
        } else {
            printMaxValue(path, jsonNode.asText());
        }
    }

    private void printMaxValue(Deque<String> path, String value) {
        System.out.println(String.join(" -> ", path) + ": " + value);
    }
}
